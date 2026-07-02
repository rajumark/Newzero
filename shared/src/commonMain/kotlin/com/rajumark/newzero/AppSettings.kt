package com.rajumark.newzero

class AppSettings(val preloadedUrls: Set<String>) {
    fun isPreloaded(feedUrl: String) = preloadedUrls.contains(feedUrl)
}