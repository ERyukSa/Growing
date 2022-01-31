package com.eryuksa.growing.fragment.miracle_morning.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.FragmentMiracleMorningBinding
import com.eryuksa.growing.fragment.miracle_morning.data.CalendarViewPagerAdapter
import com.eryuksa.growing.fragment.miracle_morning.data.MiracleMorningViewModel

class MiracleMorningFragment : Fragment() {

    private lateinit var binding: FragmentMiracleMorningBinding

    private val viewModel: MiracleMorningViewModel by lazy {
        ViewModelProvider(this)[MiracleMorningViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_miracle_morning, container, false)

        initBinding(savedInstanceState)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    private fun initBinding(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.viewPager.apply {
            adapter =
                CalendarViewPagerAdapter(this@MiracleMorningFragment)

            if (savedInstanceState == null) {
                setCurrentItem(CalendarViewPagerAdapter.START_POSITION, false)
            }

            registerOnPageChangeCallback(viewModel.onCalendarChange) // 달력 스크롤 -> 년 월 텍스트 변경


            /*binding.recyclerView.adapter = MiracleCalendarAdapter().also {
            binding.calendarAdapter = it
        }*/
            // binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), MiracleCalendar.DAYS_OF_WEEK)
        }
    }

    companion object {
        fun newInstance(): MiracleMorningFragment {
            return MiracleMorningFragment()
        }
    }
}