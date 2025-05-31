package com.demo.myapplication.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import com.demo.myapplication.data.BleEvent
import com.demo.myapplication.utils.GlobalEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class BleServerManager(private val context: Context) {

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private val bluetoothAdapter = bluetoothManager.adapter

    private var gattServer: BluetoothGattServer? = null

    private var advertiser: BluetoothLeAdvertiser? = bluetoothAdapter.bluetoothLeAdvertiser

    private var connectedDevice : ArrayList<BluetoothDevice>? = null

    private val TEMPERATURE_SERVICE_UUID = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb")

    private val TEMPERATURE_CHAR_UUID = UUID.fromString("00002A1C-0000-1000-8000-00805f9b34fb")

    private lateinit var temperatureCharacteristic: BluetoothGattCharacteristic


    private val heartbeatHandler = Handler(Looper.getMainLooper())
    private val HEARTBEAT_INTERVAL_MS = 50_000L

    private lateinit var job: Job
    private lateinit var job1: Job

    val scope = CoroutineScope(Dispatchers.IO)
    val scopeEventReceive = CoroutineScope(Dispatchers.IO)

    init {
        connectedDevice = ArrayList()
    }

    private val heartbeatRunnable = object : Runnable {
        override fun run() {
            sendHeartbeat()
            heartbeatHandler.postDelayed(this, HEARTBEAT_INTERVAL_MS)
        }
    }



    @SuppressLint("MissingPermission")
    fun startGattServer() {
        gattServer = bluetoothManager.openGattServer(context, gattServerCallback)

        temperatureCharacteristic = BluetoothGattCharacteristic(
            TEMPERATURE_CHAR_UUID,
            BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )

        val service = BluetoothGattService(TEMPERATURE_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        service.addCharacteristic(temperatureCharacteristic)
        gattServer?.addService(service)
    }

    @SuppressLint("MissingPermission")
    fun stopGattServer() {
        stopHeartbeat()
        gattServer?.close()
        gattServer = null
    }

    fun getEvent(){
        job1 = scopeEventReceive.launch {
            GlobalEventBus.eventDevice.eventReceive.collect{
                if(it.isSendPressure){
                    sendPressure()
                }

                if(it.isSendTemperature){
                    sendTemperature()
                }

                if(it.isSendBatteryStatus){
                    sendBatteryStatus()
                }

                if(it.isSendPulseOximetry){
                    sendPulseOximetry()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startAdvertising() {
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(true)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addServiceUuid(ParcelUuid(TEMPERATURE_SERVICE_UUID))
            .build()

        advertiser?.startAdvertising(settings, data, advertiseCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopAdvertising() {
        advertiser?.stopAdvertising(advertiseCallback)
    }

    @SuppressLint("MissingPermission")
    fun sendTemperature() {
        connectedDevice?.let { devices ->
            devices.forEach {
                sendLargeJsonOverNotification("Temperature", it)
            }

        }
    }

    @SuppressLint("MissingPermission")
    fun sendPressure() {
        connectedDevice?.let { devices ->
            devices.forEach {
                sendLargeJsonOverNotification("BloodPressure", it)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun sendBatteryStatus() {
        connectedDevice?.let { devices ->
            devices.forEach {
                sendLargeJsonOverNotification("BatteryLevel", it)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun sendPulseOximetry() {
        connectedDevice?.let { devices ->
            devices.forEach {
                sendLargeJsonOverNotification("pulse_oximetry", it)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun sendHeartbeat() {
        connectedDevice?.let { devices ->
            devices.forEach {
                sendLargeJsonOverNotification("Heartbeat", it)
            }
        }
    }

    private fun startHeartbeat() {
        heartbeatHandler.postDelayed(heartbeatRunnable, HEARTBEAT_INTERVAL_MS)
    }

    private fun stopHeartbeat() {
        heartbeatHandler.removeCallbacks(heartbeatRunnable)
    }


    @SuppressLint("MissingPermission")
    fun notifyDisconnect(reason: String = "disconnect") {
        connectedDevice?.let { devices ->
            devices.forEach {
                sendLargeJsonOverNotification(reason, it)
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun disconnectDevice() {
        if(this::job.isInitialized){
            job.cancel()
        }
        if(this::job1.isInitialized){
            job1.cancel()
        }
        if((connectedDevice?.size?:0) > 0){
            notifyDisconnect("disconnecting...")
        }
        Handler(Looper.getMainLooper()).postDelayed({
            connectedDevice?.let { devices ->
                devices.forEach {
                    gattServer?.cancelConnection(it)
                }
                connectedDevice = ArrayList()
            }
        }, 500) // delay to ensure client receives notification
        stopGattServer()
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.d("BLE", "Advertising started")
        }

        override fun onStartFailure(errorCode: Int) {
            Log.e("BLE", "Advertising failed: $errorCode")
        }
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                job = scope.launch {
                    GlobalEventBus.eventDevice.emit(BleEvent(isAdd = true, bleDevice = device))
                }
                connectedDevice?.add(device)
                if(connectedDevice?.size == 1){
                    startHeartbeat()
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BLE", "Device disconnected: ${device.address}")
                connectedDevice?.remove(device)
                job = scope.launch {
                    GlobalEventBus.eventDevice.emit(BleEvent(isRemove = true, bleDevice = device))
                }
                if(connectedDevice?.isEmpty() == true){
                    stopHeartbeat()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun sendLargeJsonOverNotification(type: String, device: BluetoothDevice) {
        val jsonBytes = type.toByteArray(Charsets.UTF_8)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gattServer?.notifyCharacteristicChanged(device, temperatureCharacteristic, false,jsonBytes)
        }else {
            temperatureCharacteristic.value = jsonBytes
            gattServer?.notifyCharacteristicChanged(device, temperatureCharacteristic, false)
        }
    }

}
