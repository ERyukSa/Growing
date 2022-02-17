package com.eryuksa.growing.miracle_morning.calendar

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.eryuksa.growing.R
import com.eryuksa.growing.config.GrowingApplication
import com.eryuksa.growing.miracle_morning.calendar.model.MonthType
import java.util.*

private val goalMinutes get() = GrowingApplication.goalMinutes
private val startDate get() = GrowingApplication.startDate

private val todayCalendar = Calendar.getInstance()
private val todayYear = todayCalendar.get(Calendar.YEAR)
private val todayMonth = todayCalendar.get(Calendar.MONTH) + 1
private val todayDate = todayCalendar.get(Calendar.DATE)

@BindingAdapter(value = ["stamp", "currentDate", "monthType"], requireAll = true)
fun setStamp(
    imageView: ImageView,
    wakeUpMinutes: Int?,
    currentDate: GrowingApplication.StartDate,
    monthType: MonthType
) {
    // 설정이 아직 안돼있을 때
    if (goalMinutes == null || startDate == null) return

    // ViewHolder의 month
    val currentMonth = when (monthType) {
        MonthType.PREV -> currentDate.month - 1
        MonthType.CURRENT -> currentDate.month
        MonthType.NEXT -> currentDate.month + 1
    }

    // 시작 날짜 이전이거나 오늘 이후 날짜는 빈 Stamp로 설정
    startDate!!.let {
        if (currentDate.year < it.year || currentDate.year > todayYear) return
        else if (currentMonth < it.month || currentMonth > todayMonth) return
        else if (currentDate.date < it.date || currentDate.date > todayDate) return

    }

    imageView.visibility = View.VISIBLE
    when {
        // 등록 x
        wakeUpMinutes == null -> {
            imageView.setImageResource(R.drawable.round_question_mark_20)
            imageView.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(imageView.context, R.color.red_sunday))
        }

        // 성공
        wakeUpMinutes <= goalMinutes!! -> {
            imageView.setImageResource(R.drawable.ic_round_star_24)
            imageView.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(imageView.context, R.color.green_stamp))
        }

        // 성공x
        else -> {
            imageView.setImageResource(R.drawable.round_done_20)
            imageView.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(imageView.context, R.color.stamp_normal))
        }
    }
}