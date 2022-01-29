package com.eryuksa.growing.fragment.miracle_morning.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.FragmentMiracleMorningBinding
import com.eryuksa.growing.fragment.miracle_morning.data.CalendarViewPagerAdapter

class MiracleMorningFragment : Fragment() {

    private lateinit var binding: FragmentMiracleMorningBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_miracle_morning, container, false)

        initBinding()

        return binding.root
    }


    private fun initBinding() {
        binding.viewPager.apply {
            adapter = CalendarViewPagerAdapter(this@MiracleMorningFragment)
            binding.viewPager.currentItem = CalendarViewPagerAdapter.START_POSITION
        }
            /*binding.recyclerView.adapter = MiracleCalendarAdapter().also {
                binding.calendarAdapter = it
            }*/
            // binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), MiracleCalendar.DAYS_OF_WEEK)
        }

    companion object {
        fun newInstance(): MiracleMorningFragment {
            return MiracleMorningFragment()
        }
    }
}