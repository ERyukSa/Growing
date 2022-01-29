package com.eryuksa.growing.fragment.miracle_morning.data

import androidx.lifecycle.MutableLiveData
import java.util.*

interface RefreshCalendarListener {
    fun onRefresh(calendar: Calendar)
}

/**
 * 참조
 * Created by WoochanLee on 25/03/2019.
 * https://woochan-dev.tistory.com/27 [우찬쓰 개발블로그]
 */
class MiracleCalendar(private val refreshCalendarListener: RefreshCalendarListener) {

    companion object {
        const val DAYS_OF_WEEK = 7
        const val LOW_OF_CALENDAR = 6

        fun newInstance(refreshCalendarListener: RefreshCalendarListener): MiracleCalendar {
            return MiracleCalendar(refreshCalendarListener)
        }
    }

    val calendarLiveData = MutableLiveData(Calendar.getInstance())

    var prevMonthTailOffset = 0 // 첫 줄에 보여줄 저번달 날짜 개수
    var nextMonthHeadOffset = 0 // 막 줄에 보여줄 다음달 날짜 개수
    var currentMonthMaxDate = 0

    val dateList = arrayListOf<Int>() // 이번 달에 보여줄 날짜(일) 리스트


    /**
     * Init calendar.
     */
    init {
        calendarLiveData.value?.time = Date() // 오늘
        makeMonthDate()
    }

    /**
     * Change to prev month.
     */
    fun changeToPrevMonth(refreshCallback: (Calendar) -> Unit) {
        val calendar = calendarLiveData.value!!

        if(calendar.get(Calendar.MONTH) == 0){
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1)
            calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        }else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        }
        makeMonthDate()
    }

    /**
     * Change to next month.
     */
    fun changeToNextMonth(refreshCallback: (Calendar) -> Unit) {
        val calendar = calendarLiveData.value!!

        if(calendar.get(Calendar.MONTH) == Calendar.DECEMBER){
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1)
            calendar.set(Calendar.MONTH, 0)
        }else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1)
        }
        makeMonthDate()
    }

    /**
     * make month date.
     */
    private fun makeMonthDate() {
        val calendar = calendarLiveData.value!!

        dateList.clear()

        calendar.set(Calendar.DATE, 1)

        currentMonthMaxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        prevMonthTailOffset = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY

        makePrevMonthTail(calendar.clone() as Calendar) // 첫 줄에 들어갈 이전 달의 날짜 추가
        makeCurrentMonth(calendar) // 이번 달 날짜 추가

        nextMonthHeadOffset = LOW_OF_CALENDAR * DAYS_OF_WEEK - (prevMonthTailOffset + currentMonthMaxDate)
        makeNextMonthHead() // 마지막 줄의 다음달 날짜 추가

        refreshCalendarListener.onRefresh(calendar)
    }

    /**
     * Generate data for the last month displayed before the first day of the current calendar.
     */
    private fun makePrevMonthTail(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        val lastDate = calendar.getActualMaximum(Calendar.DATE)
        val firstDateOfTail = lastDate - prevMonthTailOffset + 1

        for (date in firstDateOfTail..lastDate) dateList.add(date)
    }

    /**
     * Generate data for the current calendar.
     */
    private fun makeCurrentMonth(calendar: Calendar) {
        for (i in 1..calendar.getActualMaximum(Calendar.DATE)) dateList.add(i)
    }

    /**
     * Generate data for the next month displayed before the last day of the current calendar.
     */
    private fun makeNextMonthHead() {
        for (date in 1..nextMonthHeadOffset) dateList.add(date)
    }
}