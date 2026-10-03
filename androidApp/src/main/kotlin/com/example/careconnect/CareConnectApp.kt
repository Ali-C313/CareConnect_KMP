package com.example.careconnect

import android.app.Application
import com.example.careconnect.di.initKoin
import org.koin.android.ext.koin.androidContext

class CareConnectApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@CareConnectApp)
        }
    }
}
