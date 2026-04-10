package com.example.ratemelocalai.domain.model

import android.graphics.Bitmap

data class FaceAnalysisResult(
    val overallScore: Int,
    val symmetryScore: Int,
    val jawlineScore: Int,
    val proportionsScore: Int,
    val eyeAreaScore: Int,
    val cheekboneScore: Int,
    val skinTextureScore: Int,
    val estimatedAge: Int = 25,
    val hairAnalysis: String = "Normal",
    val tips: List<String> = emptyList(),
    val detailedMetrics: List<FaceMetric> = emptyList(),
    val analysisDate: Long = System.currentTimeMillis(),
    val capturedImages: Map<String, Bitmap> = emptyMap()
)

data class FaceMetric(
    val name: String,
    val score: Int,
    val description: String,
    val status: MetricStatus
)

enum class MetricStatus {
    EXCELLENT, GOOD, AVERAGE, IMPROVABLE
}
