package com.dc.todomvvmretrofit.data.model

import com.google.gson.annotations.SerializedName

data class TodoListResponse(
    @SerializedName("statusCode")
    val statusCode: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: List<TodoModel>? = null
)