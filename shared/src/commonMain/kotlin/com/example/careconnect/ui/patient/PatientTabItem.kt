package com.example.careconnect.ui.patient

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.careconnect.i18n.tr

enum class PatientTabItem(
    val route: PatientRoute,
    val icon: ImageVector
) {
    HOME(PatientRoute.Home, Icons.Default.Home),
    ORDERS(PatientRoute.Orders, Icons.Default.LocalHospital),
    MEDICINE(PatientRoute.Medicine, Icons.Default.Medication),
    APPOINTMENTS(PatientRoute.Appointments, Icons.Default.CalendarMonth),
    SETTINGS(PatientRoute.Settings, Icons.Default.Settings);

    val title: String
        get() = when (this) {
            HOME -> tr("Home")
            ORDERS -> tr("Aufträge")
            MEDICINE -> tr("Medizin")
            APPOINTMENTS -> tr("Termine")
            SETTINGS -> tr("Einstellungen")
        }
}
