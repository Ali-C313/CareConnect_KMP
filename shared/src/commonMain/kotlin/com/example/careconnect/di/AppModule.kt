package com.example.careconnect.di

import com.example.careconnect.domain.repository.AppointmentRepository
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.domain.repository.LocationRepository
import com.example.careconnect.domain.repository.MedicineRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.domain.repository.SettingsRepository
import com.example.careconnect.domain.repository.StorageRepository
import com.example.careconnect.local.SettingsRepositoryImpl
import com.example.careconnect.location.TrackingManager
import com.example.careconnect.platform.platformModule
import com.example.careconnect.remote.repository.AppointmentRepositoryImpl
import com.example.careconnect.remote.repository.AuthRepositoryImpl
import com.example.careconnect.remote.repository.EmployeeRepositoryImpl
import com.example.careconnect.remote.repository.GeoRepositoryImpl
import com.example.careconnect.remote.repository.LocationRepositoryImpl
import com.example.careconnect.remote.repository.MedicineRepositoryImpl
import com.example.careconnect.remote.repository.OrderRepositoryImpl
import com.example.careconnect.remote.repository.PatientRepositoryImpl
import com.example.careconnect.remote.repository.StorageRepositoryImpl
import com.example.careconnect.ui.auth.AuthViewModel
import com.example.careconnect.ui.auth.LoginViewModel
import com.example.careconnect.ui.auth.RegisterViewModel
import com.example.careconnect.ui.employee.employeeProfile.EmployeeProfileViewModel
import com.example.careconnect.ui.employee.home.HomeViewModel
import com.example.careconnect.ui.employee.orderDetail.OrderDetailViewModel
import com.example.careconnect.ui.employee.orders.NewOrderViewModel
import com.example.careconnect.ui.employee.orders.OrdersViewModel
import com.example.careconnect.ui.employee.patientProfile.PatientProfileViewModel
import com.example.careconnect.ui.employee.patients.PatientsViewModel
import com.example.careconnect.ui.employee.search.SearchViewModel
import com.example.careconnect.ui.employee.settings.EditEmployeeProfileViewModel
import com.example.careconnect.ui.patient.appointments.AppointmentsViewModel
import com.example.careconnect.ui.patient.home.PatientHomeViewModel
import com.example.careconnect.ui.patient.medicine.MedicineViewModel
import com.example.careconnect.ui.patient.orders.PatientOrdersViewModel
import com.example.careconnect.ui.patient.settings.EditPatientProfileViewModel
import com.example.careconnect.ui.settings.SettingsViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {

    // Firebase
    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.storage }

    // Ktor für Geoapify
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json { ignoreUnknownKeys = true }
                )
            }
        }
    }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<StorageRepository> { StorageRepositoryImpl(get()) }
    single<EmployeeRepository> { EmployeeRepositoryImpl(get(), get()) }
    single<PatientRepository> { PatientRepositoryImpl(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get()) }
    single<MedicineRepository> { MedicineRepositoryImpl(get()) }
    single<AppointmentRepository> { AppointmentRepositoryImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get()) }
    single<GeoRepository> { GeoRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    single { TrackingManager(get(), get()) }

    // ViewModels
    viewModelOf(::AuthViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::SettingsViewModel)

    // Mitarbeiter
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::PatientsViewModel)
    viewModelOf(::PatientProfileViewModel)
    viewModelOf(::OrdersViewModel)
    viewModelOf(::NewOrderViewModel)
    viewModelOf(::OrderDetailViewModel)
    viewModelOf(::EmployeeProfileViewModel)
    viewModelOf(::EditEmployeeProfileViewModel)

    // Patienten
    viewModelOf(::PatientHomeViewModel)
    viewModelOf(::PatientOrdersViewModel)
    viewModelOf(::MedicineViewModel)
    viewModelOf(::AppointmentsViewModel)
    viewModelOf(::EditPatientProfileViewModel)
}

// Wird von Android (CareConnectApp) und iOS (MainViewController) aufgerufen
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule, platformModule)
    }
}
