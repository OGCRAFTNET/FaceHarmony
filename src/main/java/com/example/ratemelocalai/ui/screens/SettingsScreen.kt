package com.example.ratemelocalai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratemelocalai.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onRestartOnboarding: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier.padding(top = 64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    "Settings", 
                    color = Color.White, 
                    fontSize = 28.sp, 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- APPEARANCE ---
            SettingsSectionTitle("Appearance")
            SettingsCard {
                SettingItem(Icons.Default.Palette, "Theme", "Dark Mode")
                Divider(color = Color.White.copy(alpha = 0.05f), thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingItem(Icons.Default.Notifications, "Notifications", "Analysis Alerts")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- APP FLOW ---
            SettingsSectionTitle("App Experience")
            SettingsCard {
                SettingItem(
                    icon = Icons.Default.Refresh,
                    title = "Restart Onboarding",
                    value = "Reset app state",
                    onClick = onRestartOnboarding
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SYSTEM ---
            SettingsSectionTitle("System")
            SettingsCard {
                SettingItem(Icons.Default.Info, "About", "FaceHarmony v2.1.0")
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        color = Color.White.copy(alpha = 0.4f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f)),
        content = content
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
            }
            Text(title, color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(start = 16.dp))
        }
        Text(value, color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp)
    }
}
