package com.eryuksa.growing.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.growing.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {

    // 연결 리스트
    private var todoHead: TodoItem.Todo? = null
    private var todoTail: TodoItem.Todo? = null
    private var doneHead: TodoItem.Todo? = null
    private var doneTail: TodoItem.Todo? = null

    private var justRemovedTodo: TodoItem.Todo? = null // 직전에 삭제한 Todo -> 되돌릴 때 사용

    private var _listForUi = MutableLiveData<List<TodoItem>>()
    val listForUi: LiveData<List<TodoItem>>
        get() = _listForUi

    private var _numOfTodo = 0
    val numOfTodo = MutableLiveData<String>()
    private var _numOfDone = 0
    val numOfDone = MutableLiveData<String>()

    private val _showRemovedSnackbar = MutableLiveData<Event<Unit>>() // 삭제됐음을 알리는 스낵바를 띄워라!
    val showRemovedSnackbar: LiveData<Event<Unit>>
        get() = _showRemovedSnackbar

    private var i = 0L

    /**
     * 할 일 목록의 꼭대기에 추가
     */
    private fun addTodoFirst(todo: TodoItem.Todo) {
        _numOfTodo += 1
        numOfTodo.value = _numOfTodo.toString()

        todo.next = null
        todo.prev = todoTail
        todoTail?.let { it.next = todo }
        todoTail = todo
        if (todoHead == null) todoHead = todo

        updateListForUi()
    }

    /**
     * 할 일 목록의 꼭대기에 추가
     */
    private fun addTodoLast(todo: TodoItem.Todo) {
        _numOfTodo += 1
        numOfTodo.value = _numOfTodo.toString()

        todo.prev = null
        todo.next = todoHead
        todoHead?.let { it.prev = todo }
        todoHead = todo
        if (todoTail == null) todoTail = todo

        updateListForUi()
    }

    /**
     * 완료 목록 끝에 추가
     */
    private fun addDoneLast(todo: TodoItem.Todo) {
        _numOfDone += 1
        numOfDone.value = _numOfDone.toString()

        todo.next = null
        todo.prev = doneTail
        doneTail?.let { it.next = todo }
        doneTail = todo
        if (doneHead == null) doneHead = todo
    }

    private fun addDoneFirst(todo: TodoItem.Todo) {
        _numOfDone += 1
        numOfDone.value = _numOfDone.toString()

        todo.prev = null
        todo.next = doneHead
        doneHead?.let{ it.prev = todo }
        doneHead = todo
        if (doneTail == null) doneTail = todo
    }

    /**
     * 할 일 완료
     */
    private fun completeTodo(todo: TodoItem.Todo) {
        todo.done.value = true

        removeTodo(todo)  // 할 일 목록에서 제거
        addDoneLast(todo) // 완료 목록에 추가
        updateListForUi()
    }

    /**
     * TodoItem 삭제
     */
    fun remove(todo: TodoItem.Todo) {
        justRemovedTodo = todo

        if (todo.done.value == true) {
            removeDone(todo)
        } else {
            removeTodo(todo)
        }

        updateListForUi()
        _showRemovedSnackbar.value = Event(Unit)
    }

    /**
     * 할 일 목록에서 삭제
     */
    private fun removeTodo(todo: TodoItem.Todo) {
        _numOfTodo -= 1
        numOfTodo.value = _numOfTodo.toString()

        // 할 일 목록에서 제거
        if(todoHead == todoTail) {
            todoHead = null
            todoTail = null
        } else if (todo == todoHead) {
            todoHead = todo.next
            todoHead!!.prev = null
        } else if (todo == todoTail) {
            todoTail = todo.prev
            todoTail!!.next = null
        } else {
            todo.prev!!.next = todo.next
            todo.next!!.prev = todo.prev
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
        addTodoFirst(todo)    // 할 일 목록에 추가
        updateListForUi()
    }

    /**
     * 삭제했던 항목 복구
     */
    fun rollBackFromRemoved() {
        val rolledBackTodo = justRemovedTodo
        justRemovedTodo = null

        rolledBackTodo?.let {
            // 목록 개수 변경
            if (it.done.value == true) {
                _numOfDone += 1
                numOfDone.value = _numOfDone.toString()
            } else {
                _numOfTodo += 1
                numOfTodo.value = _numOfTodo.toString()
            }

            val prevTodo = it.prev
            val nextTodo = it.next

            if (prevTodo == nextTodo) { // 영역에 Todo가 하나만 있었을 때
                if (it.done.value == true) {
                    doneHead = it
                    doneTail = it
                } else {
                    todoHead = it
                    todoTail = it
                }
            } else if (prevTodo == null) { // 2개 이상의 목록에서 맨 위에 있었을 떄
                it.next!!.prev = it
                if (it.done.value == true) doneHead = it
                else todoHead = it
            } else if (nextTodo == null) { // 2개 이상 목록에서 영역의 마지막에 있었을 때
                it.prev!!.next = it
                if (it.done.value == true) doneTail = it
                else todoTail = it
            } else {
                it.prev!!.next = it
                it.next!!.prev = it
            }
        }

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


    fun swapTodos(fromTodo: TodoItem.Todo, toTodo: TodoItem): Boolean {
        if (fromTodo.done.value == true && toTodo is TodoItem.DoneHeader) { // todo -> done
            fromTodo.done.value = false
            removeTodo(fromTodo)
            addDoneFirst(fromTodo)
            updateListForUi()
        } else if (fromTodo.done.value == false && toTodo is TodoItem.DoneHeader){ // done -> todo
            fromTodo.done.value = true
            removeDone(fromTodo)
            addTodoLast(fromTodo)
            updateListForUi()
        } else { // 같은 영역 안에서 스왑
            val toItem = toTodo as TodoItem.Todo
            fromTodo.next = toItem.next
            toItem.next?.let { it.prev = fromTodo }

            toItem.prev = fromTodo.prev
            fromTodo.prev?.let { it.next = toItem }
        }

        return true
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
        addTodoFirst(todo)
    }
}