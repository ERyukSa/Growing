package com.eryuksa.growing

import android.app.Application
import com.eryuksa.growing.fragment.miracle_morning.calendar.database.CalendarRepository

class GrowingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CalendarRepository.initialize(this)
    }
}