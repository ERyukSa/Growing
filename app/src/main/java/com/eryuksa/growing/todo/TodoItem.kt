package com.eryuksa.growing.todo

import androidx.lifecycle.MutableLiveData

sealed class TodoItem {
    abstract val id: Long

    data class Todo(
        val todoId: Long,
        var contents: String,
        val done: MutableLiveData<Boolean> = MutableLiveData(false)
    ) : TodoItem() {
        override val id: Long
            get() = todoId
    }

    object DoneHeader : TodoItem() {
        override val id = Long.MIN_VALUE
    }
}
