package com.trackwell.app.domain.model

enum class SugarReadingStatus {
    LOW,
    IN_RANGE,
    HIGH,
    NOT_RECORDED
}

data class ThresholdSettings(
    val lowThreshold: Int = 70,       // Below 70 is hypoglycemic / amber
    val targetMin: Int = 80,          // Healthy minimum
    val targetMax: Int = 140,         // Healthy maximum (above this is high / red)
    val criticalHigh: Int = 250       // Urgent alert threshold
) {
    fun evaluate(reading: Int): SugarReadingStatus {
        return when {
            reading < lowThreshold -> SugarReadingStatus.LOW
            reading > targetMax -> SugarReadingStatus.HIGH
            else -> SugarReadingStatus.IN_RANGE
        }
    }
}
