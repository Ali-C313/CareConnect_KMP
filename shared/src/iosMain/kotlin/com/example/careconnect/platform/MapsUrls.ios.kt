package com.example.careconnect.platform

import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.Foundation.create
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters

@OptIn(BetaInteropApi::class)
private fun String.urlEncoded(): String =
    NSString.create(string = this).stringByAddingPercentEncodingWithAllowedCharacters(
        NSCharacterSet.URLQueryAllowedCharacterSet
    ) ?: this

actual fun mapsSearchUrl(query: String): String =
    "http://maps.apple.com/?q=${query.urlEncoded()}"

actual fun mapsDirectionsUrl(latitude: Double, longitude: Double): String =
    "http://maps.apple.com/?daddr=$latitude,$longitude"
