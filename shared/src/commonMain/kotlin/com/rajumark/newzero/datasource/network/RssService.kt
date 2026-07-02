package com.rajumark.newzero.datasource.network

import com.rajumark.newzero.domain.ArticleFeed
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class RssService(
    private val httpClient: HttpClient
) {
    suspend fun fetchByUrl(url: String, isDefault: Boolean): ArticleFeed {
        val feed = httpClient.get(url = Url(url)).body<ArticleFeed>()
        feed.isPreloaded = isDefault
        feed.feedUrl = url
        return feed
    }
}