package com.example.careconnect.ui.employee.home

import com.example.careconnect.domain.model.Order

// Ein Halt der heutigen Tour
data class TourStop(
    val order: Order,
    val address: String
)
