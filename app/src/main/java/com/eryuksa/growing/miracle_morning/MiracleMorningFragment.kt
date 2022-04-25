package com.eryuksa.growing.miracle_morning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.FragmentMiracleMorningBinding
import com.eryuksa.growing.miracle_morning.settings.SettingsDialogFragment

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
        binding = FragmentMiracleMorningBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        parentFragmentManager
            .setFragmentResultListener(REQUEST_SETTINGS, viewLifecycleOwner, this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        setUpToolbar()
        setUpViewPager()
    }

    private fun setUpToolbar() {
        binding.toolbar.apply {
            menu.clear() // 오버플로우 버튼 제거하는 역할

            inflateMenu(R.menu.fragment_miracle_morning)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_setting -> {
                        SettingsDialogFragment.newInstance()

                            .show(parentFragmentManager, SettingsDialogFragment.TAG)
                    }
                }
                true
            }
        }
    }

    private fun setUpViewPager() {
        binding.viewPager.apply {
            adapter = CalendarViewPagerAdapter(this@MiracleMorningFragment, viewModel)

            setCurrentItem(viewModel.currentPosition, false)

            registerOnPageChangeCallback(viewModel.onCalendarChange) // 달력 스크롤 -> 년 월 텍스트 변경
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            REQUEST_SETTINGS -> {
                val isChanged =
                    result.getBoolean(SettingsDialogFragment.ARG_CHANGED, true)

                if (isChanged) { setUpViewPager() }
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