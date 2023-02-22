package com.dc.todomvvmretrofit.ui.view.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.dc.todomvvmretrofit.databinding.ActivitySplashBinding
import com.dc.todomvvmretrofit.ui.view.login.LoginActivity
import com.dc.todomvvmretrofit.ui.view.todo.TodoListActivity
import com.dc.todomvvmretrofit.utils.getUserdata
import com.dc.todomvvmretrofit.utils.openActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            if (getUserdata() != null) {
                openActivity(className = TodoListActivity::class.java, clearTask = true)
            } else {
                openActivity(className = LoginActivity::class.java, clearTask = true)

            }
        }, 1000)
    }
}