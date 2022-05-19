package com.eryuksa.growing.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.growing.databinding.ItemDoneHeaderBinding
import com.eryuksa.growing.databinding.ItemTodoBinding

private const val ITEM_VIEW_TYPE_TODO = 0
private const val ITEM_VIEW_TYPE_DONE_HEADER = 1

class TodoAdapter(
    private val viewModel: TodoViewModel,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<TodoItem, RecyclerView.ViewHolder>(TodoDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TodoItem.Todo -> ITEM_VIEW_TYPE_TODO
            is TodoItem.DoneHeader -> ITEM_VIEW_TYPE_DONE_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_TODO -> TodoViewHolder.from(parent, viewModel, lifecycleOwner)
            ITEM_VIEW_TYPE_DONE_HEADER -> HeaderViewHolder.from(parent, viewModel, lifecycleOwner)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TodoViewHolder) {
            val todo = getItem(position) as TodoItem.Todo
            holder.bind(todo)
        }
    }

    class TodoViewHolder private constructor(val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: TodoItem.Todo) {
            binding.todo = todo
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, _viewModel: TodoViewModel, _lifecycleOwner: LifecycleOwner): TodoViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTodoBinding.inflate(layoutInflater, parent, false).apply {
                    viewModel = _viewModel
                    lifecycleOwner = _lifecycleOwner
                }

                return TodoViewHolder(binding).also {
                    binding.viewHolder = it
                }
            }
        }
    }

    class HeaderViewHolder private constructor(val binding: ItemDoneHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup, _viewModel: TodoViewModel, _lifecycleOwner: LifecycleOwner): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDoneHeaderBinding.inflate(layoutInflater, parent, false).apply {
                    viewModel = _viewModel
                    lifecycleOwner = _lifecycleOwner
                }
                return HeaderViewHolder(binding)
            }
        }
    }
}

class TodoDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
    // 같은 객체인지
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    // 동일한 데이터를 갖고 있는지
    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem
    }
}
