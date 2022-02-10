package com.eryuksa.growing.fragment.miracle_morning.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.eryuksa.growing.R
import com.eryuksa.growing.fragment.miracle_morning.calendar.ARG_MILLIS
import com.eryuksa.growing.fragment.miracle_morning.calendar.MiracleCalendar
import com.eryuksa.growing.fragment.miracle_morning.calendar.REQUEST_TODAY_STAMP

class StampDialogFragment : DialogFragment() {

    private lateinit var timePicker: TimePicker
    private lateinit var buttonOk: Button
    private lateinit var buttonCancel: Button

    private var monthMillis: Long = 0
    private var mDayOfMonth = 1 // 일
    private var mWakeUpMinute = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            monthMillis = it.getLong(ARG_MILLIS, System.currentTimeMillis())
            mDayOfMonth = it.getInt(ARG_DATE, 1)
            mWakeUpMinute = it.getInt(ARG_MINUTE, -1)
        }

    }

    override fun onStart() {
        super.onStart()

        val layoutParams = requireDialog().window?.attributes
        layoutParams?.width = (resources.displayMetrics.widthPixels * 0.9).toInt()

        requireDialog().window?.attributes = layoutParams

        // 스탬프가 찍히지 않았으면 지금 시간으로 설정
        if (mWakeUpMinute == -1) {
            mWakeUpMinute = MiracleCalendar.currentDateTime.minuteOfDay
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stamp_dialog, container, false)

        timePicker = view.findViewById<TimePicker>(R.id.time_picker).apply {
            this.hour = mWakeUpMinute / 60
            this.minute = mWakeUpMinute % 60
        }
        buttonOk = view.findViewById(R.id.button_ok)
        buttonCancel = view.findViewById(R.id.button_cancel)

        buttonOk.setOnClickListener {
            val bundle = Bundle().apply {
                putLong(RESULT_MILLIS, monthMillis)  // 월
                putInt(RESULT_DATE, mDayOfMonth )    // 일
                putInt(RESULT_MINUTES, timePicker.hour * 60 + timePicker.minute) // 분
            }

            parentFragmentManager.setFragmentResult(REQUEST_TODAY_STAMP, bundle)
            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }

        return view
    }

    companion object {
        const val TAG = "StampDialog"

        const val ARG_DATE = "dayOfMonth"
        private const val ARG_MINUTE = "minute"

        const val RESULT_MINUTES = "minuteOfDay"
        const val RESULT_MILLIS = "millis"
        const val RESULT_DATE = "dayOfMonth"

        fun newInstance(millis: Long, dayOfMonth: Int, wakeUpMinutes: Int): StampDialogFragment {
            val bundle = Bundle().apply {
                putLong(ARG_MILLIS, millis)
                putInt(ARG_DATE, dayOfMonth)
                putInt(ARG_MINUTE, wakeUpMinutes)
            }

            return StampDialogFragment().apply {
                arguments = bundle
            }
        }
    }
}
