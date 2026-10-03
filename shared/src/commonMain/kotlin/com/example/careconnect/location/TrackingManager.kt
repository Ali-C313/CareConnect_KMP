package com.example.careconnect.location

import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.repository.LocationRepository
import com.example.careconnect.platform.LocationTracker
import com.example.careconnect.util.currentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Lädt den Standort des Mitarbeiters nach Firestore hoch (employeeLocations/{id}).
 * Wie in der iOS-App max. alle 10 Sekunden.
 */
class TrackingManager(
    private val tracker: LocationTracker,
    private val locationRepo: LocationRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var employeeId: String? = null
    private var lastUpload = 0L

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _lastLocation = MutableStateFlow<Coordinate?>(null)
    val lastLocation = _lastLocation.asStateFlow()

    fun start(employeeId: String) {
        this.employeeId = employeeId
        tracker.stop()
        tracker.start { coordinate -> onLocation(coordinate) }
        _isTracking.update { true }
    }

    // z.B. nachdem die Berechtigung erteilt wurde
    fun restart() {
        employeeId?.let { start(it) }
    }

    fun stop() {
        tracker.stop()
        employeeId = null
        _isTracking.update { false }
    }

    private fun onLocation(coordinate: Coordinate) {
        _lastLocation.update { coordinate }

        val now = currentTimeMillis()
        if (now - lastUpload < 10_000) return
        lastUpload = now

        val id = employeeId ?: return
        scope.launch {
            locationRepo.uploadLocation(id, coordinate)
        }
    }
}
