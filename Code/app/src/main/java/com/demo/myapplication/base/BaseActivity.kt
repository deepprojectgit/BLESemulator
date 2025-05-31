package com.demo.myapplication.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.demo.myapplication.R

abstract class BaseActivity<VB : ViewBinding>(val bindingInflater: (LayoutInflater) -> VB) : AppCompatActivity() {

    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        initView()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPress()
            }
        }
        setStatusBarColor(R.color.colorPrimary)
        onBackPressedDispatcher.addCallback(this@BaseActivity, callback)
    }


    fun setStatusBarColor(colorResId: Int) {
        window.statusBarColor = android.graphics.Color.parseColor("#2256A0")
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, colorResId)
    }

    abstract fun initView()
    abstract fun onBackPress()
}