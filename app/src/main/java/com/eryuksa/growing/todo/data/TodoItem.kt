package com.eryuksa.growing.todo.data

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.joda.time.DateTime

sealed class TodoItem {
    abstract val id: Long

    @Entity
    data class Todo(
        var contents: String,
        @PrimaryKey
        override val id: Long = System.currentTimeMillis(),
        var done: MutableLiveData<Boolean> = MutableLiveData(false),
        var position: Int = 0,
        val dateTime: DateTime = DateTime.now().withTimeAtStartOfDay()
    ) : TodoItem() {

        val currentDone: Boolean
            get() = done.value ?: false

        @Ignore
        // done.value가 바뀌기 전 상태 -> DiffUtil에서 contents 비교할 때 사용
        var prevDone: Boolean = currentDone.not()

        override fun toString(): String {
            return "Todo(todoId=$id, contents=$contents, done=${done.value}, position=$position, dateTime=${dateTime.millis})"
        }
    }

    object DoneHeader : TodoItem() {
        override val id = Long.MIN_VALUE
    }
}
