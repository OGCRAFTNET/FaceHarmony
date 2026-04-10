package com.example.ratemelocalai.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onNavigate: () -> Unit) {
    val appName = "FACEHARMONY"
    
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.94f) }

    LaunchedEffect(Unit) {
        launch {
            // Ultra-smooth high-end fade
            alphaAnim.animateTo(1f, tween(2000, easing = EaseOutQuart))
            delay(1200)
            alphaAnim.animateTo(0f, tween(1500, easing = EaseInExpo))
        }
        
        launch {
            // Subtle breathing scale
            scaleAnim.animateTo(1.02f, tween(4000, easing = LinearOutSlowInEasing))
        }
        
        delay(4500)
        onNavigate()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                alpha = alphaAnim.value
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            }
        ) {
            Text(
                text = appName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraLight,
                letterSpacing = 22.sp, // Extreme spacing for high-end luxury feel
                modifier = Modifier.padding(start = 22.dp) // Compensate for last letter spacing to center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Minimalist breathing accent line
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(0.5.dp)
                    .background(Color.White.copy(alpha = 0.2f * alphaAnim.value))
            )
        }
    }
}
