package com.example.ratemelocalai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.03f)
                    )
                )
            )
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "btn_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .alpha(if (enabled) 1f else 0.6f)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (enabled) {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00E5FF),
                            Color(0xFF0A84FF)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            Color.Gray.copy(alpha = 0.5f),
                            Color.DarkGray.copy(alpha = 0.5f)
                        )
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) { onClick() }
            .padding(vertical = 12.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) Color.Black else Color.LightGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}
