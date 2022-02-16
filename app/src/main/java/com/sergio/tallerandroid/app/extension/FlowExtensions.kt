package com.sergio.tallerandroid.app.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> Flow<T>.onChange(
    lifecycle: Lifecycle,
    lifecycleScope: LifecycleCoroutineScope,
    action: (T) -> Unit) =
    flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach { action(it) }.launchIn(lifecycleScope)