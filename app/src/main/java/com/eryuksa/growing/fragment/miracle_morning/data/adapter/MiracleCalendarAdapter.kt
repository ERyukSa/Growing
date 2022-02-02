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
import com.eryuksa.growing.fragment.miracle_morning.data.view_model.DateViewModel
import java.util.*

class MiracleCalendarAdapter(millis: Long) :
    RecyclerView.Adapter<MiracleCalendarAdapter.DateHolder>(),
    RefreshCalendarListener {

    private val miracleCalendar: MiracleCalendar =
        MiracleCalendar(millis, this)
    private val dateList get() = miracleCalendar.dateList

    private var selectedPos = getSelectedPos()

    inner class DateHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.dateViewModel = DateViewModel(itemView.context)
            itemView.setOnClickListener(this)
        }

        fun bind(pos: Int) {
            binding.dateViewModel?.miracleDate = dateList[pos]

            itemView.isSelected = pos == selectedPos
        }

        override fun onClick(view: View?) {
            changeSelectedState()
        }

        /**
         *  selected 아이템 뷰 변경
         */
        private fun changeSelectedState() {
            if (adapterPosition == RecyclerView.NO_POSITION) return

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
    private fun getSelectedPos(): Int {
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
