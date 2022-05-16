package com.eryuksa.growing.todo

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {

    private var todoHead: TodoItem.Todo? = null
    private var todoTail: TodoItem.Todo? = null
    private var doneHead: TodoItem.Todo? = null
    private var doneTail: TodoItem.Todo? = null
    private var justRemovedTodo: TodoItem.Todo? = null

    private var _tempListForUi = MutableLiveData<List<TodoItem>>()
    val tempListForUi: LiveData<List<TodoItem>>
        get() = _tempListForUi

    // LinkedHashSet -> 빠르게 제거할 수 있다
    private val todoList = LinkedHashSet<TodoItem.Todo>() //mutableListOf<TodoItem.Todo>()
    private val doneList = LinkedHashSet<TodoItem.Todo>() //mutableListOf<TodoItem.Todo>()

    private var _listForUi = MutableLiveData<List<TodoItem>>()
    val listForUi: LiveData<List<TodoItem>>
        get() = _listForUi

    val numOfTodo: LiveData<String> = Transformations.map(_listForUi){ todoList.size.toString() }
    val numOfDone: LiveData<String> = Transformations.map(_listForUi){ doneList.size.toString() }

    private var _tempNumOfTodo = 0
    val tempNumOfTodo = MutableLiveData<String>()
    private var _tempNumOfDone = 0
    val tempNumOfDone = MutableLiveData<String>()

    private var i = 0L

    private fun tempAddTodo(todo: TodoItem.Todo) {
        _tempNumOfTodo += 1
        tempNumOfTodo.value = _tempNumOfTodo.toString()

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

    private fun tempCompleteTodo(todo: TodoItem.Todo) {
        todo.done.value = true

        tempRemoveTodo(todo) // 할 일 목록에서 제거
        tempAddDone(todo)    // 완료 목록에 추가
        updateListForUi()
    }

    private fun tempAddDone(todo: TodoItem.Todo) {
        _tempNumOfDone += 1
        tempNumOfDone.value = _tempNumOfDone.toString()

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

    private fun tempRemove(todo: TodoItem.Todo) {
        justRemovedTodo = todo

        if (todo.done.value == true) {
            tempRemoveDone(todo)
        } else {
            tempRemoveTodo(todo)
        }

        updateListForUi()
    }

    private fun tempRemoveDone(todo: TodoItem.Todo) {
        _tempNumOfDone -= 1
        tempNumOfDone.value = _tempNumOfDone.toString()

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

    private fun tempRemoveTodo(todo: TodoItem.Todo) {
        _tempNumOfTodo -= 1
        tempNumOfTodo.value = _tempNumOfTodo.toString()

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

    private fun tempRollBackToTodo(todo: TodoItem.Todo) {
        todo.done.value = false

        tempRemoveDone(todo) // 완료 목록에서 제거
        tempAddTodo(todo)    // 할 일 목록에 추가
        updateListForUi()
    }

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

            _tempListForUi.postValue(updatedList) // 백그라운드 스레드이므로 postValue
        }
    }

    private fun addTodo(todo: TodoItem.Todo) {
        todoList.add(todo)
        updateListForUi()
    }

    /**
     * 할 일 -> 완료
     */
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

    /**
     * 완료 -> 할 일
     */
    private fun rollBackToTodo(todo: TodoItem.Todo) {
        todo.done.value = false
        doneList.remove(todo)
        addTodo(todo)
    }

    /**
     * todoList + doneList, 그 사이에 완료 header(TodoItem.DoneHeader)를 넣은 형태로 변환한다
     */
    /*private fun updateListForUi() {
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
    }*/

    /**
     * 할 일 완료 or 복귀 동작
     */
    fun onTodoStatusChanged(todo: TodoItem.Todo, done: Boolean) {
        if (done) {
            //completeTodo(todo)
            tempCompleteTodo(todo)
        } else {
            //rollBackToTodo(todo)
            tempRollBackToTodo(todo)
        }
    }

    /** ---------------------------------------------------------------------------
     * used in AddTodoDialog
     */
    fun onClickConfirmAdd(todoText: String) {
        val todo = TodoItem.Todo(i++, todoText)
        tempAddTodo(todo)
        //addTodo(todo)
    }
}