package com.example.ratemelocalai.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ratemelocalai.domain.model.FaceAnalysisResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _lastResult = MutableStateFlow<FaceAnalysisResult?>(null)
    val lastResult: StateFlow<FaceAnalysisResult?> = _lastResult.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    // Configuration Toggles
    private val _voiceGuideEnabled = MutableStateFlow(false)
    val voiceGuideEnabled: StateFlow<Boolean> = _voiceGuideEnabled.asStateFlow()

    private val _goldenRatioEnabled = MutableStateFlow(false)
    val goldenRatioEnabled: StateFlow<Boolean> = _goldenRatioEnabled.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(true) // Default true for now
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    // Analysis Selection States
    data class AnalysisOptions(
        val bodyFat: Boolean = true,
        val faceShape: Boolean = true,
        val eyebrows: Boolean = true,
        val eyeShape: Boolean = true,
        val skin: Boolean = true,
        val hair: Boolean = true,
        val mathAnalysis: Boolean = true,
        val attractiveness: Boolean = true,
        val improvementTips: Boolean = true
    )

    private val _analysisOptions = MutableStateFlow(AnalysisOptions())
    val analysisOptions: StateFlow<AnalysisOptions> = _analysisOptions.asStateFlow()

    fun updateAnalysisOptions(options: AnalysisOptions) {
        _analysisOptions.value = options
    }

    fun setAnalysisResult(result: FaceAnalysisResult) {
        _lastResult.value = result
        _isAnalyzing.value = false
    }

    fun startAnalysis() {
        _isAnalyzing.value = true
    }

    fun toggleVoiceGuide() {
        _voiceGuideEnabled.value = !_voiceGuideEnabled.value
    }

    fun toggleGoldenRatio() {
        _goldenRatioEnabled.value = !_goldenRatioEnabled.value
    }

    fun resetOnboarding() {
        _onboardingCompleted.value = false
    }
    
    fun completeOnboarding() {
        _onboardingCompleted.value = true
    }
}
