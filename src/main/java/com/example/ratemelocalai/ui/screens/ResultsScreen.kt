package com.example.ratemelocalai.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratemelocalai.R
import com.example.ratemelocalai.domain.model.FaceAnalysisResult
import com.example.ratemelocalai.domain.model.MetricStatus
import com.example.ratemelocalai.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResultsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onSettings: () -> Unit
) {
    val result by viewModel.lastResult.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 64.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(6.dp))
                )

                IconButton(
                    onClick = onSettings,
                    modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }

            result?.let { res ->
                // --- SCORE DISPLAY ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularRatingDisplay(score = res.overallScore)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- CAPTURED SAMPLES ---
                SectionHeader("Biometric Samples")
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(res.capturedImages.toList()) { (key, bitmap) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = key,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.05f)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                key,
                                color = Color.White.copy(alpha = 0.3f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- AI ANALYSIS REPORT ---
                SectionHeader("Analysis Insights")
                Column(modifier = Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    res.tips.forEach { tip ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(tip, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, lineHeight = 22.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- DETAILED METRICS ---
                SectionHeader("Metric Breakdown")
                Column(modifier = Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    res.detailedMetrics.forEach { metric ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(20.dp)
                        ) {
                            MetricRow(metric.name, metric.score)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
                
                Button(
                    onClick = { shareReport(context, res) },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text("Export Biometric Protocol", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
                
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        color = Color.White.copy(alpha = 0.4f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp
    )
}

@Composable
fun MetricRow(label: String, score: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text("$score%", color = Color.White.copy(alpha = 0.5f), fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f))) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(score / 100f)
                    .fillMaxHeight()
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun CircularRatingDisplay(score: Int) {
    val animatedScore = remember { Animatable(0f) }
    LaunchedEffect(score) {
        animatedScore.animateTo(score.toFloat(), animationSpec = tween(2000, easing = EaseOutQuart))
    }

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(220.dp)) {
            val strokeWidth = 2.dp.toPx()
            drawCircle(color = Color.White.copy(alpha = 0.05f), style = Stroke(width = strokeWidth))
            
            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = (animatedScore.value / 100f) * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth * 2, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${animatedScore.value.toInt()}", 
                color = Color.White, 
                fontSize = 84.sp, 
                fontWeight = FontWeight.Light,
                letterSpacing = (-2).sp
            )
            Text(
                text = "OVERALL INDEX", 
                color = Color.White.copy(alpha = 0.3f), 
                fontSize = 10.sp, 
                fontWeight = FontWeight.Bold, 
                letterSpacing = 2.sp
            )
        }
    }
}

private fun shareReport(context: Context, result: FaceAnalysisResult) {
    val reportText = """
        ╔══════════════════════════════════════════╗
        ║       FACEHARMONY BIOMETRIC REPORT       ║
        ╚══════════════════════════════════════════╝
        
        DATE: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}
        PROTOCOL ID: FH-${(10000..99999).random()}
        
        [ SUMMARY ]
        Overall Harmony Index: ${result.overallScore}/100
        Classification: ${when {
            result.overallScore > 90 -> "Elite Symmetric Alignment"
            result.overallScore > 80 -> "High Structural Harmony"
            result.overallScore > 70 -> "Standard Aesthetic Proportions"
            else -> "Variational Biometric Data"
        }}
        
        [ METRIC BREAKDOWN ]
        ${result.detailedMetrics.joinToString("\n") { "• ${it.name.padEnd(20)}: ${it.score}%" }}
        
        [ AI DIAGNOSTIC INSIGHTS ]
        ${result.tips.joinToString("\n- ", prefix = "- ")}
        
        --------------------------------------------
        CONFIDENTIAL • ON-DEVICE NEURAL PROCESSING
        GENERATED BY FACEHARMONY AI
        --------------------------------------------
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Biometric Report: ${result.overallScore}/100")
        putExtra(Intent.EXTRA_TEXT, reportText)
    }
    context.startActivity(Intent.createChooser(intent, "Export Protocol"))
}
