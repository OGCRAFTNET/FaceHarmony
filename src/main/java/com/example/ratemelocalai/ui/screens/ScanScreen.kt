package com.example.ratemelocalai.ui.screens

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ratemelocalai.camera.FaceMeshAnalyzer
import com.example.ratemelocalai.camera.MediaPipeFaceAnalyzer
import com.example.ratemelocalai.domain.model.FaceAnalysisResult
import com.example.ratemelocalai.ui.components.FaceMeshOverlay
import com.example.ratemelocalai.viewmodel.MainViewModel
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import java.util.concurrent.Executors
import kotlin.math.abs

enum class ScanStep(val instruction: String, val yawTarget: Float) {
    FRONT("Front Profile", 0f),
    SLIGHT_LEFT("Tilt Left", 12f),
    LEFT("Full Left", 28f),
    SLIGHT_RIGHT("Tilt Right", -12f),
    RIGHT("Full Right", -28f),
    DONE("Analyzing...", 0f)
}

@Composable
fun ScanScreen(
    viewModel: MainViewModel,
    onResult: (FaceAnalysisResult) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    val analysisOptions by viewModel.analysisOptions.collectAsState()

    var hasCameraPermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }

    var currentStep by remember { mutableStateOf(ScanStep.FRONT) }
    var currentResult by remember { mutableStateOf<FaceLandmarkerResult?>(null) }
    var meshWidth by remember { mutableIntStateOf(0) }
    var meshHeight by remember { mutableIntStateOf(0) }
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    val capturedResults = remember { mutableStateMapOf<String, FaceLandmarkerResult?>() }
    val capturedBitmaps = remember { mutableStateMapOf<String, Bitmap>() }
    var isAnalyzing by remember { mutableStateOf(false) }
    var lastCapturedStep by remember { mutableStateOf<ScanStep?>(null) }
    
    var currentYaw by remember { mutableFloatStateOf(0f) }
    var showMenu by remember { mutableStateOf(true) }

    // Dynamic Blur Animation
    val blurRadius by animateDpAsState(
        targetValue = if (showMenu) 40.dp else 0.dp,
        animationSpec = tween(800, easing = EaseInOutQuart),
        label = "menu_blur"
    )

    LaunchedEffect(currentResult) {
        val pose = FaceMeshAnalyzer.detectPose(currentResult)
        if (pose != null) {
            currentYaw = pose.yaw
        }

        if (!isAnalyzing) return@LaunchedEffect
        
        if (pose != null) {
            val threshold = 7f 
            val diff = abs(pose.yaw - currentStep.yawTarget)
            
            if (diff < threshold && lastCapturedStep != currentStep) {
                capturedResults[currentStep.name] = currentResult
                currentBitmap?.let { capturedBitmaps[currentStep.name] = it }
                lastCapturedStep = currentStep
                
                when (currentStep) {
                    ScanStep.FRONT -> currentStep = ScanStep.SLIGHT_LEFT
                    ScanStep.SLIGHT_LEFT -> currentStep = ScanStep.LEFT
                    ScanStep.LEFT -> currentStep = ScanStep.SLIGHT_RIGHT
                    ScanStep.SLIGHT_RIGHT -> currentStep = ScanStep.RIGHT
                    ScanStep.RIGHT -> {
                        currentStep = ScanStep.DONE
                        isAnalyzing = false
                        val result = FaceMeshAnalyzer.calculateFinalScore(capturedResults.toMap(), capturedBitmaps.toMap())
                        onResult(result)
                    }
                    else -> {}
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            AndroidView(factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val imageAnalysis = androidx.camera.core.ImageAnalysis.Builder()
                        .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor, MediaPipeFaceAnalyzer(ctx) { result, w, h, bitmap ->
                                currentResult = result
                                meshWidth = w
                                meshHeight = h
                                currentBitmap = bitmap
                            })
                        }
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, imageAnalysis)
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }, modifier = Modifier.fillMaxSize().blur(blurRadius))
            
            if (!showMenu) {
                FaceMeshOverlay(currentResult, meshWidth, meshHeight, isFrontCamera = true)
            }
        }

        // Main UI Overlay
        Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
            // Apple Style Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                
                Text("Scan", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)

                Box(modifier = Modifier.size(40.dp)) // Spacer
            }

            AnimatedVisibility(
                visible = showMenu,
                enter = fadeIn(tween(400)) + scaleIn(tween(600, easing = EaseOutBack), initialScale = 0.9f),
                exit = fadeOut(tween(400)) + scaleOut(tween(600, easing = EaseInBack), targetScale = 0.9f),
                modifier = Modifier.align(Alignment.Center)
            ) {
                AppleMenu(
                    options = analysisOptions,
                    onOptionChange = { viewModel.updateAnalysisOptions(it) },
                    onStart = { 
                        showMenu = false
                        isAnalyzing = true
                    }
                )
            }

            if (isAnalyzing && !showMenu) {
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            currentStep.instruction,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        // Minimalist Indicator
                        Box(
                            modifier = Modifier.width(100.dp).height(2.dp).background(Color.White.copy(alpha = 0.2f))
                        ) {
                            val progress = (currentStep.ordinal.toFloat() / (ScanStep.entries.size - 1))
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progress)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppleMenu(
    options: MainViewModel.AnalysisOptions,
    onOptionChange: (MainViewModel.AnalysisOptions) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .clip(RoundedCornerShape(44.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- HEADER ICON ---
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Tune, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Analysis Protocol", 
            color = Color.White, 
            fontSize = 24.sp, 
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )
        Text(
            "Configure your biometric parameters",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AppleToggle("Golden Ratio (PHI)", options.mathAnalysis) { onOptionChange(options.copy(mathAnalysis = it)) }
            AppleToggle("Biometric Symmetry", options.faceShape) { onOptionChange(options.copy(faceShape = it)) }
            AppleToggle("Orbital & Ocular Metrics", options.eyeShape) { onOptionChange(options.copy(eyeShape = it, eyebrows = it)) }
            AppleToggle("Mandibular Sharpness", options.bodyFat) { onOptionChange(options.copy(bodyFat = it)) }
            AppleToggle("Dermal Texture Analysis", options.skin) { onOptionChange(options.copy(skin = it, hair = it)) }
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(22.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
        ) {
            Text("Begin Analysis", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
fun AppleToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label, 
            color = Color.White.copy(alpha = 0.85f), 
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF34C759), // Apple Green
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.White.copy(alpha = 0.12f),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
