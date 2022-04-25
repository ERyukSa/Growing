package com.eryuksa.growing.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.ItemTodoBinding

private const val ITEM_VIEW_TYPE_TODO = 0
private const val ITEM_VIEW_TYPE_DONE_HEADER = 1

class TodoAdapter : ListAdapter<TodoItem, RecyclerView.ViewHolder>(TodoDiffCallback()) {
    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is TodoItem.Todo -> ITEM_VIEW_TYPE_TODO
            is TodoItem.DoneHeader -> ITEM_VIEW_TYPE_DONE_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ITEM_VIEW_TYPE_TODO -> TodoViewHolder.from(parent)
            ITEM_VIEW_TYPE_DONE_HEADER -> HeaderViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TodoViewHolder) {
            val todo = getItem(position) as TodoItem.Todo
            holder.bind(todo)
        }
    }

    class TodoViewHolder private constructor(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: TodoItem.Todo) {
            binding.todo = todo
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : TodoViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTodoBinding.inflate(layoutInflater, parent, false)
                return TodoViewHolder(binding)
            }
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.item_done_header, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }
}

class TodoDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem
    }
}
