package com.demo.myapplication.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.demo.myapplication.R
import com.demo.myapplication.base.BaseActivity
import com.demo.myapplication.databinding.ActivitySplashBinding
import com.demo.myapplication.utils.UtilsFunc
import com.demo.myapplication.utils.showAlertDialog
import com.demo.myapplication.utils.showSettingsDialog

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
            permissions.entries.forEach { entry ->
                val permissionName: String = entry.key
                val isGranted: Boolean = entry.value
                if (isGranted) {
                    nextScreenNavigation()
                } else {
                    handlePermissionDenied(
                        this@SplashActivity,
                        permissionName
                    )
                }
            }
        }

    override fun initView() {
        setOnClicks()
    }



    fun setOnClicks() {
        binding.btnGoToNextScreen.setOnClickListener {
            checkPermissions(this@SplashActivity) {
                nextScreenNavigation()
            }
        }
    }

    override fun onBackPress() {
        finish()
    }

    private fun handlePermissionDenied(
        activity: Activity,
        permission: String
    ) {
        when {
            activity.shouldShowRequestPermissionRationale(permission) -> {
                showPermissionAlert()
            }

            else -> {
                showSettingsDialog {
                    nextScreenNavigation()
                }
            }
        }
    }

    private fun nextScreenNavigation() {
       startActivity(Intent(this@SplashActivity,HomeActivity::class.java))
       finish()
    }

    private fun checkPermissions(
        context: Context,
        onAllPermissionsGranted: () -> Unit
    ) {

        val bluetoothAdvertisePermission = Manifest.permission.BLUETOOTH_ADVERTISE
        val bluetoothConnectPermission = Manifest.permission.BLUETOOTH_CONNECT
        val accessFineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val permissionsToRequest = mutableListOf<String>()



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    bluetoothAdvertisePermission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(bluetoothAdvertisePermission)
            }
            if (ContextCompat.checkSelfPermission(
                    context,
                    bluetoothConnectPermission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(bluetoothConnectPermission)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    context,
                    accessFineLocationPermission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(accessFineLocationPermission)
            }
        }

        // Request permissions if needed
        if (permissionsToRequest.isNotEmpty()) {
            showPermissionAlert()
        } else {
            onAllPermissionsGranted()
        }
    }

    private fun showPermissionAlert() {
        showAlertDialog(
            strTitle = getString(R.string.permission_required),
            strMessage = getString(R.string.permission_message),
            strPositiveBtnText = getString(R.string.btn_grant),
            strNegativeBtnText = getString(R.string.btn_cancel),
            positiveButtonCallBack = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionsLauncher.launch(
                        arrayOf(
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            },
            negativeButtonCallBack = {
                nextScreenNavigation()
            }
        )
    }
}