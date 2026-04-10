package com.example.ratemelocalai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import kotlin.math.max

@Composable
fun FaceMeshOverlay(
    result: FaceLandmarkerResult?,
    imageWidth: Int,
    imageHeight: Int,
    isFrontCamera: Boolean = true
) {
    if (result == null || result.faceLandmarks().isEmpty() || imageWidth == 0 || imageHeight == 0) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val landmarkPoints = result.faceLandmarks()[0]

        val canvasWidth = size.width
        val canvasHeight = size.height
        
        val scale = max(canvasWidth / imageWidth, canvasHeight / imageHeight)
        val scaledWidth = imageWidth * scale
        val scaledHeight = imageHeight * scale
        
        val offsetX = (canvasWidth - scaledWidth) / 2
        val offsetY = (canvasHeight - scaledHeight) / 2

        fun transformX(x: Float): Float {
            val mappedX = x * scaledWidth + offsetX
            return if (isFrontCamera) canvasWidth - mappedX else mappedX
        }
        
        fun transformY(y: Float): Float {
            return y * scaledHeight + offsetY
        }

        // --- APPLE STYLE HIGH-PRECISION MESH ---
        
        // 1. Full Tesselation (The Base Network) - Very subtle
        val meshColor = Color.White.copy(alpha = 0.08f)
        FaceLandmarker.FACE_LANDMARKS_TESSELATION.forEach { connection ->
            val start = landmarkPoints[connection.start()]
            val end = landmarkPoints[connection.end()]
            drawLine(
                color = meshColor,
                start = Offset(transformX(start.x()), transformY(start.y())),
                end = Offset(transformX(end.x()), transformY(end.y())),
                strokeWidth = 0.15.dp.toPx()
            )
        }

        // 2. High Definition Contours (Eyes, Lips, Oval)
        val contourColor = Color.White.copy(alpha = 0.45f)
        val detailStroke = 0.6.dp.toPx()

        val featureConnections = listOf(
            FaceLandmarker.FACE_LANDMARKS_FACE_OVAL,
            FaceLandmarker.FACE_LANDMARKS_LIPS,
            FaceLandmarker.FACE_LANDMARKS_LEFT_EYE,
            FaceLandmarker.FACE_LANDMARKS_RIGHT_EYE
        )

        featureConnections.forEach { connections ->
            connections.forEach { connection ->
                val start = landmarkPoints[connection.start()]
                val end = landmarkPoints[connection.end()]
                drawLine(
                    color = contourColor,
                    start = Offset(transformX(start.x()), transformY(start.y())),
                    end = Offset(transformX(end.x()), transformY(end.y())),
                    strokeWidth = detailStroke
                )
            }
        }

        // 3. Iris / Pupils (Precision indicators)
        val irisColor = Color.White.copy(alpha = 0.7f)
        val irisConnections = listOf(FaceLandmarker.FACE_LANDMARKS_LEFT_IRIS, FaceLandmarker.FACE_LANDMARKS_RIGHT_IRIS)
        irisConnections.forEach { connections ->
            connections.forEach { connection ->
                val start = landmarkPoints[connection.start()]
                val end = landmarkPoints[connection.end()]
                drawLine(
                    color = irisColor,
                    start = Offset(transformX(start.x()), transformY(start.y())),
                    end = Offset(transformX(end.x()), transformY(end.y())),
                    strokeWidth = 0.8.dp.toPx()
                )
            }
        }
        
        // 4. Manual Nose Detail (Precision points)
        // Mediapipe landmarks for nose: 1, 4, 6, 197, 195, 5, 2
        val nosePoints = listOf(1, 4, 6, 197, 195, 5, 2)
        nosePoints.forEach { i ->
            val p = landmarkPoints[i]
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = 1.dp.toPx(),
                center = Offset(transformX(p.x()), transformY(p.y()))
            )
        }
    }
}
