package com.example.careconnect.remote

import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.FirebaseFirestore

// Namen der Collections in Firestore
object CollectionNames {
    const val ORGANIZATIONS = "organizations"
    const val MEMBERS = "members"
    const val USERS = "users"
    const val MEMBERSHIPS = "memberships"
    const val INVITES = "invites"
    const val DIRECTORY = "directory"
    const val JOIN_REQUESTS = "joinRequests"

    // Unter organizations/{orgId}
    const val EMPLOYEES = "employees"
    const val PATIENTS = "patients"
    const val ORDERS = "orders"
    const val MEDICINES = "medicines"
    const val APPOINTMENTS = "appointments"
    const val DOCUMENTS = "documents"
    const val EMPLOYEE_LOCATIONS = "employeeLocations"
    const val PRESCRIPTIONS = "prescriptions"
}

// Kurzform für organizations/{orgId}
fun FirebaseFirestore.org(orgId: String): DocumentReference =
    collection(CollectionNames.ORGANIZATIONS).document(orgId)
