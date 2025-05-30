package com.demo.myapplication.utils

import com.demo.myapplication.data.BleEvent

object GlobalEventBus {
    val eventDevice = SingleEventFlow<BleEvent>()
}