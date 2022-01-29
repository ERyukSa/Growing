package com.eryuksa.growing.fragment.miracle_morning.data

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eryuksa.growing.fragment.miracle_morning.ui.CalendarFragment
import org.joda.time.DateTime

class CalendarViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /* 달의 첫 번째 Day timeInMillis*/
    private val start: Long = DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun getItemId(position: Int): Long {
        return DateTime(start).plusMonths(position - START_POSITION).millis
    }

    override fun createFragment(position: Int): Fragment {
        val millis = getItemId(position)
        return CalendarFragment.newInstance(millis)
    }

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2
    }
}