package com.eryuksa.growing.miracle_morning.calendar

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.eryuksa.growing.R
import com.eryuksa.growing.config.GrowingApplication
import org.joda.time.DateTime

private val goalMinutes get() = GrowingApplication.goalMinutes
private val startDate get() = GrowingApplication.startDateInMillis

@BindingAdapter("android:textColor")
fun setDateTextColor(textView: TextView, @ColorRes id: Int) {
    textView.setTextColor(ContextCompat.getColor(textView.context, id))
}

@BindingAdapter(value = ["wakeUpMinutes", "currentDateTime"], requireAll = true)
fun setStamp(
    imageView: ImageView,
    wakeUpMinutes: Int?,
    currentDateTime: DateTime
) {
    // 설정이 아직 안돼있을 때
    if (goalMinutes == null || startDate == null) return

    // 시작 날짜 이전이거나 오늘 이후 날짜는 빈 Stamp로 설정
    if (currentDateTime.isBefore(startDate!!) || currentDateTime.isAfterNow) {
        imageView.visibility = View.GONE
        return
    }

    imageView.visibility = View.VISIBLE
    imageView.setStampImage(wakeUpMinutes)
}

private fun ImageView.setStampImage(wakeUpMinutes: Int?) {
    when {
        // 등록 x
        wakeUpMinutes == null -> {
            setImageResource(R.drawable.round_question_mark_20)
            imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red_sunday))
        }

        // 미라클모닝 성공
        wakeUpMinutes <= goalMinutes!! -> {
            setImageResource(R.drawable.ic_round_star_24)
            imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_stamp))
        }

        // 성공x but 등록
        else -> {
            setImageResource(R.drawable.round_done_20)
            imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.stamp_normal))
        }
    }
}