package com.eryuksa.growing.miracle_morning.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.growing.R
import com.eryuksa.growing.miracle_morning.stamp.StampDialogFragment

private const val TAG = "CalendarFragment"
const val REQUEST_TODAY_STAMP = "wakeUpTime"
const val ARG_MILLIS = "millis"

class CalendarFragment : Fragment(), FragmentResultListener, DoubleClickCallback {

    private lateinit var viewModel: CalendarViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MiracleCalendarAdapter

    private var millis = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        millis = arguments?.getLong(ARG_MILLIS) ?: System.currentTimeMillis()

        viewModel = ViewModelProvider(
            this,
            CalendarViewModel.Factory(millis)
        )[CalendarViewModel::class.java]

        lifecycle.addObserver(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        adapter = MiracleCalendarAdapter(viewModel, this)

        recyclerView.layoutManager =
            GridLayoutManager(requireContext(), MiracleCalendar.DAYS_OF_WEEK)
        recyclerView.adapter = adapter

        parentFragmentManager
            .setFragmentResultListener(REQUEST_TODAY_STAMP, viewLifecycleOwner, this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 기상시간 등록, 변경했을 때 UI 업데이트
        viewModel.updatedStamp.observe(viewLifecycleOwner) {
            adapter.updateStamp(it)
        }

        // Room에서 스탬프 객체 로딩 완료
        viewModel.isStampLoaded.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        viewModel.onFragmentResult(requestKey, result)
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
    override fun onItemDoubleClicked(clickedMillis: Long, dayOfMonth: Int, wakeUpMinutes: Int?) {
        StampDialogFragment.newInstance(clickedMillis, dayOfMonth, wakeUpMinutes ?: -1).show(
            this.parentFragmentManager,
            StampDialogFragment.TAG
        )
    }
}