package com.eryuksa.growing.todo

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TodoViewModel : ViewModel() {

    // LinkedHashSet -> 빠르게 제거할 수 있다
    private val todoList = LinkedHashSet<TodoItem.Todo>() //mutableListOf<TodoItem.Todo>()
    private val doneList = LinkedHashSet<TodoItem.Todo>() //mutableListOf<TodoItem.Todo>()

    private var _listForUi = MutableLiveData<List<TodoItem>>()
    val listForUi: LiveData<List<TodoItem>>
        get() = _listForUi

    val numOfTodo: LiveData<String> = Transformations.map(_listForUi){ todoList.size.toString() }
    val numOfDone: LiveData<String> = Transformations.map(_listForUi){ doneList.size.toString() }

    private var i = 0L

    private fun addTodo(todo: TodoItem.Todo) {
        todoList.add(todo)
        updateListForUi()
    }

    private fun completeTodo(todo: TodoItem.Todo) {
        todo.done.value = true
        todoList.remove(todo)
        doneList.add(todo)
        updateListForUi()
    }

    fun remove(todo: TodoItem.Todo) {
        if (todo.done.value == true) {
            doneList.remove(todo)
        } else {
            todoList.remove(todo)
        }
        updateListForUi()
    }

    fun move() {

    }

    private fun updateListForUi() {
        viewModelScope.launch(Dispatchers.Default) {
            val updatedList: List<TodoItem> = when {
                todoList.isEmpty() && doneList.isEmpty() -> emptyList()
                todoList.isEmpty() -> listOf(TodoItem.DoneHeader) + doneList
                doneList.isEmpty() -> emptyList<TodoItem>() + todoList
                else -> todoList.toList() + listOf(TodoItem.DoneHeader) + doneList
            }

            withContext(Dispatchers.Main){
                _listForUi.value = updatedList
            }
        }
    }

    /**
     * 할 일 완료 or 복귀 동작
     * position: todoList + 완료 헤더 + doneList 에서의 위치
     */
    fun onTodoStatusChanged(todo: TodoItem.Todo, done: Boolean) {
        if (done) {
            completeTodo(todo)
        } else {
            doneList.remove(todo)
            todo.done.value = false
            addTodo(todo)
        }
    }

    /** ---------------------------------------------------------------------------
     * used in AddTodoDialog
     */
    fun onClickConfirmAdd(todoText: String) {
        val todo = TodoItem.Todo(i++, todoText)
        addTodo(todo)
    }
}