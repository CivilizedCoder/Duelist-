package com.example

import com.example.model.CombatDirection
import org.junit.Assert.assertEquals
import org.junit.Test

class CombatDirectionTest {

    @Test
    fun `joystick up maps to TOP zone`() {
        val dir = CombatDirection.fromJoystickOffset(0f, -0.8f)
        assertEquals(CombatDirection.TOP, dir)
    }

    @Test
    fun `joystick left maps to LEFT zone`() {
        val dir = CombatDirection.fromJoystickOffset(-0.8f, 0f)
        assertEquals(CombatDirection.LEFT, dir)
    }

    @Test
    fun `joystick right maps to RIGHT zone`() {
        val dir = CombatDirection.fromJoystickOffset(0.8f, 0f)
        assertEquals(CombatDirection.RIGHT, dir)
    }
}
