package com.demo.myapplication.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder

inline fun Context.showAlertDialog(
    strTitle:String = "",
    strMessage:String = "",
    strPositiveBtnText:String = "",
    strNegativeBtnText:String = "",
    crossinline positiveButtonCallBack : () -> Unit,
    crossinline negativeButtonCallBack : () -> Unit
) {
    if(strNegativeBtnText.isNotEmpty()){
        MaterialAlertDialogBuilder(this)
            .setTitle(strTitle)
            .setMessage(
                strMessage
            )
            .setPositiveButton(strPositiveBtnText) { _, _ ->
                positiveButtonCallBack.invoke()
            }
            .setNegativeButton(strNegativeBtnText) { _, _ ->
                negativeButtonCallBack.invoke()
            }
            .show()
    }else{
        MaterialAlertDialogBuilder(this)
            .setTitle(strTitle)
            .setMessage(
                strMessage
            )
            .setPositiveButton(strPositiveBtnText) { _, _ ->
                positiveButtonCallBack.invoke()
            }
            .show()
    }

}


inline fun Context.showSettingsDialog(crossinline callback : () -> Unit) {
    MaterialAlertDialogBuilder(this)
        .setTitle("Permission Denied")
        .setMessage("You have denied permission multiple times. Please go to settings and enable the permission manually.")
        .setPositiveButton("Open Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", this.`package`, null)
            }
            this.startActivity(intent)
        }
        .setNegativeButton("Cancel") { _, _ ->
            callback.invoke()
        }
        .show()
}

fun Fragment.reqAct() = requireActivity() as AppCompatActivity

fun ViewPager2.disableSwipe() {
    val view = this.getChildAt(0)
    view.setOnTouchListener { _, _ -> true }
}
