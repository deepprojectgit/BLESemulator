package com.demo.myapplication.ui.fragment

import android.widget.Toast
import com.demo.myapplication.base.BaseFragment
import com.demo.myapplication.data.BleEvent
import com.demo.myapplication.databinding.FragmentControllerBinding
import com.demo.myapplication.service.BleForegroundService
import com.demo.myapplication.utils.GlobalEventBus
import com.demo.myapplication.utils.lifeCycleLaunch
import com.demo.myapplication.utils.reqAct

class ControllerFragment : BaseFragment<FragmentControllerBinding>(FragmentControllerBinding::inflate) {

    override fun initView() {
        binding.btnSendPressure.setOnClickListener {
            if(BleForegroundService.isServiceRunning){
                lifeCycleLaunch {
                    GlobalEventBus.eventDevice.emit(BleEvent(isSendPressure = true))
                }
            }else{
                Toast.makeText(reqAct(), "Please Enable Bluetooth", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSendBatteryStatus.setOnClickListener {
            if(BleForegroundService.isServiceRunning){
                lifeCycleLaunch {
                    GlobalEventBus.eventDevice.emit(BleEvent(isSendBatteryStatus = true))
                }
            }else{
                Toast.makeText(reqAct(), "Please Enable Bluetooth", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnPulseOximetry.setOnClickListener {
            if(BleForegroundService.isServiceRunning){
                lifeCycleLaunch {
                    GlobalEventBus.eventDevice.emit(BleEvent(isSendPulseOximetry = true))
                }
            }else{
                Toast.makeText(reqAct(), "Please Enable Bluetooth", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSendTemperature.setOnClickListener {
            if(BleForegroundService.isServiceRunning){
                lifeCycleLaunch {
                    GlobalEventBus.eventDevice.emit(BleEvent(isSendTemperature = true))
                }
            }else{
                Toast.makeText(reqAct(), "Please Enable Bluetooth", Toast.LENGTH_SHORT).show()
            }

        }
    }



}