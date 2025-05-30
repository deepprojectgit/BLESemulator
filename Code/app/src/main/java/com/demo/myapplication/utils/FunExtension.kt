package com.demo.myapplication.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

inline fun LifecycleOwner.lifeCycleLaunch(crossinline block: suspend () -> Unit) {
    lifecycleScope.launch {
        block()
    }
}