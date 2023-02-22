package com.dc.todomvvmretrofit.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dc.todomvvmretrofit.base.ItemClickListener
import com.dc.todomvvmretrofit.data.model.TodoModel
import com.dc.todomvvmretrofit.databinding.ChildTodoListBinding
import com.dc.todomvvmretrofit.utils.DELETE
import com.dc.todomvvmretrofit.utils.UPDATE

class TodoListAdapter(
    private val todoList: List<TodoModel>,
    private val itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        return TodoListViewHolder(
            ChildTodoListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        holder.setDataToViews(position)
        holder.setOnClickListener()
    }

    inner class TodoListViewHolder(private val binding: ChildTodoListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun setDataToViews(position: Int) {
            binding.title.text = todoList[position].title
            binding.description.text = todoList[position].description
            binding.dateTime.text = todoList[position].dateTime
            binding.priority.text = todoList[position].priority

        }

        fun setOnClickListener() {
            binding.deleteButton.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, DELETE)
            }
            binding.editButton.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, UPDATE)
            }
        }
    }
}
