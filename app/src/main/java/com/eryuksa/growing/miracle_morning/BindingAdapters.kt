package com.eryuksa.growing.miracle_morning

import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.eryuksa.growing.R
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("app:title")
fun setTitle(toolbar: Toolbar, millis: Long?) {
    if (millis != null) {
        val formatter =
            SimpleDateFormat(toolbar.context.getString(R.string.month_format), Locale.KOREA)

        toolbar.title = formatter.format(Date(millis))
    }
}