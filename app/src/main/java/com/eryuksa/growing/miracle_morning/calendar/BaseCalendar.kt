package com.eryuksa.growing.miracle_morning.calendar

import com.eryuksa.growing.miracle_morning.calendar.model.DayType
import com.eryuksa.growing.miracle_morning.calendar.model.MiracleDate
import com.eryuksa.growing.miracle_morning.calendar.model.MonthType
import java.util.*

/**
 * 참조
 * Created by WoochanLee on 25/03/2019.
 * https://woochan-dev.tistory.com/27 [우찬쓰 개발블로그]
 */
class BaseCalendar(millis: Long) {

    companion object {
        const val DAYS_OF_WEEK = 7
    }

    val calendar: Calendar = Calendar.getInstance()

    val prevMonthMillis: Long
    val nextMonthMillis: Long

    var prevMonthTailOffset = 0 // 첫 줄에 보여줄 저번달 날짜 개수
    var nextMonthHeadOffset = 0 // 막 줄에 보여줄 다음달 날짜 개수
    var currentMonthMaxDate = 0

    val dateList = arrayListOf<MiracleDate>() // 이번 달에 보여줄 날짜(일) 리스트

    /**
     * Init calendar.
     */
    init {
        calendar.timeInMillis = millis // 지금 보여주는 달의 1일

        // 이전 달 millis
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        prevMonthMillis = calendar.timeInMillis

        // 다음 달 millis
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 2)
        nextMonthMillis = calendar.timeInMillis

        calendar.timeInMillis = millis // 원상 복구
        makeMonthDate() // date 생성
    }

    /**
     * make month date.
     */
    private fun makeMonthDate() {
        dateList.clear()

        calendar.set(Calendar.DATE, 1)

        currentMonthMaxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        prevMonthTailOffset = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY

        makePrevMonthTail(calendar.clone() as Calendar) // 첫 줄에 들어갈 이전 달의 날짜 추가
        makeCurrentMonth(calendar) // 이번 달 날짜 추가

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

        // 마지막 주의 남은 칸에 보여줄 다음달 날짜 개수
        nextMonthHeadOffset = Calendar.SATURDAY - calendar.get(Calendar.DAY_OF_WEEK)
        makeNextMonthHead() // 마지막 줄의 다음달 날짜 추가
    }

    /**
     * Generate data for the last month displayed before the first day of the current calendar.
     */
    private fun makePrevMonthTail(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        val lastDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDate = lastDate - prevMonthTailOffset + 1

        for (dateNumber in firstDate..lastDate) {
            dateList.add(MiracleDate(dateNumber, MonthType.PREV, getDayType(dateList.size)))
        }
    }

    /**
     * Generate data for the current calendar.
     */
    private fun makeCurrentMonth(calendar: Calendar) {
        for (dateNumber in 1..calendar.getActualMaximum(Calendar.DATE)) {
            dateList.add(MiracleDate(dateNumber, MonthType.CURRENT, getDayType(dateList.size)))
        }
    }

    /**
     * Generate data for the next month displayed before the last day of the current calendar.
     */
    private fun makeNextMonthHead() {
        for (dateNumber in 1..nextMonthHeadOffset) {
            dateList.add(MiracleDate(dateNumber, MonthType.NEXT, getDayType(dateList.size)))
        }
    }

    /**
     * 인덱스에 따라 평일, 토요일, 일요일 구분
     */
    private fun getDayType(position: Int): DayType {
        return when(position % 7) {
            0 -> DayType.SUNDAY
            6 -> DayType.SATURDAY
            else -> DayType.WEEKDAY
        }
    }
}