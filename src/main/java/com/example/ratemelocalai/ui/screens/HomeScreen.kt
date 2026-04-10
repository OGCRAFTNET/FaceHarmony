package com.example.ratemelocalai.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratemelocalai.R
import com.example.ratemelocalai.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onStartScan: () -> Unit,
    onSettings: () -> Unit
) {
    val lastResult by viewModel.lastResult.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- ULTRA MINIMAL HEADER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "FaceHarmony",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = (-0.5).sp
                    )
                }
                
                IconButton(
                    onClick = onSettings,
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // --- HERO SCORE SECTION ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = lastResult?.overallScore?.toString() ?: "--",
                        color = Color.White,
                        fontSize = 110.sp,
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = (-4).sp
                    )
                    Text(
                        text = "HARMONY INDEX",
                        color = Color.White.copy(alpha = 0.3f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- INFO GRID ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoTile(
                    modifier = Modifier.weight(1f),
                    label = "Biological Age",
                    value = lastResult?.estimatedAge?.let { "~$it" } ?: "--"
                )
                InfoTile(
                    modifier = Modifier.weight(1f),
                    label = "Processing",
                    value = "On-Device"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- CTABUTTON ---
            Button(
                onClick = onStartScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    "Start Biometric Scan", 
                    color = Color.Black, 
                    fontWeight = FontWeight.SemiBold, 
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun InfoTile(modifier: Modifier, label: String, value: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .padding(20.dp)
    ) {
        Column {
            Text(label, color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
