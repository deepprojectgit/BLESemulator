package com.demo.myapplication.utils

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SingleEventFlow<T> {
    private val _sharedFlow = MutableSharedFlow<T>(
        replay = 0, // No replay, so the value is emitted only once
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val eventReceive = _sharedFlow.asSharedFlow()

    suspend fun emit(value: T) {
        _sharedFlow.emit(value)
    }

    fun tryEmit(value: T) {
        _sharedFlow.tryEmit(value)
    }
}