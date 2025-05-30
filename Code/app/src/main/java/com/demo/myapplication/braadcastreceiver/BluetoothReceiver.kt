package com.demo.myapplication.braadcastreceiver

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.demo.myapplication.data.BleEvent
import com.demo.myapplication.utils.GlobalEventBus
import com.demo.myapplication.utils.UtilsFunc

class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
            when (state) {
                BluetoothAdapter.STATE_OFF -> {
                    // Bluetooth is OFF
                    Toast.makeText(context, "Bluetooth is OFF", Toast.LENGTH_SHORT).show()
                    GlobalEventBus.eventDevice.tryEmit(BleEvent(isBleOFF = true))
                    UtilsFunc.stopBleForegroundService(context)
                }
                BluetoothAdapter.STATE_ON -> {
                    // Bluetooth is ON
                    GlobalEventBus.eventDevice.tryEmit(BleEvent(isBleOn = true))
                    Toast.makeText(context, "Bluetooth is ON", Toast.LENGTH_SHORT).show()
                    UtilsFunc.startBleForegroundService(context)
                }
            }
        }
    }
}