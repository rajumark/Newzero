package com.rajumark.newzero.app

import com.rajumark.newzero.core.FeedManager
import com.rajumark.newzero.domain.ArticleFeed
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ArticleState(
    val progress: Boolean,
    val sources: List<ArticleFeed>,
    val selectedFeed: ArticleFeed? = null //null means selected all
) : NanoState

fun ArticleState.mainFeedPosts() = (selectedFeed?.channel?.item ?: sources.flatMap { it.channel?.item ?: emptyList() }).sortedByDescending { it.pubDate }

sealed class ArticleAction : NanoAction {
    data class Refresh(val forceReload: Boolean) : ArticleAction()
    data class Add(val url: String) : ArticleAction()
    data class Delete(val url: String) : ArticleAction()
    data class SelectFeed(val feed: ArticleFeed?) : ArticleAction()
    data class Data(val sources: List<ArticleFeed>) : ArticleAction()
    data class Error(val error: Exception) : ArticleAction()
}

sealed class ArticleEffect : NanoEffect {
    data class Error(val error: Exception) : ArticleEffect()
}

class ArticleStore(
    private val rssReader: FeedManager
) : NanoStore<ArticleState, ArticleAction, ArticleEffect>,
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private val state = MutableStateFlow(ArticleState(false, emptyList()))
    private val sideEffect = MutableSharedFlow<ArticleEffect>()

    override fun stateFlow(): StateFlow<ArticleState> = state

    override fun effectFlow(): Flow<ArticleEffect> = sideEffect

    override fun dispatch(action: ArticleAction) {
        Napier.d(tag = "ArticleStore", message = "NanoAction: $action")
        val oldState = state.value

        val newState = when (action) {
            is ArticleAction.Refresh -> {
                if (oldState.progress) {
                    launch { sideEffect.emit(ArticleEffect.Error(Exception("In progress"))) }
                    oldState
                } else {
                    launch { loadAllSources(action.forceReload) }
                    oldState.copy(progress = true)
                }
            }
            is ArticleAction.Add -> {
                if (oldState.progress) {
                    launch { sideEffect.emit(ArticleEffect.Error(Exception("In progress"))) }
                    oldState
                } else {
                    launch { addSource(action.url) }
                    ArticleState(true, oldState.sources)
                }
            }
            is ArticleAction.Delete -> {
                if (oldState.progress) {
                    launch { sideEffect.emit(ArticleEffect.Error(Exception("In progress"))) }
                    oldState
                } else {
                    launch { removeSource(action.url) }
                    ArticleState(true, oldState.sources)
                }
            }
            is ArticleAction.SelectFeed -> {
                if (action.feed == null || oldState.sources.contains(action.feed)) {
                    oldState.copy(selectedFeed = action.feed)
                } else {
                    launch { sideEffect.emit(ArticleEffect.Error(Exception("Unknown feed"))) }
                    oldState
                }
            }
            is ArticleAction.Data -> {
                if (oldState.progress) {
                    val selected = oldState.selectedFeed?.let {
                        if (action.sources.contains(it)) it else null
                    }
                    ArticleState(false, action.sources, selected)
                } else {
                    launch { sideEffect.emit(ArticleEffect.Error(Exception("Unexpected action"))) }
                    oldState
                }
            }
            is ArticleAction.Error -> {
                if (oldState.progress) {
                    launch { sideEffect.emit(ArticleEffect.Error(action.error)) }
                    ArticleState(false, oldState.sources)
                } else {
                    launch { sideEffect.emit(ArticleEffect.Error(Exception("Unexpected action"))) }
                    oldState
                }
            }
        }

        if (newState != oldState) {
            Napier.d(tag = "ArticleStore", message = "NewState: $newState")
            state.value = newState
        }
    }

    private suspend fun loadAllSources(forceReload: Boolean) {
        try {
            val allSources = rssReader.loadAllSources(forceReload)
            dispatch(ArticleAction.Data(allSources))
        } catch (e: Exception) {
            dispatch(ArticleAction.Error(e))
        }
    }

    private suspend fun addSource(url: String) {
        try {
            rssReader.addSource(url)
            val allSources = rssReader.loadAllSources(false)
            dispatch(ArticleAction.Data(allSources))
        } catch (e: Exception) {
            dispatch(ArticleAction.Error(e))
        }
    }

    private suspend fun removeSource(url: String) {
        try {
            rssReader.removeSource(url)
            val allSources = rssReader.loadAllSources(false)
            dispatch(ArticleAction.Data(allSources))
        } catch (e: Exception) {
            dispatch(ArticleAction.Error(e))
        }
    }
}
