package com.dc.todomvvmretrofit.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.dc.todomvvmretrofit.databinding.ActivityBaseBinding
import com.dc.todomvvmretrofit.ui.view.login.LoginActivity
import com.dc.todomvvmretrofit.utils.*

abstract class BaseActivity : AppCompatActivity() {
    private val baseBinding: ActivityBaseBinding by lazy {
        ActivityBaseBinding.inflate(layoutInflater)
    }
    private val navigationViewAdapter = NavigationViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(baseBinding.root)

        val childView = onCreateChildView()
        baseBinding.baseLayout.addView(childView.view)
        baseBinding.baseTitle.text = childView.title
        enableDisableBack(childView.showBack)

        baseClickListener()
        initNavigationRecyclerView()
    }

    private fun initNavigationRecyclerView() {
        val list = listOf(
            NavigationModel(
                id = 1,
                name = "Logout",
            ),
        )


        baseBinding.navigationViewLayout.navigationRecycler.adapter = navigationViewAdapter
        navigationViewAdapter.submitList(list)
    }

    private fun baseClickListener() {
        baseBinding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        baseBinding.menuButton.setOnClickListener {
            baseBinding.drawerLayout.openDrawer(GravityCompat.START, true)
        }
        navigationViewAdapter.setOnItemClickListener {
            baseBinding.drawerLayout.closeDrawer(GravityCompat.START, true)
            when (it.id) {
                1 -> {
                    setUserdata(null)
                    openActivity(className = LoginActivity::class.java, clearTask = true)
                }
            }

        }
    }

    private fun enableDisableBack(showBack: Boolean) {
        if (showBack) {
            baseBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            baseBinding.backButton.show()
            baseBinding.menuButton.invisible()
            baseBinding.menuButton.disable()
        } else {
            baseBinding.backButton.invisible()
            baseBinding.menuButton.show()
            baseBinding.menuButton.enable()
        }
    }

    abstract fun onCreateChildView(): ChildView

    data class ChildView(
        val view: View,
        val title: String? = "",
        val showBack: Boolean = true,
    )
}