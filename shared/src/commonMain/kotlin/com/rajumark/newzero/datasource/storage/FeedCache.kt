package com.rajumark.newzero.datasource.storage

import com.rajumark.newzero.domain.ArticleFeed
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class FeedCache(
    private val settings: Settings,
    private val json: Json
) {
    private companion object {
        private const val KEY_CACHE = "key_feed_cache"
    }

    private var diskCache: Map<String, ArticleFeed>
        get() {
            return settings.getStringOrNull(KEY_CACHE)?.let { str ->
                json.decodeFromString(ListSerializer(ArticleFeed.serializer()), str)
                    .associate { it.feedUrl to it }
            } ?: mutableMapOf()
        }
        set(value) {
            val list = value.map { it.value }
            settings[KEY_CACHE] =
                json.encodeToString(ListSerializer(ArticleFeed.serializer()), list)
        }

    private val memCache: MutableMap<String, ArticleFeed> by lazy { diskCache.toMutableMap() }

    suspend fun fetchByUrl(url: String): ArticleFeed? = memCache[url]

    suspend fun save(feed: ArticleFeed) {
        memCache[feed.feedUrl] = feed
        diskCache = memCache
    }

    suspend fun removeSource(url: String) {
        memCache.remove(url)
        diskCache = memCache
    }

    suspend fun loadAllSources(): List<ArticleFeed> = memCache.values.toList()
}