package com.dc.todomvvmretrofit.ui.view.todo

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dc.todomvvmretrofit.base.BaseActivity
import com.dc.todomvvmretrofit.base.GeneralState
import com.dc.todomvvmretrofit.base.ItemClickListener
import com.dc.todomvvmretrofit.data.model.TodoModel
import com.dc.todomvvmretrofit.data.network.RetrofitClient
import com.dc.todomvvmretrofit.data.repository.todo.TodoRepository
import com.dc.todomvvmretrofit.databinding.ActivityTodoListBinding
import com.dc.todomvvmretrofit.ui.adapter.TodoListAdapter
import com.dc.todomvvmretrofit.ui.viewmodel.todo.TodoListViewModel
import com.dc.todomvvmretrofit.utils.*

class TodoListActivity : BaseActivity() {
    private val todoList = arrayListOf<TodoModel>()
    private lateinit var adapter: TodoListAdapter
    private val binding: ActivityTodoListBinding by lazy {
        ActivityTodoListBinding.inflate(layoutInflater)
    }
    private val viewModel: TodoListViewModel by lazy {
        ViewModelProvider(
            this,
            TodoListViewModel.ViewModelFactory(
                TodoRepository.instance(RetrofitClient.invokeWithAuth(this)),
            )
        )[TodoListViewModel::class.java]
    }

    override fun onCreateChildView(): ChildView {
        return ChildView(view = binding.root, showBack = false, title = "My Todo")
    }

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        observers()
        onClickListeners()
    }

    private fun onClickListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchTodoList()
        }
        binding.retryButton.setOnClickListener {
            viewModel.fetchTodoList()
        }
        binding.addButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(TYPE, ADD)
            openActivity(
                className = AddUpdateTodoActivity::class.java,
                bundleKey = BUNDLE_DATA,
                bundle = bundle
            )
        }
    }

    private fun observers() {
        viewModel.todoList.observe(this) { listItems ->
            if (!listItems.isNullOrEmpty()) {
                todoList.clear()
                todoList.addAll(listItems)
            } else {
                todoList.clear()
            }
            if (::adapter.isInitialized) {
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        viewModel.todoListState.observe(this) { state ->
            when (state) {
                is GeneralState.Loading -> {
                    showHideViews(showLoader = true)
                }
                is GeneralState.Error -> {
                    showHideViews(showRetry = true, showPlaceHolder = true)
                    binding.placeholder.text = state.message
                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
                is GeneralState.Success -> {
                    showHideViews(showRecycler = true)
                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TodoListAdapter(todoList, object : ItemClickListener {
            override fun onItemClick(position: Int, option: String) {
                if (option == DELETE) {
                    deleteTodo(todoList[position].id)

                } else if (option == UPDATE) {
                    updateTodo(todoList[position])
                }
            }
        })
        binding.recyclerView.adapter = adapter
    }

    private fun updateTodo(todoModel: TodoModel) {
        val bundle = Bundle()
        bundle.putString(TYPE, UPDATE)
        bundle.putParcelable(TODO_DATA, todoModel)
        openActivity(
            className = AddUpdateTodoActivity::class.java,
            bundleKey = BUNDLE_DATA,
            bundle = bundle
        )
    }

    private fun deleteTodo(todoId: String?) {
        todoId?.let {
            viewModel.deleteTodo(it).observe(this) { state ->
                when (state) {
                    is GeneralState.Loading -> showHideViews(showLoader = true, showRecycler = true)
                    is GeneralState.Error -> {
                        showHideViews(showLoader = false, showRecycler = true)
                        showToast(state.message)
                    }
                    is GeneralState.Success -> {
                        showHideViews(showLoader = false, showRecycler = true)
                        showToast(state.message)
                    }
                }
            }
        }
    }

    private fun showHideViews(
        showRecycler: Boolean = false,
        showPlaceHolder: Boolean = false,
        showRetry: Boolean = false,
        showLoader: Boolean = false
    ) {
        if (showRecycler) {
            binding.recyclerView.show()
        } else {
            binding.recyclerView.gone()
        }

        if (showPlaceHolder) {
            binding.placeholder.show()
        } else {
            binding.placeholder.gone()
        }

        if (showRetry) {
            binding.retryButton.show()
        } else {
            binding.retryButton.gone()
        }

        if (showLoader) {
            binding.progress.show()
        } else {
            binding.progress.gone()
        }
    }

}