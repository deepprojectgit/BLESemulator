package com.demo.myapplication.service


import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.demo.myapplication.ble.BleServerManager

class BleForegroundService : Service() {
    lateinit var bleServerManager: BleServerManager

    companion object {
        var isServiceRunning: Boolean = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bleServerManager = BleServerManager(this@BleForegroundService)
        bleServerManager.startAdvertising()
        bleServerManager.startGattServer()
        bleServerManager.getEvent()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        bleServerManager.disconnectDevice()
        bleServerManager.stopAdvertising()
    }

}