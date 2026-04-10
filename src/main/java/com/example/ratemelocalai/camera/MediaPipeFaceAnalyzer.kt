package com.example.ratemelocalai.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class MediaPipeFaceAnalyzer(
    context: Context,
    private val onResults: (FaceLandmarkerResult, Int, Int, Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    private var faceLandmarker: FaceLandmarker? = null

    init {
        val baseOptionsBuilder = BaseOptions.builder()
            .setDelegate(Delegate.GPU)
            .setModelAssetPath("face_landmarker.task")

        val imgOptions = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(baseOptionsBuilder.build())
            .setRunningMode(RunningMode.IMAGE)
            .setNumFaces(1)
            .build()
            
        try {
            faceLandmarker = FaceLandmarker.createFromOptions(context, imgOptions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun analyze(imageProxy: ImageProxy) {
        try {
            val bitmap = imageProxy.toBitmap()
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            
            val matrix = Matrix().apply {
                postRotate(rotationDegrees.toFloat())
            }
            
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )

            val mpImage = BitmapImageBuilder(rotatedBitmap).build()
            
            val result = faceLandmarker?.detect(mpImage)
            if (result != null && result.faceLandmarks().isNotEmpty()) {
                onResults(result, rotatedBitmap.width, rotatedBitmap.height, rotatedBitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            imageProxy.close()
        }
    }
}
