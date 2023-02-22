package com.dc.todomvvmretrofit.ui.view.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.dc.todomvvmretrofit.R
import com.dc.todomvvmretrofit.base.BaseActivity
import com.dc.todomvvmretrofit.data.model.TodoModel
import com.dc.todomvvmretrofit.data.network.RetrofitClient
import com.dc.todomvvmretrofit.data.repository.todo.TodoRepository
import com.dc.todomvvmretrofit.databinding.ActivityAddUpdateTodoBinding
import com.dc.todomvvmretrofit.ui.viewmodel.todo.AddUpdateTodoViewModel
import com.dc.todomvvmretrofit.utils.*
import com.dc.todomvvmretrofit.utils.custombottomsheet.BottomSheetModel
import com.dc.todomvvmretrofit.utils.custombottomsheet.CustomBottomSheet
import java.util.*

class AddUpdateTodoActivity : BaseActivity() {
    private var todoId: String? = null
    private var type: String? = ""
    private val binding: ActivityAddUpdateTodoBinding by lazy {
        ActivityAddUpdateTodoBinding.inflate(layoutInflater)
    }

    private val viewModel: AddUpdateTodoViewModel by lazy {
        ViewModelProvider(
            this,
            AddUpdateTodoViewModel.ViewModelFactory(
                TodoRepository.instance(RetrofitClient.invokeWithAuth(this)),
            )
        )[AddUpdateTodoViewModel::class.java]
    }

    override fun onCreateChildView(): ChildView {
        var title = ""
        getIntentData()
        type?.let {
            if (it == ADD) {
                title = "Add Todo"
            } else if (it == UPDATE) {
                title = "Update Todo"
            }
        }
        return ChildView(view = binding.root, title = title)
    }

    override fun onResume() {
        super.onResume()
        onClickListener()
    }

    private fun getIntentData() {
        val bundle = intent.getBundleExtra(BUNDLE_DATA)
        type = bundle?.getString(TYPE)
        type?.let {
            if (it == ADD) {
                binding.addUpdateButton.text = getString(R.string.add)
            } else if (it == UPDATE) {
                binding.addUpdateButton.text = getString(R.string.update)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    setDataToFields(bundle?.getParcelable(TODO_DATA, TodoModel::class.java))
                }else{
                    setDataToFields(bundle?.getParcelable(TODO_DATA))
                }
            }
        }
    }

    private fun setDataToFields(todoData: TodoModel?) {
        todoData?.let {
            it.id?.let { todoId ->
                this.todoId = todoId
            }
            it.title?.let { title ->
                binding.title.setText(title)
            }
            it.description?.let { description ->
                binding.description.setText(description)
            }
            it.dateTime?.let { dateTime ->
                stringToCalender(
                    dateTime = dateTime,
                    formatType = DateTimeFormat.SERVER
                )?.let { calendar ->
                    binding.datetime.setText(
                        convertDateTime(
                            dateTime = calendar,
                            formatType = DateTimeFormat.DISPLAY
                        )
                    )
                }

            }
            it.priority?.let { priority ->
                binding.priority.setText(priority)
            }
        }
    }

    private fun onClickListener() {
        binding.datetime.setOnClickListener {
            pickDateTime()
        }
        binding.datetime.setOnFocusChangeListener { _, b ->
            if (b) {
                pickDateTime()
            }
        }

        binding.priority.setOnClickListener {
            showPriorityBottomSheet()
        }
        binding.priority.setOnFocusChangeListener { _, b ->
            if (b) {
                showPriorityBottomSheet()
            }
        }

        binding.addUpdateButton.setOnClickListener {
            addUpdateTodo()
        }

        binding.title.addTextChangedListener {
            if (binding.titleLayout.error != null) {
                binding.titleLayout.error = null
            }
        }
        binding.description.addTextChangedListener {
            if (binding.descriptionLayout.error != null) {
                binding.descriptionLayout.error = null
            }
        }
        binding.datetime.addTextChangedListener {
            if (binding.datetimeLayout.error != null) {
                binding.datetimeLayout.error = null
            }
        }
        binding.priority.addTextChangedListener {
            if (binding.priorityLayout.error != null) {
                binding.priorityLayout.error = null
            }
        }

    }

    private fun addUpdateTodo() {
        val title: String = binding.title.text.toString().trim()
        val description: String = binding.description.text.toString().trim()
        var dateTime = ""
        val priority: String = binding.priority.text.toString().trim()

        stringToCalender(
            dateTime = binding.datetime.text.toString().trim(),
            formatType = DateTimeFormat.DISPLAY
        )?.let { calendar ->
            dateTime = convertDateTime(
                dateTime = calendar,
                formatType = DateTimeFormat.SERVER
            )
        }

        if (type == ADD) {
            viewModel.addTodo(
                title = title,
                description = description,
                dateTime = dateTime,
                priority = priority
            ).observe(this, ::handleState)
        } else if (type == UPDATE) {
            todoId?.let {
                viewModel.updateTodo(
                    todoId = it,
                    title = title,
                    description = description,
                    dateTime = dateTime,
                    priority = priority
                ).observe(this, ::handleState)
            }
        }
    }

    private fun handleState(state: AddUpdateTodoViewModel.State) {
        when (state) {
            is AddUpdateTodoViewModel.State.Loading -> setLoading(true)
            is AddUpdateTodoViewModel.State.Error -> {
                setLoading(false)
                showToast(state.message)
            }
            is AddUpdateTodoViewModel.State.ValidationError -> {
                setLoading(false)
                binding.titleLayout.error = state.titleError
                binding.descriptionLayout.error = state.descriptionError
                binding.datetimeLayout.error = state.dateTimeError
                binding.priorityLayout.error = state.priorityError
            }
            is AddUpdateTodoViewModel.State.Success -> {
                setLoading(false)
                showToast(state.message)
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.titleLayout.disable()
            binding.descriptionLayout.disable()
            binding.datetimeLayout.disable()
            binding.priorityLayout.disable()
            binding.progressBar.show()
            binding.addUpdateButton.disable()
            binding.addUpdateButton.invisible()

        } else {
            binding.titleLayout.enable()
            binding.descriptionLayout.enable()
            binding.datetimeLayout.enable()
            binding.priorityLayout.enable()
            binding.progressBar.gone()
            binding.addUpdateButton.enable()
            binding.addUpdateButton.show()
        }
    }

    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(this, { _, year, month, day ->
            TimePickerDialog(this, { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                setDateTime(pickedDateTime)
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

    private fun setDateTime(pickedDateTime: Calendar) {

        binding.datetime.setText(
            convertDateTime(
                dateTime = pickedDateTime,
                formatType = DateTimeFormat.DISPLAY
            )
        )
    }

    private fun showPriorityBottomSheet() {
        val list = ArrayList<BottomSheetModel>()

        list.add(BottomSheetModel("High"))
        list.add(BottomSheetModel("Medium"))
        list.add(BottomSheetModel("Low"))

        CustomBottomSheet(this, list).setOnClickListener(object :
            CustomBottomSheet.BottomSheetClickListener {
            override fun onClick(model: BottomSheetModel) {
                binding.priority.setText(model.name)
            }
        }).show()
    }

}