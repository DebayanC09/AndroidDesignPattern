package com.dc.todomvvmretrofit.ui.view.signup

import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dc.todomvvmretrofit.base.BaseActivity
import com.dc.todomvvmretrofit.data.network.RetrofitClient
import com.dc.todomvvmretrofit.data.repository.signup.SignUpRepository
import com.dc.todomvvmretrofit.databinding.ActivitySignUpBinding
import com.dc.todomvvmretrofit.ui.view.todo.TodoListActivity
import com.dc.todomvvmretrofit.ui.viewmodel.signup.SignUpViewModel
import com.dc.todomvvmretrofit.utils.*

class SignUpActivity : BaseActivity() {
    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private val viewModel: SignUpViewModel by lazy {
        ViewModelProvider(
            this,
            SignUpViewModel.ViewModelFactory(
                SignUpRepository.instance(RetrofitClient.invokeWithOutAuth()),
                application
            )
        )[SignUpViewModel::class.java]
    }


    override fun onCreateChildView(): ChildView {
        return ChildView(view = binding.root)
    }

    override fun onResume() {
        super.onResume()
        setOnClickListener()
    }


    private fun setOnClickListener() {
        binding.email.addTextChangedListener {
            if (binding.emailLayout.error != null) {
                binding.emailLayout.error = null
            }
        }
        binding.password.addTextChangedListener {
            if (binding.passwordLayout.error != null) {
                binding.passwordLayout.error = null
            }
        }

        binding.loginButton.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            viewModel.register(name, email, password)
                .observe(this, Observer(this::handleState))
        }
    }

    private fun handleState(state: SignUpViewModel.State) {
        when (state) {
            is SignUpViewModel.State.Loading -> setLoading(true)
            is SignUpViewModel.State.Success -> {
                setLoading(false)
                showToast(state.message)
                openActivity(className = TodoListActivity::class.java, clearTask = true)
            }
            is SignUpViewModel.State.Error -> {
                setLoading(false)
                showToast(state.message)
            }
            is SignUpViewModel.State.ValidationError -> {
                binding.nameLayout.error = state.nameError
                binding.emailLayout.error = state.emailError
                binding.passwordLayout.error = state.passwordError
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.nameLayout.disable()
            binding.emailLayout.disable()
            binding.passwordLayout.disable()
            binding.loginButton.invisible()
            binding.progressBar.show()
            binding.loginButton.disable()

        } else {
            binding.nameLayout.enable()
            binding.emailLayout.enable()
            binding.passwordLayout.enable()
            binding.loginButton.show()
            binding.progressBar.gone()
            binding.loginButton.enable()
        }
    }
}