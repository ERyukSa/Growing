package com.eryuksa.growing.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {

    private val todoList = mutableListOf<TodoItem.Todo>()
    private val doneList = mutableListOf<TodoItem.Todo>()

    private var _listForAdapter = MutableLiveData<List<TodoItem>>()
    val listForAdapter: LiveData<List<TodoItem>>
        get() = _listForAdapter

    fun addTodo() {

    }

    fun completeTodo(position: Int) {

    }

    fun remove(position: Int) {

    }

    fun move() {

    }

    private fun changeListForAdapter() {
        viewModelScope.launch(Dispatchers.Default) {
            _listForAdapter.value = when {
                todoList.isEmpty() && doneList.isEmpty() -> emptyList()
                todoList.isEmpty() -> listOf(TodoItem.DoneHeader) + doneList
                doneList.isEmpty() -> emptyList<TodoItem>() + todoList
                else -> todoList + listOf(TodoItem.DoneHeader) + doneList
            }
        }
    }
}