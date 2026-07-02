package com.rajumark.newzero.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rajumark.newzero.app.ArticleAction
import com.rajumark.newzero.app.ArticleStore
import com.rajumark.newzero.domain.ArticleFeed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedList(store: ArticleStore) {
    val state = store.stateFlow().collectAsState()
    val urlInput = remember { mutableStateOf("") }
    val feedForDelete = remember<MutableState<ArticleFeed?>> { mutableStateOf(null) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = urlInput.value,
                onValueChange = { urlInput.value = it },
                placeholder = { Text("RSS feed URL") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = {
                    if (urlInput.value.isNotBlank()) {
                        store.dispatch(
                            ArticleAction.Add(urlInput.value.replace("http://", "https://"))
                        )
                        urlInput.value = ""
                    }
                }
            ) {
                Text("Add")
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = state.value.progress,
                onRefresh = { store.dispatch(ArticleAction.Refresh(true)) },
                modifier = Modifier.fillMaxSize()
            ) {
                FeedItemList(feeds = state.value.sources) {
                    feedForDelete.value = it
                }
            }
            feedForDelete.value?.let { feed ->
                DeleteFeedDialog(
                    feed = feed,
                    onDelete = {
                        store.dispatch(ArticleAction.Delete(feed.feedUrl))
                        feedForDelete.value = null
                    },
                    onDismiss = { feedForDelete.value = null }
                )
            }
        }
    }
}

@Composable
fun FeedItemList(
    feeds: List<ArticleFeed>,
    onClick: (ArticleFeed) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        itemsIndexed(feeds) { i, feed ->
            FeedItem(feed) { onClick(feed) }
        }
    }
}

@Composable
fun FeedItem(
    feed: ArticleFeed,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick, enabled = !feed.isPreloaded)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FeedIcon(feed = feed)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            feed.channel?.title?.let { title ->
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            feed.channel?.description?.let { description ->
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
