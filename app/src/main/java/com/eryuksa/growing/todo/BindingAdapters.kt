package com.eryuksa.growing.todo

import android.graphics.Paint
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * 할 일 완료 -> 취소선 표시, 복귀 -> 취소선 해제
 */
@BindingAdapter("app:strikeThrough")
fun setStrikeThrough(textView: TextView, strikeThroughOn: Boolean) {

    if (strikeThroughOn) {
        textView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        textView.paintFlags = 0
    }
}