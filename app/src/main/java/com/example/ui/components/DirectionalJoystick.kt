package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.CombatDirection
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ShieldZoneLeftColor
import com.example.ui.theme.ShieldZoneRightColor
import com.example.ui.theme.ShieldZoneTopColor
import com.example.ui.theme.SteelDark
import com.example.ui.theme.SteelLight
import com.example.ui.theme.SteelMedium
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DirectionalJoystick(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    activeDirection: CombatDirection?,
    onJoystickMoved: (x: Float, y: Float) -> Unit,
    onJoystickReleased: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val animatedX by animateFloatAsState(targetValue = offsetX, label = "joyX")
    val animatedY by animateFloatAsState(targetValue = offsetY, label = "joyY")

    Box(
        modifier = modifier
            .size(size)
            .testTag("directional_joystick")
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val radius = size.toPx() / 2f
                        val dx = (offset.x - radius) / radius
                        val dy = (offset.y - radius) / radius
                        offsetX = dx.coerceIn(-1f, 1f)
                        offsetY = dy.coerceIn(-1f, 1f)
                        onJoystickMoved(offsetX, offsetY)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val radius = size.toPx() / 2f
                        offsetX = (offsetX + dragAmount.x / radius).coerceIn(-1f, 1f)
                        offsetY = (offsetY + dragAmount.y / radius).coerceIn(-1f, 1f)
                        onJoystickMoved(offsetX, offsetY)
                    },
                    onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                        onJoystickReleased()
                    },
                    onDragCancel = {
                        offsetX = 0f
                        offsetY = 0f
                        onJoystickReleased()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val centerOffset = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.width / 2f
            val maxThumbDistance = radius * 0.55f

            // Outer Metallic Ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SteelDark, SteelMedium, SteelLight),
                    center = centerOffset,
                    radius = radius
                ),
                radius = radius,
                center = centerOffset
            )

            // Draw 3 Directional 120-degree Sectors (Top, Left, Right)
            // Sector 1: TOP (Overhead) - centered ~270° in Canvas coordinates (90° up)
            val topActive = activeDirection == CombatDirection.TOP
            val topColor = if (topActive) ShieldZoneTopColor else ShieldZoneTopColor.copy(alpha = 0.25f)
            drawArc(
                color = topColor,
                startAngle = 210f,
                sweepAngle = 120f,
                useCenter = true,
                size = this.size,
                topLeft = Offset.Zero
            )

            // Sector 2: LEFT - centered ~180° in Canvas coordinates
            val leftActive = activeDirection == CombatDirection.LEFT
            val leftColor = if (leftActive) ShieldZoneLeftColor else ShieldZoneLeftColor.copy(alpha = 0.25f)
            drawArc(
                color = leftColor,
                startAngle = 90f,
                sweepAngle = 120f,
                useCenter = true,
                size = this.size,
                topLeft = Offset.Zero
            )

            // Sector 3: RIGHT - centered ~0° in Canvas coordinates
            val rightActive = activeDirection == CombatDirection.RIGHT
            val rightColor = if (rightActive) ShieldZoneRightColor else ShieldZoneRightColor.copy(alpha = 0.25f)
            drawArc(
                color = rightColor,
                startAngle = -30f,
                sweepAngle = 120f,
                useCenter = true,
                size = this.size,
                topLeft = Offset.Zero
            )

            // Inner Ring Border Lines dividing sectors
            val divAngles = listOf(-30f, 90f, 210f)
            for (angleDeg in divAngles) {
                val rad = Math.toRadians(angleDeg.toDouble())
                val endX = centerOffset.x + (radius * cos(rad)).toFloat()
                val endY = centerOffset.y + (radius * sin(rad)).toFloat()
                drawLine(
                    color = SteelLight,
                    start = centerOffset,
                    end = Offset(endX, endY),
                    strokeWidth = 3f
                )
            }

            // Outer Steel Rim Accent
            drawCircle(
                color = GoldPrimary,
                radius = radius - 2f,
                center = centerOffset,
                style = Stroke(width = 4f)
            )

            // Direction Arrow Indicators
            // Top Arrow
            val topArrowPath = Path().apply {
                moveTo(centerOffset.x, centerOffset.y - radius * 0.75f)
                lineTo(centerOffset.x - 12f, centerOffset.y - radius * 0.58f)
                lineTo(centerOffset.x + 12f, centerOffset.y - radius * 0.58f)
                close()
            }
            drawPath(path = topArrowPath, color = if (topActive) Color.White else GoldPrimary.copy(alpha = 0.7f))

            // Left Arrow
            val leftArrowPath = Path().apply {
                moveTo(centerOffset.x - radius * 0.75f, centerOffset.y + radius * 0.45f)
                lineTo(centerOffset.x - radius * 0.55f, centerOffset.y + radius * 0.35f)
                lineTo(centerOffset.x - radius * 0.55f, centerOffset.y + radius * 0.55f)
                close()
            }
            drawPath(path = leftArrowPath, color = if (leftActive) Color.White else GoldPrimary.copy(alpha = 0.7f))

            // Right Arrow
            val rightArrowPath = Path().apply {
                moveTo(centerOffset.x + radius * 0.75f, centerOffset.y + radius * 0.45f)
                lineTo(centerOffset.x + radius * 0.55f, centerOffset.y + radius * 0.35f)
                lineTo(centerOffset.x + radius * 0.55f, centerOffset.y + radius * 0.55f)
                close()
            }
            drawPath(path = rightArrowPath, color = if (rightActive) Color.White else GoldPrimary.copy(alpha = 0.7f))

            // Thumb Pad Handle
            val thumbCenter = Offset(
                x = centerOffset.x + (animatedX * maxThumbDistance),
                y = centerOffset.y + (animatedY * maxThumbDistance)
            )

            val activeColor = when (activeDirection) {
                CombatDirection.TOP -> ShieldZoneTopColor
                CombatDirection.LEFT -> ShieldZoneLeftColor
                CombatDirection.RIGHT -> ShieldZoneRightColor
                else -> GoldPrimary
            }

            // Thumb Knob Outer Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(activeColor, activeColor.copy(alpha = 0.2f), Color.Transparent),
                    center = thumbCenter,
                    radius = radius * 0.38f
                ),
                radius = radius * 0.38f,
                center = thumbCenter
            )

            // Thumb Knob Body
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SteelLight, SteelMedium, SteelDark),
                    center = thumbCenter,
                    radius = radius * 0.25f
                ),
                radius = radius * 0.25f,
                center = thumbCenter
            )

            drawCircle(
                color = activeColor,
                radius = radius * 0.25f,
                center = thumbCenter,
                style = Stroke(width = 3f)
            )

            // Center Steel Stud
            drawCircle(
                color = Color.White,
                radius = 6f,
                center = thumbCenter
            )
        }
    }
}
