package com.trackwell.app.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Onboarding : Screen("onboarding", "Onboarding")
    data object CareProfile : Screen("care_profile", "Care Profile")
    data object Today : Screen("today", "Today")
    data object History : Screen("history", "History")
    data object Settings : Screen("settings", "Settings")
    data object ImportData : Screen("import_data", "Import")
    data object ExportData : Screen("export_data", "Export")
    data object WeightHistory : Screen("weight_history", "Weight History")
    data object BloodSugarHistory : Screen("blood_sugar_history", "Blood Sugar History")
    data object Medicine : Screen("medicine", "Medicines")
    data object DayDetail : Screen("day_detail/{date}", "Day Detail") {
        fun createRoute(date: String) = "day_detail/$date"
    }
}
