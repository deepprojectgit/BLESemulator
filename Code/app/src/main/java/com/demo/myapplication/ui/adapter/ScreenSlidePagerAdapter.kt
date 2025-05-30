package com.demo.myapplication.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ScreenSlidePagerAdapter (fa: FragmentActivity,private val fragmentList:List<Fragment>): FragmentStateAdapter(fa) {

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int): Fragment {

        return fragmentList[position]

    }
}
