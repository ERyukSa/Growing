package com.eryuksa.growing.fragment.miracle_morning.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.ItemDateBinding
import com.eryuksa.growing.fragment.miracle_morning.data.MiracleCalendar
import com.eryuksa.growing.fragment.miracle_morning.data.RefreshCalendarListener
import com.eryuksa.growing.fragment.miracle_morning.data.model.MiracleDate
import com.eryuksa.growing.fragment.miracle_morning.data.model.MonthType
import com.eryuksa.growing.fragment.miracle_morning.data.view_model.DateViewModel
import java.util.*

interface DoubleClickCallback {
    fun onItemDoubleClicked(monthType: MonthType, dayOfMonth: Int)
}

class MiracleCalendarAdapter(private val millis: Long, doubleClickCallback: DoubleClickCallback) :
    RecyclerView.Adapter<MiracleCalendarAdapter.DateHolder>(),
    RefreshCalendarListener {

    private val onDoubleClicked = doubleClickCallback::onItemDoubleClicked

    private val miracleCalendar: MiracleCalendar =
        MiracleCalendar(millis, this)
    private val dateList get() = miracleCalendar.dateList

    private var selectedPos = initSelectedPos()

    fun updateStamp(monthTypeOrdinal: Int, dayOfMonth: Int, minutesOfDay: Int) {
        val pos = when(monthTypeOrdinal) {
            MonthType.PREV.ordinal -> dayOfMonth - dateList[0].dateNumber
            MonthType.CURRENT.ordinal -> miracleCalendar.prevMonthTailOffset + dayOfMonth - 1
            else -> miracleCalendar.prevMonthTailOffset + miracleCalendar.currentMonthMaxDate + dayOfMonth - 1
        }

        dateList[pos].wakeUpMinutes.value = minutesOfDay
        notifyItemChanged(pos)
    }

    inner class DateHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        lateinit var miracleDate: MiracleDate

        init {
            binding.dateViewModel = DateViewModel(itemView.context)
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
                onDoubleClicked(miracleDate.monthType, miracleDate.dateNumber)
            } else {
                changeSelectedState()
            }
        }

        /**
         *  selected 아이템 뷰 변경
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

    override fun onRefresh(calendar: Calendar) {
        notifyDataSetChanged()
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
}
