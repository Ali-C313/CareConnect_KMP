package com.example.careconnect.ui.patient

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.ui.patient.appointments.AppointmentsScreen
import com.example.careconnect.ui.patient.home.PatientHomeScreen
import com.example.careconnect.ui.patient.medicine.MedicineScreen
import com.example.careconnect.ui.patient.orders.PatientOrdersScreen
import com.example.careconnect.ui.patient.settings.EditPatientProfileScreen
import com.example.careconnect.ui.patient.settings.PatientSettingsScreen

// Entspricht PatientTabView aus iOS
@Composable
fun PatientApp() {
    val navController = rememberNavController()
    var currentTab by rememberSaveable { mutableStateOf(PatientTabItem.HOME) }

    fun selectTab(tabItem: PatientTabItem) {
        currentTab = tabItem
        navController.navigateToTab(tabItem.route)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar {
                PatientTabItem.entries.forEach { tabItem ->
                    NavigationBarItem(
                        selected = currentTab == tabItem,
                        onClick = { selectTab(tabItem) },
                        icon = { Icon(tabItem.icon, tabItem.title) },
                        label = { Text(tabItem.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PatientRoute.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<PatientRoute.Home> {
                PatientHomeScreen(
                    onOrdersClick = { selectTab(PatientTabItem.ORDERS) }
                )
            }
            composable<PatientRoute.Orders> {
                PatientOrdersScreen()
            }
            composable<PatientRoute.Medicine> {
                MedicineScreen()
            }
            composable<PatientRoute.Appointments> {
                AppointmentsScreen()
            }
            composable<PatientRoute.Settings> {
                PatientSettingsScreen(
                    onEditProfileClick = { navController.navigate(PatientRoute.EditProfile) }
                )
            }
            composable<PatientRoute.EditProfile> {
                EditPatientProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

private fun NavHostController.navigateToTab(route: PatientRoute) {
    navigate(route) {
        popUpTo(PatientRoute.Home) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
