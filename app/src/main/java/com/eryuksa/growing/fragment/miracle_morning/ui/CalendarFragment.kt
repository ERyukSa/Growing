package com.eryuksa.growing.fragment.miracle_morning.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.growing.R
import com.eryuksa.growing.fragment.miracle_morning.data.MiracleCalendar
import com.eryuksa.growing.fragment.miracle_morning.data.MiracleCalendarAdapter

const val ARG_MILLIS = "millis"

class CalendarFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
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
        recyclerView.layoutManager = GridLayoutManager(requireContext(), MiracleCalendar.DAYS_OF_WEEK)
        recyclerView.adapter = MiracleCalendarAdapter(millis)

        return view
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
}