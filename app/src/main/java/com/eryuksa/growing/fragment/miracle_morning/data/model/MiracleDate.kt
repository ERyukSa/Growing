package com.eryuksa.growing.fragment.miracle_morning.data.model

import androidx.lifecycle.MutableLiveData

data class MiracleDate(
    val dateNumber: Int,
    val monthType: MonthType,
    val dayType: DayType,
    val wakeUpMinutes: MutableLiveData<Int?> = MutableLiveData(null)
)
