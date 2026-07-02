@file:Suppress("DEPRECATION")

package com.rajumark.newzero.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rajumark.newzero.domain.FeedChannel
import com.rajumark.newzero.domain.FeedImage
import com.rajumark.newzero.domain.ArticleItem
import com.rajumark.newzero.domain.ArticleMedia
import com.rajumark.newzero.domain.ArticleFeed

@Preview
@Composable
private fun FeedItemPreview() {
    AppTheme {
        FeedItem(feed = PreviewData.feed) {}
    }
}

@Preview
@Composable
private fun PostPreview() {
    AppTheme {
        PostItem(item = PreviewData.post, onClick = {})
    }
}

@Preview
@Composable
private fun FeedIconPreview() {
    AppTheme {
        FeedIcon(feed = PreviewData.feed)
    }
}

@Preview
@Composable
private fun FeedIconSelectedPreview() {
    AppTheme {
        FeedIcon(feed = PreviewData.feed, true)
    }
}

private object PreviewData {
    val mediaContent = ArticleMedia(type = "image", url = "https://via.placeholder.com/300" )
    val post = ArticleItem(
        title = "Sample Post Title",
        description = "Kotlin was created as an alternative to Java, meaning that its application area within the JVM ecosystem was meant to be the same as Java’s. Obviously, this includes server-side development. We would love...",
        mediaContent = mediaContent,
        link = "https://example.com/post",
        pubDate = "42",
        guid = "https://example.com/post",
        contentEncoded = "Blah"
    )
    val image = FeedImage(url = "https://via.placeholder.com/32", title = "Sample Blog", link = "https://example.com", width = 32, height = 32)
    val channel = FeedChannel(
        title = "Sample Blog",
        link = "example.com",
        description = "example.com",
        copyright = "Copyright 2025",
        item = listOf(post),
        image = image
    )
    val feed = ArticleFeed(
        version = "2.0",
        channel = channel,
        feedUrl = "https://example.com/feed/",
        isPreloaded = false
    )
}
