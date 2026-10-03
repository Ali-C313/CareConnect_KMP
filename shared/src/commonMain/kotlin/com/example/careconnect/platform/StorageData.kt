package com.example.careconnect.platform

import dev.gitlive.firebase.storage.Data

// Android: ByteArray, iOS: NSData
expect fun ByteArray.toStorageData(): Data
