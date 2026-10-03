package com.example.careconnect.ui.employee

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed interface EmployeeRoute {
    @Serializable
    object Home : EmployeeRoute

    @Serializable
    object Patients : EmployeeRoute

    @Serializable
    object Orders : EmployeeRoute

    @Serializable
    object Settings : EmployeeRoute

    @Serializable
    object Search : EmployeeRoute

    @Serializable
    object EditProfile : EmployeeRoute

    @Serializable
    data class PatientProfile(val patientId: String) : EmployeeRoute

    @Serializable
    data class EmployeeProfile(val employeeId: String) : EmployeeRoute

    @Serializable
    data class OrderDetail(val orderId: String) : EmployeeRoute
}

enum class EmployeeTabItem(
    val route: EmployeeRoute,
    val title: String,
    val icon: ImageVector
) {
    HOME(EmployeeRoute.Home, "Home", Icons.Default.Home),
    PATIENTS(EmployeeRoute.Patients, "Patienten", Icons.Default.Groups),
    ORDERS(EmployeeRoute.Orders, "Aufträge", Icons.Default.Description),
    SETTINGS(EmployeeRoute.Settings, "Einstellungen", Icons.Default.Settings)
}
