package com.eryuksa.growing.config

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.eryuksa.growing.miracle_morning.calendar.data.CalendarRepository
import com.eryuksa.growing.motivation.data.YoutubeRepository

class GrowingApplication : Application() {

    companion object {

        const val START_DATE = "dateMillis"
        const val ARG_GOAL_MINUTES = "goal"

        lateinit var sSharedPreferences: SharedPreferences

        var startDateInMillis: Long? = null // 미라클 모닝 시작 날짜
        var goalMinutes: Int? = null     // 목표 기상 시간
    }

    override fun onCreate() {
        super.onCreate()
        CalendarRepository.initialize(this)
        YoutubeRepository.initialize()

        sSharedPreferences = getDefaultSharedPreferences(this)
        loadSettingsValue()
    }

    override fun onTerminate() {
        YoutubeRepository.get().onDestroy()
        super.onTerminate()
    }

    /**
     * 시작 날짜, 목표 기상 시간을 불러온다
     */
    private fun loadSettingsValue() {
        val startDateMillis: Long = sSharedPreferences.getLong(START_DATE, -1L)
        if (startDateMillis != -1L) {
            startDateInMillis = startDateMillis
        }

        val tempMinutes = sSharedPreferences.getInt(ARG_GOAL_MINUTES, -1)
        if (tempMinutes != -1) {
            goalMinutes = tempMinutes
        }
    }
}