package com.eryuksa.growing.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.eryuksa.growing.util.Event
import java.util.*

class TodoViewModel : ViewModel() {

    private val todoList = mutableListOf<TodoItem>()
    private val doneHeader = TodoItem.DoneHeader
    private var headerIdx = 0 // 헤더 자리 -> 헤더 위에 할 일 추가할 때 사용

    private var _listForUi = MutableLiveData<List<TodoItem>>()
    val listForUi: LiveData<List<TodoItem>>
        get() = _listForUi

    private val _numOfTodo = MutableLiveData(0)
    val numOfTodo: LiveData<String> = Transformations.map(_numOfTodo) { it.toString() }
    private var _numOfDone = MutableLiveData(0)
    val numOfDone: LiveData<String> = Transformations.map(_numOfDone) { it.toString() }

    // 직전에 삭제한 할 일 -> 되돌릴 때 사용
    private var removedTodo: TodoItem.Todo? = null
    private var removedIdx = 0

    // 삭제됐음을 알리는 스낵바를 띄워라!
    private val _showRemovedSnackbar = MutableLiveData<Event<Unit>>()
    val showRemovedSnackbar: LiveData<Event<Unit>>
        get() = _showRemovedSnackbar

    // 항목 위치 변경 중
    var isSwapping = false

    private var i = 0L // 객체 아이디

    /**
     * 할 일 추가
     */
    fun add(todo: TodoItem.Todo) {
        moveToTodo(todo)
        updateListForUi()
    }

    /**
     * 할 일 영역에 넣기
     */
    private fun moveToTodo(todo: TodoItem.Todo) {
        _numOfTodo.value = _numOfTodo.value?.plus(1)
        todoList.add(headerIdx++, todo)
    }

    /**
     * 완료 영역에 넣기
     */
    private fun moveToDone(todo: TodoItem.Todo) {
        _numOfDone.value = _numOfDone.value?.plus(1)?.also {
            if (it == 1) todoList.add(doneHeader)
            todoList.add(todo)
        }
    }

    /**
     * 할 일 -> 완료
     */
    private fun completeTodo(idx: Int) {
        val todo = todoList[idx] as TodoItem.Todo
        removeFromTodo(idx)
        moveToDone(todo)
        updateListForUi()
    }

    /**
     * 화면에서 아예 삭제
     */
    fun remove(idx: Int) {
        // 되돌릴 때 사용하기 위해 방금 삭제한 객체 저장
        removedTodo = (todoList[idx] as TodoItem.Todo)
        removedIdx = idx

        // 해당 영역에서 삭제
        if (removedTodo!!.done.value == true) {
            removeFromDone(idx)
        } else {
            removeFromTodo(idx)
        }

        _showRemovedSnackbar.value = Event(Unit) // 스낵바 요청
        updateListForUi()
    }

    /**
     * 할 일 영역에서 삭제
     */
    private fun removeFromTodo(idx: Int) {
        _numOfTodo.value = _numOfTodo.value?.minus(1)
        todoList.removeAt(idx)
        headerIdx-- // 헤더 위치를 위로 당긴다
    }

    /**
     * 완료 영역에서 삭제
     */
    private fun removeFromDone(idx: Int) {
        _numOfDone.value = _numOfDone.value?.minus(1)?.also {
            todoList.removeAt(idx)
            if (it == 0) todoList.removeAt(idx - 1) // 완료한 일이 없으면 헤더도 함께 삭제
        }
    }

    /**
     * 완료 -> 할 일
     */
    private fun rollBackToTodo(idx: Int) {
        val todo = todoList[idx] as TodoItem.Todo
        removeFromDone(idx)
        moveToTodo(todo)
        updateListForUi()
    }

    /**
     * 삭제 -> 되돌리기
     */
    fun rollBackFromRemoved() {

        removedTodo?.let { todo ->
            // 완료 항목이 0개인 상태에서 되돌리기 -> 헤더도 함께 추가
            if (_numOfDone.value == 0) todoList.add(doneHeader)
            todoList.add(removedIdx, todo)

            if (todo.done.value == true) {
                _numOfDone.value = _numOfDone.value!! + 1
            } else {
                _numOfTodo.value = _numOfTodo.value!! + 1
            }
            updateListForUi()
            removedTodo = null
        }
    }

    /**
     * 프래그먼트에게 제공할 리스트
     */
    private fun updateListForUi() {
        _listForUi.value = todoList.toList()
    }

    /**
     * 드래그&드롭 -> 위치 변경
     */
    fun swapTodos(fromIdx: Int, toIdx: Int): Boolean {
        isSwapping = true

        val fromTodo = todoList[fromIdx] as TodoItem.Todo
        // 할 일 -> 완료 영역으로 넘어갈 때
        if (fromTodo.done.value == true && todoList[toIdx] is TodoItem.DoneHeader) {
            _numOfTodo.value = _numOfTodo.value!! + 1
            _numOfDone.value = _numOfDone.value!! - 1
            fromTodo.done.value = false
        } // 완료 -> 할 일 영역
        else if (fromTodo.done.value == false && todoList[toIdx] is TodoItem.DoneHeader) {
            _numOfTodo.value = _numOfTodo.value!! - 1
            _numOfDone.value = _numOfDone.value!! + 1
            fromTodo.done.value = true
        }

        Collections.swap(todoList, fromIdx, toIdx)
        updateListForUi()
        isSwapping = false
        return true
    }

    /**
     * 사용자가 체크박스를 클릭했을 때 동작(완료/복귀)
     */
    fun onCheckboxChecked(idx: Int, checked: Boolean) {
        // 드래그&드롭으로 위치를 바꾸는 중에 호출되면 리턴
        if (todoList[idx] is TodoItem.DoneHeader) return
        val todo = todoList[idx] as TodoItem.Todo
        if (todo.done.value == checked) return // onBindViewHolder() 과정에서 호출되면 리턴

        todo.done.value = checked
        if (checked) {
            completeTodo(idx)
        } else {
            rollBackToTodo(idx)
        }
    }

    /** ---------------------------------------------------------------------------
     * used in AddTodoDialog
     */
    fun onClickConfirmAdd(todoText: String) {
        val todo = TodoItem.Todo(i++, todoText)
        add(todo)
    }
}