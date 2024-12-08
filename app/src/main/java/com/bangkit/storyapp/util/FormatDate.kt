package com.bangkit.storyapp.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun formatDate(date: String): String {
    return try {
        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.US
        )
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat(
            "yyyy-MM-dd 'Jam' HH.mm",
            Locale.getDefault()
        )
        outputFormat.timeZone = TimeZone.getDefault() // Menggunakan zona waktu perangkat

        val date = inputFormat.parse(date)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        // Menangani format yang salah
        "Invalid date"
    }
}