package com.demo.myapplication.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.demo.myapplication.service.BleForegroundService

class UtilsFunc {
    companion object{
        var isPowerOn = false

        fun startBleForegroundService(context: Context) {
            if (BleForegroundService.isServiceRunning) {
                return
            }
            try {
                val serviceIntent= Intent(context, BleForegroundService::class.java)
                context.stopService(serviceIntent)
                context.startService(serviceIntent)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        fun stopBleForegroundService(context: Context) {
            try {
                val serviceIntent= Intent(context, BleForegroundService::class.java)
                context.stopService(serviceIntent)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}