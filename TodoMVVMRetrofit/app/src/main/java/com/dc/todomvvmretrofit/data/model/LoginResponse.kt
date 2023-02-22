package com.dc.todomvvmretrofit.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("statusCode")
    @Expose
    var statusCode: String? = null,

    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("message")
    @Expose
    var message: String? = null,

    @SerializedName("user")
    @Expose
    var user: UserModel? = null
)