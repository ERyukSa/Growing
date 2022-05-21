package com.eryuksa.growing.todo

import androidx.lifecycle.MutableLiveData

sealed class TodoItem {
    abstract val id: Long

    data class Todo(
        val todoId: Long,
        var contents: String,
        var done: MutableLiveData<Boolean> = MutableLiveData(false),
        var prevDone: Boolean = false // done.value가 바뀌기 전 상태 -> DiffUtil contents 비교할 때 사용
    ) : TodoItem() {
        override val id: Long
            get() = todoId

        override fun toString(): String {
            return "Todo(todoId=$todoId, contents=$contents, done=${done.value})"
        }

        val currentDone: Boolean
            get() = done.value ?: false
    }

    object DoneHeader : TodoItem() {
        override val id = Long.MIN_VALUE
    }
}
