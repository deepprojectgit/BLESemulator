package com.demo.myapplication.data

import android.bluetooth.BluetoothDevice

data class BleEvent(
    val isRemove: Boolean = false,
    val isBleOFF: Boolean = false,
    val isBleOn: Boolean = false,
    val isAdd: Boolean = false,
    val bleDevice: BluetoothDevice?=null,
    val isSendTemperature: Boolean = false,
    val isSendPressure: Boolean = false,
    val isSendBatteryStatus: Boolean = false,
    val isSendPulseOximetry: Boolean = false,
)
