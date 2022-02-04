package com.eryuksa.growing.fragment.miracle_morning.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.growing.R
import com.eryuksa.growing.fragment.miracle_morning.data.MiracleCalendar
import com.eryuksa.growing.fragment.miracle_morning.data.adapter.DoubleClickCallback
import com.eryuksa.growing.fragment.miracle_morning.data.adapter.MiracleCalendarAdapter
import com.eryuksa.growing.fragment.miracle_morning.data.model.MonthType

private const val TAG = "CalendarFragment"
const val REQUEST_TODAY_STAMP = "wakeUpTime"
const val ARG_MILLIS = "millis"

class CalendarFragment : Fragment(), FragmentResultListener, DoubleClickCallback {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MiracleCalendarAdapter

    private var millis = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        millis = arguments?.getLong(ARG_MILLIS) ?: System.currentTimeMillis()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        adapter = MiracleCalendarAdapter(millis, this)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), MiracleCalendar.DAYS_OF_WEEK)
        recyclerView.adapter = adapter

        parentFragmentManager.setFragmentResultListener(
            REQUEST_TODAY_STAMP,
            viewLifecycleOwner,
            this
        )

        return view
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        if (requestKey == REQUEST_TODAY_STAMP) {
            val monthTypeOrdinal = result.getInt(StampDialogFragment.RESULT_MONTH_TYPE)
            val dayOfMonth = result.getInt(StampDialogFragment.RESULT_DATE)
            val minutesOfDay = result.getInt(StampDialogFragment.RESULT_MINUTES)

            adapter.updateStamp(monthTypeOrdinal, dayOfMonth, minutesOfDay)
        }
    }

    companion object {
        fun newInstance(millis: Long): CalendarFragment {
            val bundle = Bundle().apply {
                putLong(ARG_MILLIS, millis)
            }

            return CalendarFragment().apply {
                arguments = bundle
            }
        }
    }

    /**
     * 달력 아이템 더블 클릭했을 때 동작 설정
     */
    override fun onItemDoubleClicked(monthType: MonthType, dayOfMonth: Int){
        StampDialogFragment.newInstance(millis, monthType.ordinal, dayOfMonth).show(
            this.parentFragmentManager,
            StampDialogFragment.TAG
        )
    }
}