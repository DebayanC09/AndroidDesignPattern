package com.dc.todomvvmretrofit.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dc.todomvvmretrofit.databinding.ChildNavigationViewBinding

class NavigationViewAdapter :
    ListAdapter<NavigationModel, NavigationViewAdapter.ViewHolder>(DiffCallBack) {


    private var onItemClickListener: ((data: NavigationModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (data: NavigationModel) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChildNavigationViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.getDataToViews(getItem(position))
        holder.setOnClickListener(getItem(position))
    }

    override fun submitList(list: List<NavigationModel>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class ViewHolder(private val binding: ChildNavigationViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getDataToViews(navigationModel: NavigationModel) {
            binding.navigationViewName.text = (navigationModel.name)
        }

        fun setOnClickListener(navigationModel: NavigationModel) {
            binding.navigationViewRoot.setOnClickListener {
                onItemClickListener?.let {
                    it(navigationModel)
                }
            }
        }
    }

    private object DiffCallBack : DiffUtil.ItemCallback<NavigationModel>() {
        override fun areItemsTheSame(oldItem: NavigationModel, newItem: NavigationModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: NavigationModel,
            newItem: NavigationModel
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
}