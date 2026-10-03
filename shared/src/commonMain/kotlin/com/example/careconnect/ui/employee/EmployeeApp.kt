package com.example.careconnect.ui.employee

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.careconnect.ui.employee.employeeProfile.EmployeeProfileScreen
import com.example.careconnect.ui.employee.home.HomeScreen
import com.example.careconnect.ui.employee.orderDetail.OrderDetailScreen
import com.example.careconnect.ui.employee.orders.OrdersScreen
import com.example.careconnect.ui.employee.patientProfile.PatientProfileScreen
import com.example.careconnect.ui.employee.patients.PatientsScreen
import com.example.careconnect.ui.employee.search.SearchScreen
import com.example.careconnect.ui.employee.settings.EditEmployeeProfileScreen
import com.example.careconnect.ui.employee.settings.EmployeeSettingsScreen

// Entspricht MainTabView aus iOS
@Composable
fun EmployeeApp() {
    val navController = rememberNavController()
    var currentTab by rememberSaveable { mutableStateOf(EmployeeTabItem.HOME) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar {
                EmployeeTabItem.entries.forEach { tabItem ->
                    NavigationBarItem(
                        selected = currentTab == tabItem,
                        onClick = {
                            currentTab = tabItem
                            navController.navigate(tabItem.route) {
                                popUpTo(EmployeeRoute.Home) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tabItem.icon, tabItem.title) },
                        label = { Text(tabItem.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = EmployeeRoute.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<EmployeeRoute.Home> {
                HomeScreen(
                    onSearchClick = { navController.navigate(EmployeeRoute.Search) },
                    onEmployeeClick = { employeeId ->
                        navController.navigate(EmployeeRoute.EmployeeProfile(employeeId))
                    }
                )
            }

            composable<EmployeeRoute.Patients> {
                PatientsScreen(
                    onPatientClick = { patientId ->
                        navController.navigate(EmployeeRoute.PatientProfile(patientId))
                    }
                )
            }

            composable<EmployeeRoute.Orders> {
                OrdersScreen(
                    onOrderClick = { orderId ->
                        navController.navigate(EmployeeRoute.OrderDetail(orderId))
                    }
                )
            }

            composable<EmployeeRoute.Settings> {
                EmployeeSettingsScreen(
                    onEditProfileClick = { navController.navigate(EmployeeRoute.EditProfile) }
                )
            }

            composable<EmployeeRoute.Search> {
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    onEmployeeClick = { navController.navigate(EmployeeRoute.EmployeeProfile(it)) },
                    onPatientClick = { navController.navigate(EmployeeRoute.PatientProfile(it)) },
                    onOrderClick = { navController.navigate(EmployeeRoute.OrderDetail(it)) }
                )
            }

            composable<EmployeeRoute.EditProfile> {
                EditEmployeeProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable<EmployeeRoute.PatientProfile> { backStackEntry ->
                val route = backStackEntry.toRoute<EmployeeRoute.PatientProfile>()
                PatientProfileScreen(
                    patientId = route.patientId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<EmployeeRoute.EmployeeProfile> { backStackEntry ->
                val route = backStackEntry.toRoute<EmployeeRoute.EmployeeProfile>()
                EmployeeProfileScreen(
                    employeeId = route.employeeId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<EmployeeRoute.OrderDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<EmployeeRoute.OrderDetail>()
                OrderDetailScreen(
                    orderId = route.orderId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
