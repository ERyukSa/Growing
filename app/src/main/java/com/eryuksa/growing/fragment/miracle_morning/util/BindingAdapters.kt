package com.eryuksa.growing.fragment.miracle_morning.util

import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.eryuksa.growing.R
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("android:text")
fun setText(textView: TextView, millis: Long?) {
    if (millis != null) {
        val formatter =
            SimpleDateFormat(textView.context.getString(R.string.month_format), Locale.KOREA)

        textView.text = formatter.format(Date(millis))
    }
}

@BindingAdapter("app:title")
fun setTitle(toolbar: Toolbar, millis: Long?) {
    if (millis != null) {
        val formatter =
            SimpleDateFormat(toolbar.context.getString(R.string.month_format), Locale.KOREA)

        toolbar.title = formatter.format(Date(millis))
    }
}