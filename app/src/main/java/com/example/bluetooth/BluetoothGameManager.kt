package com.example.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

enum class BluetoothConnectionState {
    DISCONNECTED,
    SEARCHING,
    HOSTING,
    CONNECTING,
    CONNECTED,
    SIMULATED_PEER
}

data class BluetoothDeviceInfo(
    val name: String,
    val address: String,
    val isPaired: Boolean = false
)

class BluetoothGameManager(private val context: Context) {

    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(Context.TELECOM_SERVICE) as? BluetoothManager
            ?: context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

    val adapter: BluetoothAdapter? = bluetoothManager?.adapter ?: BluetoothAdapter.getDefaultAdapter()

    private val _connectionState = MutableStateFlow(BluetoothConnectionState.DISCONNECTED)
    val connectionState: StateFlow<BluetoothConnectionState> = _connectionState.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDeviceInfo>> = _discoveredDevices.asStateFlow()

    private val _receivedPacket = MutableStateFlow<BluetoothPacket?>(null)
    val receivedPacket: StateFlow<BluetoothPacket?> = _receivedPacket.asStateFlow()

    private var activeSocket: BluetoothSocket? = null
    private var serverSocket: BluetoothServerSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val TAG = "BluetoothManager"
        val APP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        const val SERVICE_NAME = "IronAndSteelDuel"
    }

    @SuppressLint("MissingPermission")
    fun refreshPairedDevices() {
        if (adapter == null || !adapter.isEnabled) {
            _discoveredDevices.value = emptyList()
            return
        }
        try {
            val paired = adapter.bondedDevices?.map { dev ->
                BluetoothDeviceInfo(
                    name = dev.name ?: "Unknown Knight",
                    address = dev.address,
                    isPaired = true
                )
            } ?: emptyList()
            _discoveredDevices.value = paired
        } catch (e: Exception) {
            Log.e(TAG, "Error listing paired devices", e)
        }
    }

    @SuppressLint("MissingPermission")
    fun startHosting() {
        if (adapter == null || !adapter.isEnabled) {
            startSimulatedPeer()
            return
        }
        _connectionState.value = BluetoothConnectionState.HOSTING
        scope.launch {
            try {
                serverSocket = adapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, APP_UUID)
                val socket = serverSocket?.accept(30000) // 30 sec timeout
                if (socket != null) {
                    serverSocket?.close()
                    manageConnectedSocket(socket)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Host server exception", e)
                _connectionState.value = BluetoothConnectionState.DISCONNECTED
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(deviceAddress: String) {
        if (adapter == null || !adapter.isEnabled) {
            startSimulatedPeer()
            return
        }
        _connectionState.value = BluetoothConnectionState.CONNECTING
        scope.launch {
            try {
                val device: BluetoothDevice = adapter.getRemoteDevice(deviceAddress)
                val socket = device.createRfcommSocketToServiceRecord(APP_UUID)
                adapter.cancelDiscovery()
                socket.connect()
                manageConnectedSocket(socket)
            } catch (e: Exception) {
                Log.e(TAG, "Client connect exception", e)
                _connectionState.value = BluetoothConnectionState.DISCONNECTED
            }
        }
    }

    fun startSimulatedPeer() {
        _connectionState.value = BluetoothConnectionState.SIMULATED_PEER
        _receivedPacket.value = null
    }

    private fun manageConnectedSocket(socket: BluetoothSocket) {
        activeSocket = socket
        _connectionState.value = BluetoothConnectionState.CONNECTED
        try {
            outputStream = socket.outputStream
            inputStream = socket.inputStream

            // Start Listening Loop
            val buffer = ByteArray(1024)
            while (_connectionState.value == BluetoothConnectionState.CONNECTED) {
                val bytes = inputStream?.read(buffer) ?: -1
                if (bytes > 0) {
                    val rawJson = String(buffer, 0, bytes)
                    parseAndEmitPacket(rawJson)
                } else if (bytes == -1) {
                    break
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Stream reading exception", e)
        } finally {
            disconnect()
        }
    }

    fun sendPacket(packet: BluetoothPacket) {
        if (_connectionState.value == BluetoothConnectionState.SIMULATED_PEER) {
            // Echo or handle in game loop directly
            return
        }
        if (_connectionState.value != BluetoothConnectionState.CONNECTED) return

        scope.launch {
            try {
                val json = JSONObject().apply {
                    put("type", packet.type)
                    put("direction", packet.direction)
                    put("timestamp", packet.timestamp)
                    put("isParryAction", packet.isParryAction)
                    put("senderId", packet.senderId)
                }.toString()
                outputStream?.write(json.toByteArray())
                outputStream?.flush()
            } catch (e: Exception) {
                Log.e(TAG, "Failed sending packet", e)
            }
        }
    }

    private fun parseAndEmitPacket(rawJson: String) {
        try {
            val json = JSONObject(rawJson)
            val packet = BluetoothPacket(
                type = json.optString("type"),
                direction = json.optString("direction", null),
                timestamp = json.optLong("timestamp"),
                isParryAction = json.optBoolean("isParryAction"),
                senderId = json.optString("senderId")
            )
            _receivedPacket.value = packet
        } catch (e: Exception) {
            Log.e(TAG, "JSON parse error", e)
        }
    }

    fun disconnect() {
        try {
            inputStream?.close()
            outputStream?.close()
            activeSocket?.close()
            serverSocket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Disconnect exception", e)
        } finally {
            activeSocket = null
            serverSocket = null
            inputStream = null
            outputStream = null
            _connectionState.value = BluetoothConnectionState.DISCONNECTED
        }
    }
}
