package com.eryuksa.growing.miracle_morning.model

import androidx.lifecycle.MutableLiveData
import org.joda.time.DateTime

data class MiracleDate(
    val dateTime: DateTime,
    val wakeUpMinutes: MutableLiveData<Int?> = MutableLiveData(null)
)