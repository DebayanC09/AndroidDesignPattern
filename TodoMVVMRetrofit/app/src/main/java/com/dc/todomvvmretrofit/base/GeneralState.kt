package com.dc.todomvvmretrofit.base

sealed class GeneralState<out T : Any> {
    data class Success<out T : Any>(
        val message: String? = "",
        val data: T? = null
    ) : GeneralState<T>()

    data class Error(
        val status: Int,
        val message: String? = null
    ) : GeneralState<Nothing>()

    object Loading : GeneralState<Nothing>()
}
