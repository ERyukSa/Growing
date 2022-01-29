package com.eryuksa.growing.fragment.miracle_morning.data

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.eryuksa.growing.R

class DateViewModel(private val context: Context) : BaseObservable() {

    var dateNumber: Int = 0
        set(dateNumber) {
            field = dateNumber
            notifyChange()
        }

    var isInThisMonth = true
    var dayType = DayType.WEEKDAY

    @get:Bindable
    val dateForText: String
        get() = dateNumber.toString()

    @get:Bindable
    val dateColor: Int
        get() {
            return if (isInThisMonth && dayType == DayType.WEEKDAY) {
                ContextCompat.getColor(context, R.color.white)
            } else if (isInThisMonth && dayType == DayType.SATURDAY) {
                ContextCompat.getColor(context, R.color.blue_saturday)
            } else if (isInThisMonth && dayType == DayType.SUNDAY) {
                ContextCompat.getColor(context, R.color.red_sunday)
            } else if (!isInThisMonth && dayType == DayType.SATURDAY) {
                ContextCompat.getColor(context, R.color.blue_saturday_dim)
            } else if (!isInThisMonth && dayType == DayType.SUNDAY) {
                ContextCompat.getColor(context, R.color.red_sunday_dim)
            } else {
                ContextCompat.getColor(context, R.color.gray_date)
            }
        }
}
