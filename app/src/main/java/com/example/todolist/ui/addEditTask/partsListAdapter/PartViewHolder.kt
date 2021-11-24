package com.example.todolist.ui.addEditTask.partsListAdapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.todolist.databinding.ItemTextPartBinding
import com.example.todolist.ui.entities.BasePart
import com.example.todolist.ui.entities.TextPart

sealed class PartViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    abstract fun onBindViewHolder(basePart: BasePart)

    class TextViewHolder(private val binding: ItemTextPartBinding) : PartViewHolder(binding) {
        override fun onBindViewHolder(basePart: BasePart) {
            val currentTextPart = basePart as TextPart

            binding.apply {
                //textviewItemtextpartHeader.text =currentTextPart.
                edittextItemtextpartContent.setText(currentTextPart.content)
            }
        }
    }
}