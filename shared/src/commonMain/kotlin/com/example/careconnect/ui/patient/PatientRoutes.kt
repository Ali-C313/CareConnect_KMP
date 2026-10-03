package com.example.careconnect.ui.patient

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed interface PatientRoute {
    @Serializable
    object Home : PatientRoute

    @Serializable
    object Orders : PatientRoute

    @Serializable
    object Medicine : PatientRoute

    @Serializable
    object Appointments : PatientRoute

    @Serializable
    object Settings : PatientRoute

    @Serializable
    object EditProfile : PatientRoute
}

enum class PatientTabItem(
    val route: PatientRoute,
    val title: String,
    val icon: ImageVector
) {
    HOME(PatientRoute.Home, "Home", Icons.Default.Home),
    ORDERS(PatientRoute.Orders, "Aufträge", Icons.Default.LocalHospital),
    MEDICINE(PatientRoute.Medicine, "Medizin", Icons.Default.Medication),
    APPOINTMENTS(PatientRoute.Appointments, "Termine", Icons.Default.CalendarMonth),
    SETTINGS(PatientRoute.Settings, "Einstellungen", Icons.Default.Settings)
}
