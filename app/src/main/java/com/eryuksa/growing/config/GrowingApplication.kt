package com.eryuksa.growing.config

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.eryuksa.growing.fragment.miracle_morning.database.CalendarRepository

class GrowingApplication : Application() {

    data class StartDate(var year: Int, var month: Int, var date: Int)

    companion object {

        const val START_YEAR = "year"
        const val START_MONTH = "month"
        const val START_DATE = "date"
        const val ARG_GOAL_MINUTES = "goal"

        lateinit var sSharedPreferences: SharedPreferences

        var startDate: StartDate? = null // 미라클 모닝 시작 날짜
        var goalMinutes: Int? = null     // 목표 기상 시간
    }

    override fun onCreate() {
        super.onCreate()
        CalendarRepository.initialize(this)

        sSharedPreferences = getDefaultSharedPreferences(this)
        loadSettingsValue()
    }

    /**
     * 시작 날짜, 목표 기상 시간을 불러온다
     */
    private fun loadSettingsValue() {
        val startYear = sSharedPreferences.getInt(START_YEAR, -1)
        if (startYear != -1) {
            val startMonth = sSharedPreferences.getInt(START_MONTH, -1)
            val startDayOfMonth = sSharedPreferences.getInt(START_DATE, -1)

            startDate = StartDate(startYear, startMonth, startDayOfMonth)
        }

        val tempMinutes = sSharedPreferences.getInt(ARG_GOAL_MINUTES, -1)
        if (tempMinutes != -1) {
            goalMinutes = tempMinutes
        }
    }
}