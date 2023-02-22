package com.dc.todomvvmretrofit.base

interface ItemClickListener {
    fun onItemClick(position: Int, option: String = "")
}