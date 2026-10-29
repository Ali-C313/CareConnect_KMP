package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Coordinate
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

// employeeLocations/{employeeId}
@Serializable
data class EmployeeLocationDto(
    val lat: Double? = null,
    val lon: Double? = null,
    val updatedAt: Timestamp? = null
)

// Firestore -> App-Model
fun EmployeeLocationDto.toCoordinate(): Coordinate? {
    val latitude = lat ?: return null
    val longitude = lon ?: return null
    return Coordinate(latitude, longitude)
}
