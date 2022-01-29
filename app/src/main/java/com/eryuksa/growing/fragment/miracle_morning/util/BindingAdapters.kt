package com.eryuksa.growing.fragment.miracle_morning.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.eryuksa.growing.R
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("android:text")
fun setText(textView: TextView, calendar: Calendar?) {
    if (calendar != null) {
        val formatter =
            SimpleDateFormat(textView.context.getString(R.string.month_format), Locale.KOREA)

        textView.text = formatter.format(calendar.time)
    }
}