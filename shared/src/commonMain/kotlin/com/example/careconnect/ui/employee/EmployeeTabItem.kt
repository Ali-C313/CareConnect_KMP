package com.example.careconnect.ui.employee

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.careconnect.domain.model.OrganizationType
import com.example.careconnect.i18n.tr

enum class EmployeeTabItem(
    val route: EmployeeRoute,
    val icon: ImageVector
) {
    HOME(EmployeeRoute.Home, Icons.Default.Home),
    ORDERS(EmployeeRoute.Orders, Icons.Default.Description),
    PRESCRIPTIONS(EmployeeRoute.Prescriptions, Icons.AutoMirrored.Filled.Assignment),
    PATIENTS(EmployeeRoute.Patients, Icons.Default.Groups),
    SETTINGS(EmployeeRoute.Settings, Icons.Default.Settings);

    // Praxis und Klinik machen Hausbesuche statt Pflegeaufträge
    fun titleFor(type: OrganizationType): String = when (this) {
        HOME -> tr("Home")
        ORDERS -> if (type == OrganizationType.PFLEGEDIENST) tr("Aufträge") else tr("Hausbesuche")
        PRESCRIPTIONS -> tr("Verordnungen")
        PATIENTS -> tr("Patienten")
        SETTINGS -> tr("Einstellungen")
    }
}
