package com.rajumark.newzero.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rajumark.newzero.Res
import com.rajumark.newzero.app.ArticleAction
import com.rajumark.newzero.app.ArticleStore
import com.rajumark.newzero.app_name
import com.rajumark.newzero.back_button
import com.rajumark.newzero.feed_list
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import androidx.compose.ui.platform.LocalUriHandler

enum class Screen(val title: StringResource) {
    Main(Res.string.app_name), FeedList(Res.string.feed_list), Settings(Res.string.feed_list);
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssFeedAppBar(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back_button)
                    )
                }
            }
        },
        actions = {
            if (currentScreen == Screen.Main) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreen(
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val store: ArticleStore = koinInject<ArticleStore>()
    val state by store.stateFlow().collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(Unit) {
        store.dispatch(ArticleAction.Refresh(false))
    }
    PullToRefreshBox(
        isRefreshing = state.progress,
        onRefresh = { store.dispatch(ArticleAction.Refresh(true)) },
        modifier = modifier,
        content = {
            MainFeed(
                store = store,
                onPostClick = { post ->
                    post.link?.let { url ->
                        uriHandler.openUri(url)
                    }
                },
                onEditClick = onEditClick
            )
        })
}

@Composable
fun FeedListScreen() {
    val store: ArticleStore = koinInject<ArticleStore>()
    FeedList(store = store)
}
