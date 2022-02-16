package com.eryuksa.growing.miracle_morning

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2
import org.joda.time.DateTime

private const val TAG = "MorningViewModel"

class MiracleMorningViewModel : ViewModel() {

    /* 화면에서 보여주는 달 1일의 timeInMillis -> Databinding에서 사용 */
    private var _currentMonthMillis =
        MutableLiveData(DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis)
    val currentMonthMillis get() = _currentMonthMillis

    // 이번 달의 1일의 timeInMillis: 첫 진입할 때 보여줄 날짜
    private val start: Long = DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis
    private val startDateTime = DateTime(start)

    // config change에 대응하기 위한 viewPager의 직전 position
    private var _currentPosition = START_POSITION
    val currentPosition get() = _currentPosition

    val onCalendarChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            _currentPosition = position
            _currentMonthMillis.value =
                startDateTime.plusMonths(position - START_POSITION).millis

            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }

    fun getPositionMillis(position: Int): Long
        = startDateTime.plusMonths(position - START_POSITION).millis

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2
    }
}