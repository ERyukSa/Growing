package com.eryuksa.growing.fragment.miracle_morning.calendar

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.eryuksa.growing.R
import com.eryuksa.growing.fragment.miracle_morning.calendar.model.DayType
import com.eryuksa.growing.fragment.miracle_morning.calendar.model.MiracleDate
import com.eryuksa.growing.fragment.miracle_morning.calendar.model.MonthType

class DateViewModel(private val context: Context) : BaseObservable() {

    var miracleDate: MiracleDate? = null
        set(miracleDate) {
            field = miracleDate
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
                ContextCompat.getColor(context, R.color.white)
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
}
