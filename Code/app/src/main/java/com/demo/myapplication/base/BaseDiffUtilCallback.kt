package com.demo.myapplication.base

import androidx.recyclerview.widget.DiffUtil

class BaseDiffUtilCallback<T>(
    private val oldList: List<T>,
    private val newList: List<T>,
    private val compareItems: (T, T) -> Boolean,
    private val compareContents: (T, T) -> Boolean
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return compareItems(oldList[oldItemPosition], newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return compareContents(oldList[oldItemPosition], newList[newItemPosition])
    }
}