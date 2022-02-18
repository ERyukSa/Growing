package com.eryuksa.growing.miracle_morning.calendar.data

import org.joda.time.DateTime

/**
 * 참조
 * Created by WoochanLee on 25/03/2019.
 * https://woochan-dev.tistory.com/27 [우찬쓰 개발블로그]
 */
class BaseCalendar(millis: Long) {

    companion object {
        const val DAYS_OF_WEEK = 7
    }

    val currentDateTime = DateTime(millis)
    val prevDateTime: DateTime = currentDateTime.minusMonths(1)
    val nextDateTime: DateTime = currentDateTime.plusMonths(1)

    var prevMonthTailOffset = 0 // 첫 줄에 보여줄 저번달 날짜 개수
    var nextMonthHeadOffset = 0 // 막 줄에 보여줄 다음달 날짜 개수
    private var currentMonthMaxDate = 0

    val dateTimeList = arrayListOf<DateTime>() // 이번 달에 보여줄 날짜 리스트

    /**
     * Init calendar.
     */
    init {
        makeMonthDate() // date 생성
    }

    /**
     * make month date.
     */
    private fun makeMonthDate() {
        prevMonthTailOffset = currentDateTime.dayOfWeek % 7 // dayOfWeek: 월(1) ~ 일(7)
        makePrevMonthTail() // 첫 줄에 들어갈 이전 달의 날짜 추가

        currentMonthMaxDate = currentDateTime.dayOfMonth().maximumValue
        makeCurrentMonth(currentMonthMaxDate) // 이번 달 날짜 추가

        // 마지막 주의 남은 칸에 보여줄 다음달 날짜 개수
        nextMonthHeadOffset = 7 - nextDateTime.dayOfWeek // 7 - 다음달 1일의 dayOfWeek
        makeNextMonthHead() // 마지막 줄의 다음달 날짜 추가
    }

    /**
     * Generate data for the last month displayed before the first day of the current calendar.
     */
    private fun makePrevMonthTail() {
        val lastDate = prevDateTime.dayOfMonth().maximumValue
        val firstDate = lastDate - prevMonthTailOffset + 1

        for (i in firstDate-1 until lastDate) {
            dateTimeList.add(prevDateTime.plusDays(i))
        }
    }

    /**
     * Generate data for the current calendar.
     */
    private fun makeCurrentMonth(lastDayOfMonth: Int) {
        for (i in 0 until lastDayOfMonth) {
            dateTimeList.add(currentDateTime.plusDays(i))
        }
    }

    /**
     * Generate data for the next month displayed before the last day of the current calendar.
     */
    private fun makeNextMonthHead() {
        for (i in 0 until nextMonthHeadOffset) {
            dateTimeList.add(nextDateTime.plusDays(i))
        }
    }
}