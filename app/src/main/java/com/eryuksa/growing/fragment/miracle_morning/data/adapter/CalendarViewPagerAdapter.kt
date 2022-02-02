package com.eryuksa.growing.fragment.miracle_morning.data.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eryuksa.growing.fragment.miracle_morning.ui.CalendarFragment
import org.joda.time.DateTime

private const val TAG = "CalendarViewPagerAdapt"

class CalendarViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /*
    getItemId와 containsItem을 둘 다 구현하거나, 안한다.
    기본 구현된 함수들은 이동, 추가, 삭제되지 않는 dataSet일 때 올바르게 작동한다
    getItemId()만 오버라이딩하면 구성 변경시 에러 발생 --> onSaveInstanceState()가 호출되지 않는다
    Note: The DiffUtil utility class relies on identifying items by ID.
    If you are using ViewPager2 to page through a mutable collection,
    you must also override getItemId() and containsItem().
   */

    /* 달의 첫 번째 Day timeInMillis*/
    private var start: Long = DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    // 지금 코드에서는 getItemId()랑 containsItem()이 없어도 된다 -> dateSet에 변경이 일어나지 않기 때문이다.
    override fun getItemId(position: Int): Long {
        return DateTime(start).plusMonths(position - START_POSITION).millis
    }

    override fun containsItem(itemId: Long): Boolean {
        val date = DateTime(itemId)
        return date.dayOfMonth == 1 && date.millisOfDay == 0
    }

    override fun createFragment(position: Int): Fragment {
        val movedStartMillis = getItemId(position)
        return CalendarFragment.newInstance(movedStartMillis)
    }

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2
    }
}