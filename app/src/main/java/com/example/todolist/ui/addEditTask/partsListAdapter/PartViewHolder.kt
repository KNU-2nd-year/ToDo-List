package com.example.todolist.ui.addEditTask.partsListAdapter

import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.todolist.databinding.ItemTextPartBinding
import com.example.todolist.databinding.ItemTodoPartBinding
import com.example.todolist.ui.entities.BasePart
import com.example.todolist.ui.entities.TextPart
import com.example.todolist.ui.entities.TodoPart

sealed class PartViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    abstract fun onBindViewHolder(basePart: BasePart)

    class TextViewHolder(
        private val binding: ItemTextPartBinding,
        private val onPartClickListener: OnPartClickListener
    ) : PartViewHolder(binding) {
        override fun onBindViewHolder(basePart: BasePart) {
            val currentTextPart = basePart as TextPart

            binding.apply {
                edittextItemtextpartContent.setText(currentTextPart.content)
                /*edittextItemtextpartContent.addTextChangedListener {
                    onPartClickListener.onPartContentChanged(currentTextPart.copy(content = it.toString()))
                }*/
                edittextItemtextpartContent.setOnFocusChangeListener { view, hasFocus ->
                    if (!hasFocus) {
                        onPartClickListener.onPartContentChanged(currentTextPart
                            .copy(content = edittextItemtextpartContent.text.toString()))
                    }
                }
            }
        }
    }

    class TodoViewHolder(
        private val binding: ItemTodoPartBinding,
        private val onPartClickListener: OnPartClickListener
    ) : PartViewHolder(binding) {
        override fun onBindViewHolder(basePart: BasePart) {
            val currentTodoPart = basePart as TodoPart

            binding.apply {
                edittextItemtodopartContent.setText(currentTodoPart.content)
                checkboxItemtodopartCompleted.isChecked = currentTodoPart.isCompleted
                edittextItemtodopartContent.setOnFocusChangeListener { view, hasFocus ->
                    if (!hasFocus) {
                        onPartClickListener.onPartContentChanged(currentTodoPart.copy(
                            content = edittextItemtodopartContent.text.toString(),
                            isCompleted = checkboxItemtodopartCompleted.isChecked
                        ))
                    }
                }
            }
        }
    }
}