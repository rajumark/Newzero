package com.rajumark.newzero.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.rajumark.newzero.Res
import com.rajumark.newzero.all
import com.rajumark.newzero.domain.ArticleFeed
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeedIcon(
    feed: ArticleFeed?,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val txtAll = stringResource(Res.string.all)
    val shortName = remember(feed) { feed?.shortName() ?: txtAll }
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .border(1.5.dp, borderColor, CircleShape)
            .background(
                color = MaterialTheme.colorScheme.surface
            )
            .clickable(enabled = onClick != null, onClick = onClick ?: {})
    ) {
        feed?.channel?.image?.url?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            text = shortName
        )
    }
}

private fun ArticleFeed.shortName(): String =
    channel?.title?.replace(" ", "")?.take(2)?.uppercase() ?: ""

@Composable
fun EditIcon(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            imageVector = Icons.Default.Add,
            contentDescription = null
        )
    }
}
