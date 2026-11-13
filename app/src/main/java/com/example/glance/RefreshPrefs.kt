package com.example.glance

import android.content.Context

class RefreshPrefs(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("refresh_prefs", Context.MODE_PRIVATE)

    // Epoch seconds of the last successful refresh, or 0 if it has never happened
    fun getLastRefreshed(): Long = prefs.getLong("last_refreshed", 0L)

    fun setLastRefreshed(epochSeconds: Long) {
        prefs.edit().putLong("last_refreshed", epochSeconds).apply()
    }
}