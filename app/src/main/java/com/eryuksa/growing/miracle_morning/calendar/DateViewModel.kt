package com.eryuksa.growing.miracle_morning.calendar

import androidx.annotation.ColorRes
import androidx.lifecycle.MutableLiveData
import com.eryuksa.growing.R
import com.eryuksa.growing.miracle_morning.model.MiracleDate
import org.joda.time.DateTime

class DateViewModel(private val miracleDate: MiracleDate, private val displayMonthDate: DateTime) {

    val dateText: String
        get() = miracleDate.dateTime.dayOfMonth.toString()

    // 바인딩어댑터에서 사용
    val dateColor: Int
        @ColorRes get() {
            val currentMonthDate = miracleDate.dateTime.withDayOfMonth(1)

            // 보여주는 달의 날짜일 때
            return if (currentMonthDate.isEqual(displayMonthDate)) {
                when (miracleDate.dateTime.dayOfWeek) {
                    7 -> R.color.red_sunday
                    6 -> R.color.blue_saturday
                    else -> R.color.text
                }
            } else { // 이전 or 다음 달의 날짜일 때
                when (miracleDate.dateTime.dayOfWeek) {
                    7 -> R.color.red_sunday_dim
                    6 -> R.color.blue_saturday_dim
                    else -> R.color.gray_date
                }
            }
        }

    val wakeUpMinutes: MutableLiveData<Int?>
        get() = miracleDate.wakeUpMinutes

    // 사용자가 설정한 시작 날짜 비교할 현재 날짜 -> 바인딩어댑터에서 사용
    val currentDateTime: DateTime
        get() = miracleDate.dateTime
}
