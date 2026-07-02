package com.rajumark.newzero.datasource.network

import com.rajumark.newzero.domain.ArticleFeed
import com.rajumark.newzero.domain.ArticleItem
import com.rajumark.newzero.domain.AtomEntry
import com.rajumark.newzero.domain.AtomFeed
import com.rajumark.newzero.domain.FeedChannel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class RssService(
    private val httpClient: HttpClient
) {
    suspend fun fetchByUrl(url: String, isDefault: Boolean): ArticleFeed {
        val response = httpClient.get(url = Url(url))
        val contentType = response.contentType()
        return if (contentType?.contentSubtype == "atom+xml") {
            val atom = response.body<AtomFeed>()
            atom.toArticleFeed(url, isDefault)
        } else {
            val feed = response.body<ArticleFeed>()
            feed.isPreloaded = isDefault
            feed.feedUrl = url
            feed
        }
    }

    private fun AtomFeed.toArticleFeed(url: String, isDefault: Boolean): ArticleFeed {
        val atomLink = link?.firstOrNull { it.rel == "alternate" || it.rel == null }?.href
        return ArticleFeed(
            version = null,
            feedUrl = url,
            isPreloaded = isDefault,
            channel = FeedChannel(
                title = title,
                description = subtitle,
                link = atomLink,
                item = entries.map { it.toArticleItem() }
            )
        )
    }

    private fun AtomEntry.toArticleItem(): ArticleItem = ArticleItem(
        title = title,
        pubDate = published,
        link = link?.firstOrNull { it.rel == "alternate" || it.rel == null }?.href,
        guid = id ?: "",
        description = summary ?: content?.value,
        contentEncoded = content?.value,
        mediaContent = null
    )
}
