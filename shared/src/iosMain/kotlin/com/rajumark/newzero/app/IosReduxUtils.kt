package com.rajumark.newzero.app

import com.rajumark.newzero.core.wrap

fun FeedStore.watchState() = observeState().wrap()
fun FeedStore.watchSideEffect() = observeSideEffect().wrap()