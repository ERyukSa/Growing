package com.eryuksa.growing.fragment.miracle_morning.ui

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.FragmentMiracleMorningBinding
import com.eryuksa.growing.fragment.miracle_morning.data.view_model.MiracleMorningViewModel
import com.eryuksa.growing.fragment.miracle_morning.settings.SettingsDialogFragment
import java.util.*

class MiracleMorningFragment : Fragment(), FragmentResultListener {

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

        parentFragmentManager
            .setFragmentResultListener(REQUEST_SETTINGS, viewLifecycleOwner, this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            menu.clear() // 오버플로우 버튼 제거하는 역할
            inflateMenu(R.menu.fragment_miracle_morning)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_stamp -> {
                        StampDialogFragment().show(parentFragmentManager, StampDialogFragment.TAG)
                    }

                    R.id.menu_setting -> {
                        SettingsDialogFragment.newInstance()
                            .show(parentFragmentManager, SettingsDialogFragment.TAG)
                    }
                }

                true
            }
        }
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
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when(requestKey) {
            REQUEST_SETTINGS -> {
                val isChanged =
                    result.getBoolean(SettingsDialogFragment.ARG_CHANGED, true)

                if (isChanged) {
                    binding.viewPager.adapter = CalendarViewPagerAdapter(this)
                    binding.viewPager.setCurrentItem(viewModel.currentPosition, false)
                }
            }
        }
    }

    companion object {

        const val REQUEST_SETTINGS = "settings"

        fun newInstance(): MiracleMorningFragment {
            return MiracleMorningFragment()
        }
    }
}