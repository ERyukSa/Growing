package com.eryuksa.growing.todo

import androidx.lifecycle.MutableLiveData

sealed class TodoItem {
    abstract val id: Long

    data class Todo(
        val todoId: Long,
        var contents: String,
        val done: MutableLiveData<Boolean> = MutableLiveData(false),
        var prev: Todo? = null,
        var next: Todo? = null
    ) : TodoItem() {
        override val id: Long
            get() = todoId

        override fun toString(): String {
            return "Todo(todoId=$todoId, contents=$contents, done=${done.value})"
        }
    }

    object DoneHeader : TodoItem() {
        override val id = Long.MIN_VALUE
    }
}
