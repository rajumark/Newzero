package com.rajumark.newzero.core

import com.rajumark.newzero.AppSettings
import com.rajumark.newzero.datasource.network.RssService
import com.rajumark.newzero.datasource.storage.FeedCache
import com.rajumark.newzero.domain.ArticleFeed
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class FeedManager(
    private val feedLoader: RssService,
    private val feedStorage: FeedCache,
    private val settings: AppSettings = AppSettings(setOf("https://example.com/feed.xml"))
) {

    @Throws(Exception::class)
    suspend fun loadAllSources(
        refreshAll: Boolean = false
    ): List<ArticleFeed> {
        var feeds = feedStorage.loadAllSources()

        if (refreshAll || feeds.isEmpty()) {
            val sourceUrls =
                if (feeds.isEmpty()) settings.preloadedUrls else feeds.map { it.feedUrl }.filterNotNull()
            feeds = sourceUrls.mapAsync { url ->
                val new = feedLoader.fetchByUrl(url, settings.isPreloaded(url))
                feedStorage.save(new)
                new
            }
        }

        return feeds
    }

    @Throws(Exception::class)
    suspend fun addSource(url: String) {
        val feed = feedLoader.fetchByUrl(url, settings.isPreloaded(url))
        feedStorage.save(feed)
    }

    @Throws(Exception::class)
    suspend fun removeSource(url: String) {
        feedStorage.removeSource(url)
    }

    private suspend fun <A, B> Iterable<A>.mapAsync(f: suspend (A) -> B): List<B> =
        coroutineScope { map { async { f(it) } }.awaitAll() }
}