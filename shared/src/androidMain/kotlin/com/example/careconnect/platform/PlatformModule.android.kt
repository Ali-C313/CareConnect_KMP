package com.example.careconnect.platform

import com.example.careconnect.local.DATA_STORE_FILE_NAME
import com.example.careconnect.local.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        createDataStore {
            androidContext().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
        }
    }
    single<LocationTracker> { AndroidLocationTracker(androidContext()) }
}
