package com.eryuksa.growing.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {

    private var todoHead: TodoItem.Todo? = null
    private var todoTail: TodoItem.Todo? = null
    private var doneHead: TodoItem.Todo? = null
    private var doneTail: TodoItem.Todo? = null

    private var justRemovedTodo: TodoItem.Todo? = null // 직전에 삭제한 Todo

    private var _listForUi = MutableLiveData<List<TodoItem>>()
    val listForUi: LiveData<List<TodoItem>>
        get() = _listForUi

    private var _numOfTodo = 0
    val numOfTodo = MutableLiveData<String>()
    private var _numOfDone = 0
    val numOfDone = MutableLiveData<String>()

    private var i = 0L

    /**
     * 할 일 목록에 추가
     */
    private fun addTodo(todo: TodoItem.Todo) {
        _numOfTodo += 1
        numOfTodo.value = _numOfTodo.toString()

        todo.next = null
        if (todoHead == null) {
            todoHead = todo
            todoTail = todo
            todo.prev = null
        } else {
            todoTail!!.next = todo
            todo.prev = todoTail
            todoTail = todo
        }
        updateListForUi()
    }

    /**
     * 완료 목록에 추가
     */
    private fun addDone(todo: TodoItem.Todo) {
        _numOfDone += 1
        numOfDone.value = _numOfDone.toString()

        todo.next = null
        if (doneHead == null) {
            doneHead = todo
            doneTail = todo
            todo.prev = null
        } else {
            doneTail!!.next = todo
            todo.prev = doneTail
            doneTail = todo
        }
    }

    /**
     * 할 일 완료
     */
    private fun completeTodo(todo: TodoItem.Todo) {
        todo.done.value = true

        removeTodo(todo) // 할 일 목록에서 제거
        addDone(todo)    // 완료 목록에 추가
        updateListForUi()
    }

    fun remove(todo: TodoItem.Todo) {
        justRemovedTodo = todo

        if (todo.done.value == true) {
            removeDone(todo)
        } else {
            removeTodo(todo)
        }

        updateListForUi()
    }

    /**
     * 할 일 목록에서 삭제
     */
    private fun removeTodo(todo: TodoItem.Todo) {
        _numOfTodo -= 1
        numOfTodo.value = _numOfTodo.toString()

        // 할 일 목록에서 제거
        when {
            todoHead == todoTail -> {
                todoHead = null
                todoTail = null
            }
            todo == todoHead -> {
                todoHead = todo.next
                todoHead!!.prev = null
            }
            todo == todoTail -> {
                todoTail = todo.prev
                todoTail!!.next = null
            }
            else -> {
                todo.prev!!.next = todo.next
                todo.next!!.prev = todo.prev
            }
        }
    }

    /**
     * 완료 목록에서 삭제
     */
    private fun removeDone(todo: TodoItem.Todo) {
        _numOfDone -= 1
        numOfDone.value = _numOfDone.toString()

        // 완료 목록 변경
        if (doneHead == doneTail) {
            doneHead = null
            doneTail = null
        } else if (todo == doneHead){
            doneHead = todo.next
            doneHead!!.prev = null
        } else if (todo == doneTail) {
            doneTail = todo.prev
            doneTail!!.next = null
        } else {
            todo.prev!!.next = todo.next
            todo.next!!.prev = todo.prev
        }
    }

    /**
     * 할 일 목록으로 되돌리기
     */
    private fun rollBackToTodo(todo: TodoItem.Todo) {
        todo.done.value = false

        removeDone(todo) // 완료 목록에서 제거
        addTodo(todo)    // 할 일 목록에 추가
        updateListForUi()
    }

    /**
     * Fragment에 제공할 리스트로 변경
     */
    private fun updateListForUi() {
        viewModelScope.launch(Dispatchers.Default) {
            val updatedList = mutableListOf<TodoItem>()
            var currentTodo = todoHead

            while(currentTodo != null) {    // 할 일 목록
                updatedList.add(currentTodo)
                currentTodo = currentTodo.next
            }

            if (doneHead != null) {         // 완료 목록
                updatedList.add(TodoItem.DoneHeader)
                currentTodo = doneHead
            }
            while (currentTodo != null) {
                updatedList.add(currentTodo)
                currentTodo = currentTodo.next
            }

            _listForUi.postValue(updatedList) // 백그라운드 스레드이므로 postValue
        }
    }

    /**
     * 할 일 완료 or 복귀 동작
     */
    fun onTodoStatusChanged(todo: TodoItem.Todo, done: Boolean) {
        if (done) {
            completeTodo(todo)
        } else {
            rollBackToTodo(todo)
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