package com.rajumark.newzero.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rajumark.newzero.app.ArticleAction
import com.rajumark.newzero.app.ArticleStore
import com.rajumark.newzero.domain.ArticleFeed

@Composable
fun FeedList(store: ArticleStore) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val state = store.stateFlow().collectAsState()
        val showAddDialog = remember { mutableStateOf(false) }
        val feedForDelete = remember<MutableState<ArticleFeed?>> { mutableStateOf(null) }
        FeedItemList(feeds = state.value.sources) {
            feedForDelete.value = it
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding()
                .imePadding(),
            onClick = { showAddDialog.value = true }
        ) {
            Image(
                imageVector = Icons.Default.Add,
                modifier = Modifier.align(Alignment.Center),
                contentDescription = null
            )
        }
        if (showAddDialog.value) {
            AddFeedDialog(
                onAdd = {
                    store.dispatch(ArticleAction.Add(it))
                    showAddDialog.value = false
                },
                onDismiss = {
                    showAddDialog.value = false
                }
            )
        }
        feedForDelete.value?.let { feed ->
            DeleteFeedDialog(
                feed = feed,
                onDelete = {
                    store.dispatch(ArticleAction.Delete(feed.feedUrl))
                    feedForDelete.value = null
                },
                onDismiss = {
                    feedForDelete.value = null
                }
            )
        }
    }
}

@Composable
fun FeedItemList(
    feeds: List<ArticleFeed>,
    onClick: (ArticleFeed) -> Unit
) {
    LazyColumn {
        itemsIndexed(feeds) { i, feed ->
            if (i == 0) Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
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
        Modifier
            .clickable(onClick = onClick, enabled = !feed.isPreloaded)
            .padding(16.dp)
    ) {
        FeedIcon(feed = feed)
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            feed.channel?.title?.let { title ->
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = title
                )
            }
            feed.channel?.description?.let { description ->
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = description
                )
            }
        }
    }
}
