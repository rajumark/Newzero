@file:Suppress("DEPRECATION")

package com.rajumark.newzero.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rajumark.newzero.domain.Channel
import com.rajumark.newzero.domain.Image
import com.rajumark.newzero.domain.Item
import com.rajumark.newzero.domain.MediaContent
import com.rajumark.newzero.domain.RssFeed

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
    val mediaContent = MediaContent(type = "image", url = "https://via.placeholder.com/300" )
    val post = Item(
        title = "Sample Post Title",
        description = "Kotlin was created as an alternative to Java, meaning that its application area within the JVM ecosystem was meant to be the same as Java’s. Obviously, this includes server-side development. We would love...",
        mediaContent = mediaContent,
        link = "https://example.com/post",
        pubDate = "42",
        guid = "https://example.com/post",
        contentEncoded = "Blah"
    )
    val image = Image(url = "https://via.placeholder.com/32", title = "Sample Blog", link = "https://example.com", width = 32, height = 32)
    val channel = Channel(
        title = "Sample Blog",
        link = "example.com",
        description = "example.com",
        copyright = "Copyright 2025",
        item = listOf(post),
        image = image
    )
    val feed = RssFeed(
        version = "2.0",
        channel = channel,
        sourceUrl = "https://example.com/feed/",
        isDefault = false
    )
}
