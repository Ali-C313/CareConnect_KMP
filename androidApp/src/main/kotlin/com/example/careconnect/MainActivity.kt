package com.example.careconnect

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.careconnect.location.TrackingManager
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val trackingManager: TrackingManager by inject()

    // Standort-Berechtigung beim Start abfragen (wie in der iOS-App)
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) {
            // Tracking neu starten, falls der Mitarbeiter schon eingeloggt ist
            trackingManager.restart()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            App()
        }
    }
}
