package com.demo.myapplication.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.demo.myapplication.R
import com.demo.myapplication.base.BaseActivity
import com.demo.myapplication.braadcastreceiver.BluetoothReceiver
import com.demo.myapplication.databinding.ActivityHomeBinding
import com.demo.myapplication.ui.adapter.ScreenSlidePagerAdapter
import com.demo.myapplication.ui.fragment.ControllerFragment
import com.demo.myapplication.ui.fragment.DevicesFragment
import com.demo.myapplication.utils.GlobalEventBus
import com.demo.myapplication.utils.UtilsFunc
import com.demo.myapplication.utils.UtilsFunc.Companion.isPowerOn
import com.demo.myapplication.utils.disableSwipe
import com.demo.myapplication.utils.lifeCycleLaunch


class HomeActivity : BaseActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate) {
    var menuItem: MenuItem ?= null
    var menu: Menu ?= null
    lateinit var bluetoothReceiver:BluetoothReceiver

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        val fragments = listOf(
            DevicesFragment(),
            ControllerFragment()
        )

        binding.viewPager.apply {
            this.adapter = ScreenSlidePagerAdapter(this@HomeActivity, fragments)
            this.offscreenPageLimit = fragments.size
            this.stopNestedScroll()
            this.disableSwipe()
        }

        binding.bottomMenu.selectedItemId = R.id.device

        setOnClicks()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        enableBluetooth()
        registerReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver()
    }

    private fun setupObservers() {
       lifeCycleLaunch {
           GlobalEventBus.eventDevice.eventReceive.collect{
               if(it.isBleOn){
                   isPowerOn = true
                   menuItem?.icon?.setTint(ContextCompat.getColor(this, R.color.green))
               }

               if(it.isBleOFF){
                   isPowerOn = false
                   menuItem?.icon?.setTint(ContextCompat.getColor(this, R.color.gray))
               }
           }
       }
    }

    private val enableBluetoothLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            isPowerOn = true
            menuItem  = menu?.findItem(R.id.action_power)
            updatePowerIcon(menuItem)
        } else {
            enableBluetooth()
        }
    }

    private fun enableBluetooth(){
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (!bluetoothAdapter.isEnabled && bluetoothAdapter != null) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        }else{
            if(bluetoothAdapter != null){
                isPowerOn = true
                updatePowerIcon(menuItem)
            }
        }
    }

    private fun setOnClicks() {
        binding.bottomMenu.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.device -> {
                    binding.viewPager.setCurrentItem(0,false)
                }
                R.id.controller -> {
                    binding.viewPager.setCurrentItem(1,false)
                }
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.bottomMenu.selectedItemId = R.id.device
                    1 -> binding.bottomMenu.selectedItemId = R.id.controller
                }
            }
        })
    }

    override fun onBackPress() {
         finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        this.menu = menu
        return true
    }



    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val powerItem = menu.findItem(R.id.action_power)
        updatePowerIcon(powerItem)
        return super.onPrepareOptionsMenu(menu)
    }
    //Receiver
    private fun registerReceiver(){
        bluetoothReceiver = BluetoothReceiver()
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothReceiver, filter)
    }

    private fun unregisterReceiver(){
        unregisterReceiver(bluetoothReceiver)
    }

    @SuppressLint("MissingPermission")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_power) {
            menuItem = item
            if(!isPowerOn){
               enableBluetooth()
            }else{
               isPowerOn = false
               val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
               if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                   bluetoothAdapter.disable()
               }
               updatePowerIcon(item)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updatePowerIcon(item: MenuItem?) {
        if (isPowerOn) {
            UtilsFunc.startBleForegroundService(this@HomeActivity)
            item?.icon?.setTint(ContextCompat.getColor(this, R.color.green))
        } else {
            UtilsFunc.stopBleForegroundService(this@HomeActivity)
            item?.icon?.setTint(ContextCompat.getColor(this, R.color.gray))
        }
    }
}