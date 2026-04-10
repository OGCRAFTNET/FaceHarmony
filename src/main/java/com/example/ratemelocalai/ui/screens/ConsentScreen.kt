package com.example.ratemelocalai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratemelocalai.ui.components.GlassCard
import com.example.ratemelocalai.ui.components.PremiumButton

@Composable
fun ConsentScreen(onAccepted: () -> Unit) {
    var ageConfirmed by remember { mutableStateOf(false) }
    var privacyAccepted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Gradient background for depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF5E5CE6).copy(alpha = 0.1f),
                            Color.Black
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = "Privacy & Consent",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            GlassCard {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Your Privacy is our Priority",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "FaceHarmony uses on-device AI to analyze your facial structure. No biometric data, images, or analysis results ever leave this device. We do not use cloud processing, and we do not track you.",
                        color = Color.LightGray.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { ageConfirmed = !ageConfirmed }
                    .padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = ageConfirmed,
                    onCheckedChange = { ageConfirmed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF0A84FF),
                        uncheckedColor = Color.Gray.copy(alpha = 0.5f),
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "I confirm that I am at least 13 years old (EU) / 16 years old (USA/Global).",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { privacyAccepted = !privacyAccepted }
                    .padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = privacyAccepted,
                    onCheckedChange = { privacyAccepted = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF0A84FF),
                        uncheckedColor = Color.Gray.copy(alpha = 0.5f),
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "I agree to the local processing of my facial data for analysis purposes.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(48.dp))

            PremiumButton(
                text = "Continue",
                enabled = ageConfirmed && privacyAccepted,
                onClick = onAccepted,
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            )
        }
    }
}
