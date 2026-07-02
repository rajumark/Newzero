package com.rajumark.newzero.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NanoState
interface NanoAction
interface NanoEffect

interface NanoStore<S : NanoState, A : NanoAction, E : NanoEffect> {
    fun stateFlow(): StateFlow<S>
    fun effectFlow(): Flow<E>
    fun dispatch(action: A)
}