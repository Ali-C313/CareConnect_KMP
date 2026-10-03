package com.example.careconnect.remote.model

import dev.gitlive.firebase.firestore.Timestamp

// Firestore-Timestamp <-> Millisekunden
fun Timestamp.toMillis(): Long = seconds * 1000 + nanoseconds / 1_000_000

fun Long.toTimestamp(): Timestamp = Timestamp(
    seconds = this.floorDiv(1000L),
    nanoseconds = (this.mod(1000L) * 1_000_000L).toInt()
)
