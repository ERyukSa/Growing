package com.eryuksa.growing.todo.add

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddTodoViewModel : ViewModel() {

    private var todoTextHasContent = MutableLiveData(false)
    val btnConfirmEnabled: LiveData<Boolean>
        get() = todoTextHasContent

    val todoTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?){

            if (s.isNullOrBlank()) {
                if (todoTextHasContent.value == true){
                    todoTextHasContent.value = false
                }
            } else {
                if (todoTextHasContent.value == false) {
                    todoTextHasContent.value = true
                }
            }
        }
    }
}