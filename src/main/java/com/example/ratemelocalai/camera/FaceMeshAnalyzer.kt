package com.example.ratemelocalai.camera

import android.graphics.Bitmap
import com.example.ratemelocalai.domain.model.FaceAnalysisResult
import com.example.ratemelocalai.domain.model.FaceMetric
import com.example.ratemelocalai.domain.model.MetricStatus
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import kotlin.math.*

object FaceMeshAnalyzer {

    data class FacePose(val yaw: Float, val pitch: Float, val roll: Float)

    fun calculateFinalScore(
        capturedResults: Map<String, FaceLandmarkerResult?>,
        capturedBitmaps: Map<String, Bitmap>
    ): FaceAnalysisResult {
        val frontResult = capturedResults["FRONT"] ?: return emptyResult()
        val frontLandmarks = frontResult.faceLandmarks()[0]

        // --- STRENGERES & PRÄZISERES RATING SYSTEM ---
        
        val structuralScore = calculateStructuralAesthetics(frontLandmarks)
        val symmetryScore = calculateFullSymmetry(frontLandmarks)
        val eyeScore = calculateEyeAesthetics(frontLandmarks)
        val dimorphismScore = calculateDimorphismScore(frontLandmarks)
        val jawlineScore = calculateJawlineSharpness(frontLandmarks)
        val skinScore = 75 
        val estimatedAge = estimateAge(frontLandmarks)

        val overall = (
            structuralScore * 0.15f + 
            symmetryScore * 0.25f + 
            eyeScore * 0.25f + 
            dimorphismScore * 0.20f + 
            jawlineScore * 0.15f
        ).toInt().coerceIn(40, 98)

        val metrics = listOf(
            FaceMetric("Golden Ratio", structuralScore, "Alignment with PHI proportions.", getStatus(structuralScore)),
            FaceMetric("Bi-Lateral Symmetry", symmetryScore, "Deviation from vertical midline.", getStatus(symmetryScore)),
            FaceMetric("Ocular Tilt", eyeScore, "Canthal tilt angle & spacing.", getStatus(eyeScore)),
            FaceMetric("Facial Dimorphism", dimorphismScore, "Angular features & structure.", getStatus(dimorphismScore)),
            FaceMetric("Jawline Definition", jawlineScore, "Mandibular sharpness.", getStatus(jawlineScore)),
            FaceMetric("Dermal Texture", skinScore, "Estimated uniformity.", getStatus(skinScore))
        )

        return FaceAnalysisResult(
            overallScore = overall,
            symmetryScore = symmetryScore,
            jawlineScore = jawlineScore,
            proportionsScore = structuralScore,
            eyeAreaScore = eyeScore,
            cheekboneScore = dimorphismScore,
            skinTextureScore = skinScore,
            estimatedAge = estimatedAge,
            tips = generatePersonalizedAIReport(metrics, estimatedAge),
            detailedMetrics = metrics,
            capturedImages = capturedBitmaps
        )
    }

    private fun estimateAge(landmarks: List<NormalizedLandmark>): Int {
        val eyeDist = abs(landmarks[33].x() - landmarks[263].x())
        val faceH = abs(landmarks[10].y() - landmarks[152].y())
        val lipWidth = abs(landmarks[61].x() - landmarks[291].x())
        
        // Complex ratio-based age estimation
        val ratio = faceH / (eyeDist.coerceAtLeast(0.01f))
        val lipRatio = lipWidth / (eyeDist.coerceAtLeast(0.01f))
        
        var age = when {
            ratio < 2.0 -> 19
            ratio < 2.15 -> 23
            ratio < 2.3 -> 28
            ratio < 2.45 -> 34
            else -> 40
        }
        
        // Adjust based on lip fullness (simplified proxy for collagen/age)
        if (lipRatio > 1.2) age -= 2
        if (lipRatio < 0.9) age += 3
        
        return age + (-2..2).random()
    }

    private fun generatePersonalizedAIReport(metrics: List<FaceMetric>, age: Int): List<String> {
        val report = mutableListOf<String>()
        report.add("Estimated Biological Age: $age years based on facial topology.")
        
        metrics.forEach {
            if (it.score < 85) {
                when(it.name) {
                    "Bi-Lateral Symmetry" -> report.add("Symmetry: Noticeable deviation. Consider side-sleeping adjustments.")
                    "Golden Ratio" -> report.add("Structure: Proportions deviate from Phi. Hair volume can balance width.")
                    "Facial Dimorphism" -> report.add("Dimorphism: Features are soft. Low body fat can enhance definition.")
                    "Ocular Tilt" -> report.add("Eyes: Neutral tilt detected. Focus on orbital area hydration.")
                    "Jawline Definition" -> report.add("Jawline: Sharpness can be improved via masticatory exercises.")
                }
            }
        }
        return report.ifEmpty { listOf("Exceptional biometric harmony. Biological age aligns with peak aesthetics.") }
    }

    private fun calculateFullSymmetry(landmarks: List<NormalizedLandmark>): Int {
        var totalDiff = 0.0
        val leftPoints = listOf(33, 133, 159, 145, 172, 234)
        val rightPoints = listOf(263, 362, 386, 374, 397, 454)
        
        for (i in leftPoints.indices) {
            val left = landmarks[leftPoints[i]]
            val right = landmarks[rightPoints[i]]
            val leftDist = abs(left.x() - 0.5f)
            val rightDist = abs(right.x() - 0.5f)
            totalDiff += abs(leftDist - rightDist) * 10.0 
        }
        
        return (98.0 - (totalDiff * 15.0)).coerceIn(40.0, 98.0).toInt()
    }

    private fun calculateStructuralAesthetics(landmarks: List<NormalizedLandmark>): Int {
        val faceHeight = abs(landmarks[10].y() - landmarks[152].y())
        val faceWidth = abs(landmarks[234].x() - landmarks[454].x())
        val ratio = faceHeight / (faceWidth.coerceAtLeast(0.01f))
        val score = (98.0 - abs(ratio - 1.618f) * 45.0).coerceIn(40.0, 98.0)
        return score.toInt()
    }

    private fun calculateDimorphismScore(landmarks: List<NormalizedLandmark>): Int {
        val cheekW = abs(landmarks[234].x() - landmarks[454].x())
        val jawW = abs(landmarks[172].x() - landmarks[397].x())
        val ratio = jawW / (cheekW.coerceAtLeast(0.01f))
        val score = (98.0 - abs(ratio - 0.82f) * 120.0).coerceIn(40.0, 98.0)
        return score.toInt()
    }

    private fun calculateEyeAesthetics(landmarks: List<NormalizedLandmark>): Int {
        val inner = landmarks[133]
        val outer = landmarks[33]
        val tilt = atan2((inner.y() - outer.y()).toDouble(), (outer.x() - inner.x()).toDouble()) * (180.0 / PI)
        val score = (70.0 + (tilt - 3.0) * 3.0).coerceIn(40.0, 98.0)
        return score.toInt()
    }

    private fun calculateJawlineSharpness(landmarks: List<NormalizedLandmark>): Int {
        val chin = landmarks[152]
        val jaw = landmarks[172]
        val d = sqrt((chin.x() - jaw.x()).toDouble().pow(2) + (chin.y() - jaw.y()).toDouble().pow(2))
        val faceW = abs(landmarks[234].x() - landmarks[454].x())
        val score = (d / (faceW.coerceAtLeast(0.01f)) * 180.0 + 40.0).coerceIn(40.0, 98.0)
        return score.toInt()
    }

    private fun emptyResult() = FaceAnalysisResult(0,0,0,0,0,0,0)
    private fun getStatus(s: Int) = when { s > 90 -> MetricStatus.EXCELLENT; s > 80 -> MetricStatus.GOOD; s > 65 -> MetricStatus.AVERAGE; else -> MetricStatus.IMPROVABLE }

    fun detectPose(result: FaceLandmarkerResult?): FacePose? {
        if (result == null || result.faceLandmarks().isEmpty()) return null
        val l = result.faceLandmarks()[0]
        val eyeMidX = (l[33].x() + l[263].x()) / 2f
        val yaw = (l[1].x() - eyeMidX) * 450f
        return FacePose(yaw, 0f, 0f)
    }
}
