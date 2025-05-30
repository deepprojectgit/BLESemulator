package com.demo.myapplication.ui.fragment

import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.myapplication.base.BaseFragment
import com.demo.myapplication.databinding.FragmentDevicesBinding
import com.demo.myapplication.ui.adapter.BleDeviceAdaptor
import com.demo.myapplication.utils.GlobalEventBus
import com.demo.myapplication.utils.lifeCycleLaunch
import com.demo.myapplication.utils.reqAct

class DevicesFragment : BaseFragment<FragmentDevicesBinding>(FragmentDevicesBinding::inflate) {

    private val  bleDeviceAdaptor: BleDeviceAdaptor by lazy {
        BleDeviceAdaptor(reqAct())
    }

    override fun initView() {
        binding.rvDevices.apply {
            this.adapter = bleDeviceAdaptor
            this.layoutManager = LinearLayoutManager(reqAct())
            this.itemAnimator = DefaultItemAnimator()
        }
        setupObservers()
        if(bleDeviceAdaptor.getList().size>0){
            binding.rvDevices.visibility = View.VISIBLE
            binding.tvNoDataFound.visibility = View.GONE
        }else{
            binding.rvDevices.visibility = View.GONE
            binding.tvNoDataFound.visibility = View.VISIBLE
        }
    }

   fun setupObservers() {
        lifeCycleLaunch{
            GlobalEventBus.eventDevice.eventReceive.collect {
                if(it.isRemove){
                    it.bleDevice?.let { device->
                        bleDeviceAdaptor.removeItem(device)
                        if(bleDeviceAdaptor.getList().size>0){
                            binding.rvDevices.visibility = View.VISIBLE
                            binding.tvNoDataFound.visibility = View.GONE
                        }else{
                            binding.rvDevices.visibility = View.GONE
                            binding.tvNoDataFound.visibility = View.VISIBLE
                        }
                    }
                }
                if(it.isAdd){
                    it.bleDevice?.let { device->
                        val count = bleDeviceAdaptor.getList().count { it.address == device.address }
                        if(count <= 0){
                            bleDeviceAdaptor.addItem(device)
                        }
                    }
                    binding.rvDevices.visibility = View.VISIBLE
                    binding.tvNoDataFound.visibility = View.GONE
                }
            }
        }
    }

}