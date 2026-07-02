package com.rajumark.newzero.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rajumark.newzero.domain.ArticleItem
import com.rajumark.newzero.domain.extractImage

@Composable
fun PostList(
    modifier: Modifier,
    posts: List<ArticleItem>,
    listState: LazyListState,
    onClick: (ArticleItem) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        state = listState,
    ) {
        itemsIndexed(posts) { i, post ->
            PostItem(post) { onClick(post) }
            if (i != posts.size - 1) Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PostItem(
    item: ArticleItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        item.title?.let { title ->
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        item.extractImage()?.let { url ->
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        item.description?.let { desc ->
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                text = desc
            )
        }
        item.pubDate?.let { pubDate ->
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = pubDate
            )
        }
    }
}
