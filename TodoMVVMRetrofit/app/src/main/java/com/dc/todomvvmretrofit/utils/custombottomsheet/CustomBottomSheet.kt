package com.dc.todomvvmretrofit.utils.custombottomsheet

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dc.todomvvmretrofit.databinding.CustomBottomSheetChildBinding
import com.dc.todomvvmretrofit.databinding.CustomBottomSheetParentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomBottomSheet(
    private val context: Context,
    private val list: ArrayList<BottomSheetModel>
) {
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var onClickListener: BottomSheetClickListener

    init {
        setBottomSheetLayout()
    }

    private fun setBottomSheetLayout() {
        val binding = CustomBottomSheetParentBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = RecyclerViewAdapter(list)
    }

    fun show(): CustomBottomSheet {
        if (::bottomSheetDialog.isInitialized) {
            bottomSheetDialog.show()
        }
        return this
    }

    fun setOnClickListener(onClickListener: BottomSheetClickListener): CustomBottomSheet {
        this.onClickListener = onClickListener
        return this
    }

    inner class RecyclerViewAdapter(private val list: ArrayList<BottomSheetModel>) :
        RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {

            return RecyclerViewViewHolder(
                CustomBottomSheetChildBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
            holder.setDataToViews(position)
            holder.onClickListener()
        }

        inner class RecyclerViewViewHolder(private val binding: CustomBottomSheetChildBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun setDataToViews(position: Int) {
//                if (list[position].image != 0) {
//                    binding.childImage.setImageResource(list[position].image)
//                    binding.childImage.visibility = View.VISIBLE
//                } else {
//                    binding.childImage.visibility = View.GONE
//                }

                binding.childText.text = list[position].name
            }

            fun onClickListener() {
                binding.childText.setOnClickListener {
                    val bottomSheetModel = list[adapterPosition]
                    onClickListener.onClick(bottomSheetModel)
                    bottomSheetDialog.dismiss()
                }
            }

        }
    }

    interface BottomSheetClickListener {
        fun onClick(model: BottomSheetModel)
    }
}

