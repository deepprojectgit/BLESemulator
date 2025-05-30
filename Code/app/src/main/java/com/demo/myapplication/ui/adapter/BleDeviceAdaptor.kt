package com.demo.myapplication.ui.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import com.demo.myapplication.base.BaseViewAdapter
import com.demo.myapplication.databinding.ItemDevicesBinding


class BleDeviceAdaptor (val context: Context) : BaseViewAdapter<BluetoothDevice, ItemDevicesBinding>({ inflater, parent, attach ->
    ItemDevicesBinding.inflate(
        inflater,
        parent,
        attach
    )
},
    compareItems = { old, new -> old.address == new.address },
    compareContents = { old, new -> old == new })
{

    @SuppressLint("MissingPermission")
    override fun onBind(binding: ItemDevicesBinding, item: BluetoothDevice, position: Int) {
        binding.tvDeviceName.text = item.name
        binding.tvDeviceAddress.text = item.address
    }
}