package com.trackwell.app.domain.model

import java.util.Calendar

enum class SugarSlot(
    val id: String,
    val title: String,
    val subtitle: String,
    val defaultHour: Int,
    val defaultMinute: Int
) {
    BEFORE_BREAKFAST("before_breakfast", "Before Breakfast", "Fasting (~7:30 AM)", 7, 30),
    BEFORE_LUNCH("before_lunch", "Before Lunch", "Pre-meal (~1:00 PM)", 13, 0),
    AFTER_LUNCH("after_lunch", "After Lunch", "Post-meal (~4:30 PM)", 16, 30),
    BEFORE_DINNER("before_dinner", "Before Dinner", "Pre-meal (~8:00 PM)", 20, 0);

    companion object {
        fun fromId(id: String): SugarSlot = entries.firstOrNull { it.id == id } ?: BEFORE_BREAKFAST

        fun fromString(value: String): SugarSlot {
            return when (value.trim().uppercase()) {
                "FASTING", "BEFORE_BREAKFAST", "BEFOREBREAKFAST", "BREAKFAST" -> BEFORE_BREAKFAST
                "BEFORE_LUNCH", "BEFORELUNCH", "PRE_LUNCH", "LUNCH" -> BEFORE_LUNCH
                "AFTER_LUNCH", "AFTERLUNCH", "POST_LUNCH" -> AFTER_LUNCH
                "BEFORE_DINNER", "BEFOREDINNER", "PRE_DINNER", "DINNER", "AFTER_DINNER" -> BEFORE_DINNER
                else -> entries.firstOrNull { 
                    it.name.equals(value, ignoreCase = true) || it.id.equals(value, ignoreCase = true) 
                } ?: BEFORE_BREAKFAST
            }
        }

        /**
         * Auto-suggests the most appropriate slot based on the current hour.
         * < 10:30 -> Before Breakfast
         * 10:30 to 14:30 -> Before Lunch
         * 14:30 to 18:00 -> After Lunch
         * > 18:00 -> Before Dinner
         */
        fun suggestCurrentSlot(calendar: Calendar = Calendar.getInstance()): SugarSlot {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val totalMinutes = hour * 60 + minute

            return when {
                totalMinutes < 10 * 60 + 30 -> BEFORE_BREAKFAST
                totalMinutes < 14 * 60 + 30 -> BEFORE_LUNCH
                totalMinutes < 18 * 60 -> AFTER_LUNCH
                else -> BEFORE_DINNER
            }
        }
    }
}
