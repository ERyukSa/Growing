package com.eryuksa.growing.miracle_morning.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.growing.databinding.ItemDateBinding
import com.eryuksa.growing.miracle_morning.model.MiracleDate

class CalendarDateAdapter(
    private val calendarViewModel: CalendarViewModel,
    doubleClickCallback: DoubleClickCallback
) :
    RecyclerView.Adapter<CalendarDateAdapter.DateHolder>() {

    /**
     * 달력 날짜를 더블 클릭했을 때 프래그먼트에서 할 동작
     */
    interface DoubleClickCallback {
        fun onItemDoubleClick(miracleDate: MiracleDate)
    }

    private val onDoubleClick = doubleClickCallback::onItemDoubleClick

    private val miracleDateList
        get() = calendarViewModel.miracleDateList

    inner class DateHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private lateinit var miracleDate: MiracleDate

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(pos: Int) {
            miracleDate = miracleDateList[pos]
            binding.viewModel = DateViewModel(miracleDate, calendarViewModel.currentDateTime)

            if (adapterPosition == RecyclerView.NO_POSITION) return

            itemView.isSelected = pos == calendarViewModel.selectedDatePos.value
        }

        override fun onClick(view: View?) {
            if (adapterPosition == RecyclerView.NO_POSITION) return

            // selected 상태에서 한번 더 클릭했을 때
            if (calendarViewModel.selectedDatePos.value == adapterPosition) {
                onDoubleClick(miracleDate)
            } else {
                calendarViewModel.changeSelectedPos(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateHolder {
        val binding = ItemDateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        // 라인 개수에 따라 뷰 높이 설정
        calendarViewModel.setItemViewHeight(binding.root, parent)

        return DateHolder(binding)
    }

    override fun onBindViewHolder(holder: DateHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int =
        miracleDateList.size

}
