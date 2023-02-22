package com.dc.todomvvmretrofit.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.dc.todomvvmretrofit.data.model.UserModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


fun Context.getUserdata(): UserModel? {
    val sharedPreferences: SharedPreferences =
        getSharedPreferences(USER_CREDENTIAL, Context.MODE_PRIVATE)
    return Gson().fromJson(sharedPreferences.getString("userData", null), UserModel::class.java)
}

fun Context.setUserdata(userModel: UserModel?) {
    val sharedPreferences: SharedPreferences = getSharedPreferences(
        USER_CREDENTIAL, Context.MODE_PRIVATE
    )
    val editor: SharedPreferences.Editor = sharedPreferences.edit()
    val data: String = Gson().toJson(userModel)
    editor.putString("userData", data)
    editor.apply()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}

fun Context.showToast(message: String?) {
    if (message != null) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun <T> Context.openActivity(
    className: Class<T>,
    clearTask: Boolean = false,
    bundleKey: String = "",
    bundle: Bundle? = null
) {
    val intent = Intent(this, className)
    if (clearTask) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    } else {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    intent.putExtra(bundleKey, bundle)
    startActivity(intent)
}

fun convertDateTime(dateTime: Calendar, formatType: DateTimeFormat): String {
    var dateFormat: SimpleDateFormat? = null
    if (formatType == DateTimeFormat.SERVER) {
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    } else if (formatType == DateTimeFormat.DISPLAY) {
        dateFormat = SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault())
    }
    dateFormat?.let {
        return it.format(dateTime.time).toString()
    }
    return ""
}

fun stringToCalender(dateTime: String, formatType: DateTimeFormat): Calendar? {
    var dateFormat: SimpleDateFormat? = null
    if (formatType == DateTimeFormat.SERVER) {
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    } else if (formatType == DateTimeFormat.DISPLAY) {
        dateFormat = SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault())
    }
    dateFormat?.let {
        val date: Date? = dateFormat.parse(dateTime)
        val calendar = Calendar.getInstance()
        date?.let {
            calendar.time = it
            return calendar
        }
        return null
    }
    return null
}