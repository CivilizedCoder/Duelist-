package com.example.bluetooth

import com.example.model.CombatDirection

enum class PacketType {
    GUARD_CHANGE,
    ATTACK_START,
    PARRY_ACTION,
    SYNC_STATE,
    HEARTBEAT
}

data class BluetoothPacket(
    val type: String,
    val direction: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isParryAction: Boolean = false,
    val senderId: String = ""
) {
    companion object {
        fun guardChange(direction: CombatDirection, senderId: String): BluetoothPacket {
            return BluetoothPacket(
                type = PacketType.GUARD_CHANGE.name,
                direction = direction.name,
                senderId = senderId
            )
        }

        fun attackStart(direction: CombatDirection, senderId: String): BluetoothPacket {
            return BluetoothPacket(
                type = PacketType.ATTACK_START.name,
                direction = direction.name,
                senderId = senderId
            )
        }

        fun parryAction(direction: CombatDirection, senderId: String): BluetoothPacket {
            return BluetoothPacket(
                type = PacketType.PARRY_ACTION.name,
                direction = direction.name,
                isParryAction = true,
                senderId = senderId
            )
        }
    }
}
