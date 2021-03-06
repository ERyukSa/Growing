package com.eryuksa.growing.miracle_morning.model

import androidx.room.Entity

@Entity(primaryKeys = ["monthMillis", "dayOfMonth"])
data class MiracleStamp(
    val monthMillis: Long,
    val dayOfMonth: Int,
    val wakeUpMinutes: Int
)
