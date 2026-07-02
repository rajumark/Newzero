package com.rajumark.newzero.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rajumark.newzero.app.ArticleAction
import com.rajumark.newzero.app.ArticleStore
import com.rajumark.newzero.domain.ArticleItem
import com.rajumark.newzero.domain.ArticleFeed
import kotlinx.coroutines.launch

@Composable
fun MainFeed(
    store: ArticleStore,
    onPostClick: (ArticleItem) -> Unit,
    onEditClick: () -> Unit,
) {
    val state = store.stateFlow().collectAsState()
    val posts = remember(state.value.sources, state.value.selectedFeed) {
        (state.value.selectedFeed?.channel?.item ?: state.value.sources.flatMap { it.channel?.item ?: emptyList() })
            .sortedByDescending { it.pubDate }
    }
    Column {
        FeedBar(
            feeds = state.value.sources,
            selectedFeed = state.value.selectedFeed,
            onFeedClick = { store.dispatch(ArticleAction.SelectFeed(it)) },
            onEditClick = onEditClick
        )
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        PostList(
            modifier = Modifier.weight(1f),
            posts = posts,
            listState = listState
        ) { post ->
            coroutineScope.launch { listState.scrollToItem(0) }
            onPostClick(post)
        }
    }
}

@Composable
fun FeedBar(
    feeds: List<ArticleFeed>,
    selectedFeed: ArticleFeed?,
    onFeedClick: (ArticleFeed?) -> Unit,
    onEditClick: () -> Unit
) {
    val items = buildList {
        add(Icons.All)
        addAll(feeds.map { Icons.FeedIcon(it) })
        add(Icons.Edit)
    }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        this.items(items) { item ->
            when (item) {
                is Icons.All -> FeedIcon(
                    feed = null,
                    isSelected = selectedFeed == null,
                    onClick = { onFeedClick(null) }
                )
                is Icons.FeedIcon -> FeedIcon(
                    feed = item.feed,
                    isSelected = selectedFeed == item.feed,
                    onClick = { onFeedClick(item.feed) }
                )
                is Icons.Edit -> EditIcon(onClick = onEditClick)
            }
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}

private sealed class Icons {
    object All : Icons()
    class FeedIcon(val feed: ArticleFeed) : Icons()
    object Edit : Icons()
}
