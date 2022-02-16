package com.eryuksa.growing.miracle_morning.calendar

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.eryuksa.growing.R
import com.eryuksa.growing.config.GrowingApplication
import com.eryuksa.growing.miracle_morning.calendar.model.DayType
import com.eryuksa.growing.miracle_morning.calendar.model.MiracleDate
import com.eryuksa.growing.miracle_morning.calendar.model.MonthType
import java.util.*

class DateViewModel(private val context: Context, val calendar: Calendar) : BaseObservable() {

    var miracleDate: MiracleDate? = null
        set(miracleDate) {
            field = miracleDate
            currentDate.date = miracleDate?.dayOfMonth ?: 1
            notifyChange()
        }

    @get:Bindable
    val dateForText: String
        get() = miracleDate?.dayOfMonth.toString()

    @get:Bindable
    val dateColor: Int
        get() {
            val monthType = miracleDate?.monthType ?: MonthType.CURRENT
            val dayType = miracleDate?.dayType ?: DayType.WEEKDAY

            return if (monthType == MonthType.CURRENT && dayType == DayType.WEEKDAY) {
                ContextCompat.getColor(context, R.color.text)
            } else if (monthType == MonthType.CURRENT && dayType == DayType.SATURDAY) {
                ContextCompat.getColor(context, R.color.blue_saturday)
            } else if (monthType == MonthType.CURRENT && dayType == DayType.SUNDAY) {
                ContextCompat.getColor(context, R.color.red_sunday)
            } else if (monthType != MonthType.CURRENT && dayType == DayType.SATURDAY) {
                ContextCompat.getColor(context, R.color.blue_saturday_dim)
            } else if (monthType != MonthType.CURRENT && dayType == DayType.SUNDAY) {
                ContextCompat.getColor(context, R.color.red_sunday_dim)
            } else {
                ContextCompat.getColor(context, R.color.gray_date)
            }

        }

    val wakeUpStamp: MutableLiveData<Int?>?
        get() = miracleDate?.wakeUpMinutes

    // 사용자가 설정한 시작 날짜와 비교할 객체 -> 시작 날짜 이전에는 스탬프를 보여주지 않기 위함
    val currentDate: GrowingApplication.StartDate = GrowingApplication.StartDate(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        miracleDate?.dayOfMonth ?: 1
    )
}
