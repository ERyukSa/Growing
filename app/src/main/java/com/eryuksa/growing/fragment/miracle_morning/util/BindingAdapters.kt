package com.eryuksa.growing.fragment.miracle_morning.util

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.eryuksa.growing.R
import com.eryuksa.growing.config.GrowingApplication
import com.eryuksa.growing.fragment.miracle_morning.calendar.model.MonthType
import java.text.SimpleDateFormat
import java.util.*

private val goalMinutes get() = GrowingApplication.goalMinutes
private val startDate get() = GrowingApplication.startDate

private val todayCalendar = Calendar.getInstance()
private val todayYear = todayCalendar.get(Calendar.YEAR)
private val todayMonth = todayCalendar.get(Calendar.MONTH) + 1
private val todayDate = todayCalendar.get(Calendar.DATE)

@BindingAdapter("app:title")
fun setTitle(toolbar: Toolbar, millis: Long?) {
    if (millis != null) {
        val formatter =
            SimpleDateFormat(toolbar.context.getString(R.string.month_format), Locale.KOREA)

        toolbar.title = formatter.format(Date(millis))
    }
}

@BindingAdapter(value = ["stamp", "currentDate", "monthType"], requireAll = true)
fun setStamp(
    imageView: ImageView,
    wakeUpMinutes: Int?,
    date: GrowingApplication.StartDate,
    monthType: MonthType
) {
    // 설정이 아직 안돼있을 때
    if (goalMinutes == null || startDate == null) return

    // ViewHolder의 month
    val currentMonth = when (monthType) {
        MonthType.PREV -> date.month - 1
        MonthType.CURRENT -> date.month
        MonthType.NEXT -> date.month + 1
    }

    // 시작 날짜 이전이거나 오늘 이후 날짜는 빈 Stamp로 설정
    startDate!!.let {
        if (date.year < it.year || date.year > todayYear) {
            imageView.setImageResource(android.R.color.transparent)
            return
        } else if (currentMonth < it.month || currentMonth > todayMonth) {
            imageView.setImageResource(android.R.color.transparent)
            return
        } else if (date.date < it.date || date.date > todayDate) {
            imageView.setImageResource(android.R.color.transparent)
            return
        }
    }

    when {
        // 등록 x
        wakeUpMinutes == null -> {
            imageView.setImageResource(R.drawable.round_question_mark_20)
            imageView.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(imageView.context, R.color.red_sunday))
        }

        // 성공
        wakeUpMinutes <= goalMinutes!! -> {
            imageView.setImageResource(R.drawable.round_verified_20)
            imageView.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(imageView.context, R.color.green_stamp))
        }

        // 성공x
        else -> {
            imageView.setImageResource(R.drawable.round_close_20)
            imageView.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(imageView.context, R.color.white))
        }
    }
}