package com.eryuksa.growing.todo

sealed class TodoItem {
    abstract val id: Long

    data class Todo(val todoId: Long, private var contents: String, private var done: Boolean = false): TodoItem() {
        override val id: Long
            get()= todoId

        val todoText: String
            get() = contents

        val isDone: Boolean
            get() = done
    }

    object DoneHeader: TodoItem() {
        override val id = Long.MIN_VALUE
    }
}
