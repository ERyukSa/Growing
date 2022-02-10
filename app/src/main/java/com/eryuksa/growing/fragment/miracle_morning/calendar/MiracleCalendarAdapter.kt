package com.eryuksa.growing.fragment.miracle_morning.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.ItemDateBinding
import com.eryuksa.growing.fragment.miracle_morning.calendar.model.MiracleDate
import com.eryuksa.growing.fragment.miracle_morning.calendar.model.MiracleStamp
import com.eryuksa.growing.fragment.miracle_morning.calendar.model.MonthType
import java.util.*


/**
 * 달력 날짜를 더블 클릭했을 때 프래그먼트에서 할 동작
 */
interface DoubleClickCallback {
    fun onItemDoubleClicked(clickedMillis: Long, dayOfMonth: Int, wakeUpMinutes: Int?)
}

class MiracleCalendarAdapter(
    private val calendarViewModel: CalendarViewModel,
    doubleClickCallback: DoubleClickCallback
) :
    RecyclerView.Adapter<MiracleCalendarAdapter.DateHolder>() {

    private val onDoubleClicked = doubleClickCallback::onItemDoubleClicked

    private val miracleCalendar: MiracleCalendar
        get() = calendarViewModel.miracleCalendar
    private val dateList
        get() = miracleCalendar.dateList
    private val calendar: Calendar
        get() = miracleCalendar.calendar

    private val millis: Long get() = calendarViewModel.monthMillis // 지금 보여주는 달
    private val prevMonthMillis get() = calendarViewModel.prevMonthMillis
    private val nextMonthMillis get() = calendarViewModel.nextMonthMillis

    private var selectedPos = initSelectedPos()

    inner class DateHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        lateinit var miracleDate: MiracleDate

        init {
            binding.dateViewModel = DateViewModel(itemView.context, calendar)
            itemView.setOnClickListener(this)
        }

        fun bind(pos: Int) {
            miracleDate = dateList[pos]
            binding.dateViewModel?.miracleDate = miracleDate

            itemView.isSelected = pos == selectedPos
        }

        override fun onClick(view: View?) {
            if (adapterPosition == RecyclerView.NO_POSITION) return

            if (selectedPos == adapterPosition) {
                val monthMillis = when (miracleDate.monthType) {
                    MonthType.PREV -> prevMonthMillis
                    MonthType.CURRENT -> millis
                    MonthType.NEXT -> nextMonthMillis
                }

                onDoubleClicked(monthMillis, miracleDate.dayOfMonth, miracleDate.wakeUpMinutes.value)
            } else {
                changeSelectedState()
            }
        }

        /**
         * selected 아이템 뷰 변경
         */
        private fun changeSelectedState() {
            notifyItemChanged(selectedPos) // 이전에 선택된 아이템뷰의 selected 해제
            // 현재 포지션의 아이템뷰로 변경
            // notifyItemChanged()를 처리하는데 시간이 걸리기 때문에 동작하는 것 같다.
            selectedPos = adapterPosition
            notifyItemChanged(selectedPos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateHolder {
        val binding: ItemDateBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_date,
            parent,
            false
        )

        setItemViewHeight(binding.root, parent)

        return DateHolder(binding)
    }

    override fun onBindViewHolder(holder: DateHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    /**
     * 이번 달이면 오늘 날짜를 selected, 아니면 1일을 선택
     */
    private fun initSelectedPos(): Int {
        return if (miracleCalendar.calendar.get(Calendar.MONTH) == MiracleCalendar.currentDateTime.monthOfYear - 1) {
            miracleCalendar.prevMonthTailOffset + MiracleCalendar.currentDateTime.dayOfMonth - 1
        } else {
            miracleCalendar.prevMonthTailOffset
        }
    }

    /**
     * 캘린더의 라인 개수에 따라 아이템 뷰 높이 설정
     */
    private fun setItemViewHeight(itemView: View, parent: ViewGroup) {
        val lengthOfRow = if (dateList.size % MiracleCalendar.DAYS_OF_WEEK != 0) {
            dateList.size / MiracleCalendar.DAYS_OF_WEEK + 1
        } else {
            dateList.size / MiracleCalendar.DAYS_OF_WEEK
        }

        itemView.layoutParams.height = parent.height / lengthOfRow
    }

    /**
     * 사용자가 일어난 시간을 기록했을 때
     */
    fun updateStamp(stamp: MiracleStamp) {

        // 업데이트된 데이터의 인덱스
        val position = when {
            stamp.monthMillis < millis -> stamp.dayOfMonth - dateList[0].dayOfMonth

            stamp.monthMillis == millis -> miracleCalendar.prevMonthTailOffset + stamp.dayOfMonth - 1

            else -> (dateList.size - miracleCalendar.nextMonthHeadOffset) + stamp.dayOfMonth - 1
        }

        dateList[position].wakeUpMinutes.value = stamp.wakeUpMinutes
        notifyItemChanged(position)
    }
}
