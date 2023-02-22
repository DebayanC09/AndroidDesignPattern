package com.dc.todomvvmretrofit.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("statusCode")
    @Expose
    var statusCode: String? = null,

    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("token")
    @Expose
    var token: String? = null,
)