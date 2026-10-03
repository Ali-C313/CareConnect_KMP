package com.example.careconnect.platform

import dev.gitlive.firebase.storage.Data

actual fun ByteArray.toStorageData(): Data = Data(this)
