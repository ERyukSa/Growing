package com.eryuksa.growing.miracle_morning.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentResultListener
import com.eryuksa.growing.R
import com.eryuksa.growing.config.GrowingApplication
import com.eryuksa.growing.databinding.FragmentSettingsDialogBinding

const val REQUEST_START_DATES = "startDate"
const val REQUEST_GOAL_MINUTE = "gaolMinute"

class SettingsDialogFragment : DialogFragment(), FragmentResultListener {

    private lateinit var binding: FragmentSettingsDialogBinding

    private val startDate: GrowingApplication.StartDate?
        get() = GrowingApplication.startDate

    private var isDateChanged = false
    private var isTimeChanged = false

    override fun onStart() {
        super.onStart()

        // 다이얼로그 자체 화면의 백그라운드 설정
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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

        startDate?.let{
            initStateDatesText() // 시작 날짜 텍스트 초기회
        }

        GrowingApplication.goalMinutes?.let {
            initGoalMinutesText() // 목표 시간 텍스트 초기화
        }

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

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        // 설정이 변경되었을 때만 결과를 전달한다
        binding.buttonOk.setOnClickListener {
            if (isDateChanged) {
                saveStartDate()
                parentFragmentManager
                    .setFragmentResult(RESULT_SETTINGS, bundleOf(ARG_CHANGED to true))
            }

            if (isTimeChanged) {
                saveGoalTime()
                parentFragmentManager
                    .setFragmentResult(RESULT_SETTINGS, bundleOf(ARG_CHANGED to true))
            }

            dismiss()
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            REQUEST_START_DATES -> {
                if (startDate == null) {
                    GrowingApplication.startDate =
                        GrowingApplication.StartDate(0, 0, 0)
                }

                startDate!!.apply{
                    year = result.getInt(DateDialogFragment.RESULT_YEAR)
                    month = result.getInt(DateDialogFragment.RESULT_MONTH)
                    date = result.getInt(DateDialogFragment.RESULT_DATE)
                }

                initStateDatesText() // 바뀐 날짜로 텍스트 초기화
                isDateChanged = true
            }

            REQUEST_GOAL_MINUTE -> {
                GrowingApplication.goalMinutes = result.getInt(GoalDialogFragment.RESULT_MINUTE)

                initGoalMinutesText() // 바뀐 시간으로 텍스트 초기화
                isTimeChanged = true
            }
        }
    }

    /**
     * 미라클 모닝 시작 날짜 텍스트 설정
     */
    private fun initStateDatesText() {
        startDate?.let {
            binding.textStartDate.text =
                getString(R.string.start_date_format, it.year, it.month, it.date)
        }
    }

    /**
     * 목표 기상 시간 텍스트 설정
     */
    private fun initGoalMinutesText() {
        val goalMinutes = GrowingApplication.goalMinutes ?: return

        val beforeNoon: String =
            if (goalMinutes < 720) getString(R.string.am)
            else getString(R.string.pm)

        val hour =
            if (goalMinutes/60 > 12) goalMinutes/60 - 12
            else goalMinutes / 60

        binding.textGoal.text =
            getString(R.string.goal_time_format, beforeNoon, hour, goalMinutes%60)
    }

    /**
     * 바뀐 시작 날짜를 프리퍼런스에 저장
     */
    private fun saveStartDate() {
        startDate?.let {
            GrowingApplication.sSharedPreferences
                .edit()
                .putInt(GrowingApplication.START_YEAR, it.year)
                .putInt(GrowingApplication.START_MONTH, it.month)
                .putInt(GrowingApplication.START_DATE, it.date)
                .apply()
        }
    }

    private fun saveGoalTime() {
        GrowingApplication.goalMinutes?.let {
            GrowingApplication.sSharedPreferences
                .edit()
                .putInt(GrowingApplication.ARG_GOAL_MINUTES, it)
                .apply()
        }
    }

    companion object {

        const val TAG = "SettingsDialogFragment"
        const val RESULT_SETTINGS = "settings"
        const val ARG_CHANGED = "changed"

        fun newInstance(): SettingsDialogFragment {
            val bundle = Bundle().apply {
            }

            return SettingsDialogFragment().apply {
                arguments = bundle
            }
        }
    }
}
