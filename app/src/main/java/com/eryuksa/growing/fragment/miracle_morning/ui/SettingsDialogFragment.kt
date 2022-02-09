package com.eryuksa.growing.fragment.miracle_morning.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentResultListener
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.FragmentSettingsDialogBinding

const val REQUEST_START_DATES = "startDate"
const val REQUEST_GOAL_MINUTE = "gaolMinute"

class SettingsDialogFragment : DialogFragment(), FragmentResultListener {

    private lateinit var binding: FragmentSettingsDialogBinding

    private var year = 0
    private var month = 0
    private var dayOfMonth = 0

    private var goalMinutes = 0

    override fun onStart() {
        super.onStart()

        val layoutParams = requireDialog().window?.attributes
        layoutParams?.apply {
            width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        }

        requireDialog().window?.attributes = layoutParams

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_settings_dialog, container, false)

        /*
        buttonOk.setOnClickListener {
            val bundle = Bundle().apply {
            }

            parentFragmentManager.setFragmentResult(REQUEST_TODAY_STAMP, bundle)
            dismiss()
        }*/

        parentFragmentManager.apply {
            setFragmentResultListener(REQUEST_GOAL_MINUTE, viewLifecycleOwner, this@SettingsDialogFragment)
            setFragmentResultListener(REQUEST_START_DATES, viewLifecycleOwner, this@SettingsDialogFragment)

        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonStartDate.setOnClickListener {
            DateDialogFragment.newInstance().show(parentFragmentManager, DateDialogFragment.TAG)
        }

        binding.buttonGoalTime.setOnClickListener {
            GoalDialogFragment.newInstance().show(parentFragmentManager, GoalDialogFragment.TAG)
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            REQUEST_START_DATES -> {
                year = result.getInt(DateDialogFragment.RESULT_YEAR)
                month = result.getInt(DateDialogFragment.RESULT_MONTH)
                dayOfMonth = result.getInt(DateDialogFragment.RESULT_DATE)

                setStateDatesText()
            }

            REQUEST_GOAL_MINUTE -> {
                goalMinutes = result.getInt(GoalDialogFragment.RESULT_MINUTE)

                setGoalMinutesText()
            }
        }
    }

    /**
     * 미라클 모닝 시작 날짜 텍스트 설정
     */
    private fun setStateDatesText() {
        binding.textStartDate.text =
            getString(R.string.start_date_format, year, month, dayOfMonth)
    }

    /**
     * 목표 기상 시간 텍스트 설정
     */
    private fun setGoalMinutesText() {
        val beforeNoon: String =
            if (goalMinutes < 720) getString(R.string.am)
            else getString(R.string.pm)

        val hour =
            if (goalMinutes/60 > 12) goalMinutes/60 - 12
            else goalMinutes / 60

        binding.textGoal.text =
            getString(R.string.goal_time_format, beforeNoon, hour, goalMinutes%60)
    }

    companion object {

        const val TAG = "SettingsDialogFragment"

        fun newInstance(): SettingsDialogFragment {
            val bundle = Bundle().apply {
            }

            return SettingsDialogFragment().apply {
                arguments = bundle
            }
        }
    }
}
