package com.eryuksa.growing.fragment.miracle_morning.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.ItemDateBinding
import java.util.*

enum class DayType{
    WEEKDAY, SATURDAY, SUNDAY
}

class MiracleCalendarAdapter : RecyclerView.Adapter<MiracleCalendarAdapter.DateHolder>(),
    RefreshCalendarListener {

    private val miracleCalendar: MiracleCalendar =
        MiracleCalendar.newInstance(this)

    val currentCalendar: LiveData<Calendar>
        get() = miracleCalendar.calendarLiveData

    inner class DateHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.miracleDate = DateViewModel(itemView.context)
        }

        fun bind(dateNumber: Int, isInThisMonth: Boolean, dayType: DayType) {
            binding.miracleDate?.apply {
                this.dateNumber = dateNumber
                this.isInThisMonth = isInThisMonth
                this.dayType = dayType
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateHolder {
        val binding: ItemDateBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_date,
            parent,
            false
        )

        // item_date 높이
        binding.root.layoutParams.height = parent.height / MiracleCalendar.LOW_OF_CALENDAR

        return DateHolder(binding)
    }

    override fun onBindViewHolder(holder: DateHolder, position: Int) {
        val firstDateIndex = miracleCalendar.prevMonthTailOffset
        val lastDateIndex = miracleCalendar.dateList.size - miracleCalendar.nextMonthHeadOffset - 1

        // 이번 달 날짜인가? -> 텍스트 컬러 선택할 때 사용
        val isInThisMonth = (position in firstDateIndex..lastDateIndex)
        holder.bind(miracleCalendar.dateList[position], isInThisMonth, getDayType(position))
    }

    override fun getItemCount(): Int {
        return miracleCalendar.dateList.size
    }

    override fun onRefresh(calendar: Calendar) {
        notifyDataSetChanged()
    }

    // 인덱스에 따라 평일, 토요일, 일요일 구분
    private fun getDayType(position: Int): DayType {
        return when(position % 7) {
            0 -> DayType.SUNDAY
            6 -> DayType.SATURDAY
            else -> DayType.WEEKDAY
        }
    }
}
