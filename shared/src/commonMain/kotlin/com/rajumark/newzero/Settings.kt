package com.rajumark.newzero

class Settings(val defaultFeedUrls: Set<String>) {
    fun isDefault(feedUrl: String) = defaultFeedUrls.contains(feedUrl)
}