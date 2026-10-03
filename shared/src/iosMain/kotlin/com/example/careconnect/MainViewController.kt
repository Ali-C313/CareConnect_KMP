package com.example.careconnect

import androidx.compose.ui.window.ComposeUIViewController
import com.example.careconnect.di.initKoin

// Wird aus ContentView.swift aufgerufen
fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}
