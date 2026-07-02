package com.rajumark.newzero.app

import com.rajumark.newzero.core.wrap

fun ArticleStore.watchState() = stateFlow().wrap()
fun ArticleStore.watchSideEffect() = effectFlow().wrap()