package com.example.glance

const val REFRESH_INTERVAL_SECONDS = 15 * 60L

fun shouldRefresh(
    lastRefreshed: Long,
    now: Long,
    minIntervalSeconds: Long = REFRESH_INTERVAL_SECONDS
): Boolean {
    if (lastRefreshed == 0L) return true      // never refreshed
    if (lastRefreshed > now) return true      // stored time is in the future, clock was changed
    return now - lastRefreshed >= minIntervalSeconds
}