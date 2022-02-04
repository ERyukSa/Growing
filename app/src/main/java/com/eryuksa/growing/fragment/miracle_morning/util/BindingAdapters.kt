package com.eryuksa.growing.fragment.miracle_morning.util

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.eryuksa.growing.R
import java.text.SimpleDateFormat
import java.util.*

private const val GOAL_MINUTES = 390

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

@BindingAdapter("app:stamp")
fun setStamp(imageView: ImageView, minutesOfDay: Int?) {
    when {
        minutesOfDay == null -> {
            imageView.setImageResource(R.drawable.round_close_20)
            imageView.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(imageView.context, R.color.red_sunday))
        }
        minutesOfDay <= GOAL_MINUTES -> {
            imageView.setImageResource(R.drawable.round_verified_20)
            imageView.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(imageView.context, R.color.green_stamp))
        }
        else -> {
            imageView.setImageResource(R.drawable.round_done_20)
            imageView.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(imageView.context, R.color.white))
        }
    }
}