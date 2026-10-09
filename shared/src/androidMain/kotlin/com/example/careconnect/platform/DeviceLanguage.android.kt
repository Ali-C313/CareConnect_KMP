package com.example.careconnect.platform

import java.util.Locale

actual fun deviceLanguage(): String = Locale.getDefault().language
