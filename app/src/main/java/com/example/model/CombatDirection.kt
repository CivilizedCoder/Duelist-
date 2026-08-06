package com.example.model

import kotlin.math.atan2

enum class CombatDirection(val label: String, val angleCenter: Float) {
    TOP("Overhead Strike", 90f),
    LEFT("Left Flank Strike", 210f),
    RIGHT("Right Flank Strike", 330f);

    companion object {
        /**
         * Converts joystick (x, y) displacement to 120-degree combat direction zone.
         * In screen coordinates: Y is positive down, X is positive right.
         * Angle 0° is Right, 90° is Down, 180° is Left, 270° (-90°) is Up.
         *
         * Top Zone (Overhead): Upward movement (Y < -0.3, angle ~ 210°..330° in math degrees)
         * Left Zone: Leftward movement (X < -0.3)
         * Right Zone: Rightward movement (X > 0.3)
         */
        fun fromJoystickOffset(x: Float, y: Float, deadzone: Float = 0.25f): CombatDirection? {
            val length = kotlin.math.sqrt((x * x + y * y).toDouble()).toFloat()
            if (length < deadzone) return null

            // Calculate angle in degrees from -180 to 180
            // y is inverted in screen space (-y points UP)
            var angleDeg = Math.toDegrees(atan2(-y.toDouble(), x.toDouble())).toFloat()
            if (angleDeg < 0) angleDeg += 360f // Normalize to 0..360 (0 = Right, 90 = Up, 180 = Left, 270 = Down)

            return when {
                // Top Overhead Zone: 30° to 150° (Upward angle centered at 90°)
                angleDeg in 30.0f..150.0f -> TOP
                // Left Zone: 150° to 270° (Leftward angle centered at 210°)
                angleDeg in 150.0f..270.0f -> LEFT
                // Right Zone: 270° to 360° or 0° to 30° (Rightward angle centered at 330°)
                else -> RIGHT
            }
        }

        fun random(): CombatDirection = entries.toTypedArray().random()
    }
}
