package com.eryuksa.growing

import android.app.Application
import com.eryuksa.growing.fragment.miracle_morning.database.CalendarRepository

class GrowingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CalendarRepository.initialize(this)
    }
}