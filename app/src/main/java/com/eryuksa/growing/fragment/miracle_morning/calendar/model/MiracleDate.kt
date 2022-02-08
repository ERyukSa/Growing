package com.eryuksa.growing.fragment.miracle_morning.calendar.model

import androidx.lifecycle.MutableLiveData

data class MiracleDate(
    var dayOfMonth: Int = 1,
    var monthType: MonthType = MonthType.CURRENT,
    var dayType: DayType = DayType.WEEKDAY,
    val wakeUpMinutes: MutableLiveData<Int?> = MutableLiveData(null)
)
