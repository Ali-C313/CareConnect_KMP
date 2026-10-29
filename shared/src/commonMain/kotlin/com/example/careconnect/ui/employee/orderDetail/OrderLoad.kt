package com.example.careconnect.ui.employee.orderDetail

import com.example.careconnect.domain.model.Order

// Ergebnis beim Laden, order = null heißt nicht gefunden
data class OrderLoad(val order: Order?)
