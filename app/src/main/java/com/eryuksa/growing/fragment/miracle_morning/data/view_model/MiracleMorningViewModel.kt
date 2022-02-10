package com.eryuksa.growing.fragment.miracle_morning.data.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2
import com.eryuksa.growing.fragment.miracle_morning.ui.CalendarViewPagerAdapter
import org.joda.time.DateTime

private const val TAG = "MorningViewModel"

class MiracleMorningViewModel : ViewModel() {
    /* 달의 첫 번째 Day timeInMillis*/
    private var monthMillis =
        MutableLiveData(DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis)
    val millisLiveData get() = monthMillis

    var currentPosition = CalendarViewPagerAdapter.START_POSITION

    val onCalendarChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            currentPosition = position
            monthMillis.value =
                DateTime().plusMonths(position - CalendarViewPagerAdapter.START_POSITION).millis

            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }
}