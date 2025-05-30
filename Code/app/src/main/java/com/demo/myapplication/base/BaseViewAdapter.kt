package com.demo.myapplication.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class BaseViewAdapter<T, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val compareItems: (T, T) -> Boolean,
    private val compareContents: (T, T) -> Boolean
) : RecyclerView.Adapter<BaseViewAdapter<T, VB>.ViewHolder>() {

    private var itemList = ArrayList<T>()

    abstract fun onBind(binding: VB, item: T, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: VB = bindingInflater(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val item = itemList[position]
            holder.bind(item, position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ViewHolder(private val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T, position: Int) {
            onBind(binding, item, position)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun getList(): ArrayList<T> {
        return itemList
    }



    fun removeItem(item: T) {
        val oldList = ArrayList(itemList)
        itemList.remove(item)
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtilCallback(oldList, itemList, compareItems, compareContents))
        diffResult.dispatchUpdatesTo(this)
    }


    fun addItem(newItem: T) {
        val oldList = ArrayList(itemList)
        itemList.add(newItem)
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtilCallback(oldList, itemList, compareItems, compareContents))
        diffResult.dispatchUpdatesTo(this)
    }

}
