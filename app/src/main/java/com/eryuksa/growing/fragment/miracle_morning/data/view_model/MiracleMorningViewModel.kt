package com.eryuksa.growing.fragment.miracle_morning.data.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2
import com.eryuksa.growing.fragment.miracle_morning.data.adapter.CalendarViewPagerAdapter
import org.joda.time.DateTime

private const val TAG = "MorningViewModel"

class MiracleMorningViewModel : ViewModel() {
    /* 달의 첫 번째 Day timeInMillis*/
    private var start =
        MutableLiveData(DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis)
    val millisLiveData get() = start

    val onCalendarChange = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            start.value =
                DateTime().plusMonths(position - CalendarViewPagerAdapter.START_POSITION).millis
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }
}