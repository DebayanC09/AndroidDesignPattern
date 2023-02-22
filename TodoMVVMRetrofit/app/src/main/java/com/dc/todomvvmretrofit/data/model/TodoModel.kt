package com.dc.todomvvmretrofit.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TodoModel(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("dateTime")
    val dateTime: String? = null,
    @SerializedName("priority")
    val priority: String? = null
) : Parcelable
