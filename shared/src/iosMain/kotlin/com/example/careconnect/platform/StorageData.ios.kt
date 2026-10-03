package com.example.careconnect.platform

import dev.gitlive.firebase.storage.Data
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create

// ByteArray -> NSData für Firebase Storage
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun ByteArray.toStorageData(): Data {
    val nsData = usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }
    return Data(nsData)
}
