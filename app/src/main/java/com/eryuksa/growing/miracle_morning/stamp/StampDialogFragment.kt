package com.eryuksa.growing.miracle_morning.stamp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.eryuksa.growing.R
import com.eryuksa.growing.miracle_morning.calendar.ARG_MILLIS
import com.eryuksa.growing.miracle_morning.calendar.REQUEST_TODAY_STAMP
import com.eryuksa.growing.miracle_morning.model.MiracleDate
import org.joda.time.format.DateTimeFormat

class StampDialogFragment : DialogFragment() {

    private lateinit var dateTextView: TextView
    private lateinit var timePicker: TimePicker
    private lateinit var buttonOk: Button
    private lateinit var buttonCancel: Button

    private var dateMillis: Long = 0
    private var mWakeUpMinute: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            dateMillis = it.getLong(ARG_MILLIS, System.currentTimeMillis())
            mWakeUpMinute = it.getInt(ARG_MINUTE, -1)
        }
    }

    override fun onStart() {
        super.onStart()

        // 다이얼로그의 자체 백그라운드 투명하게 설정
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 크기 설정
        val layoutParams = requireDialog().window?.attributes
        layoutParams?.width = (resources.displayMetrics.widthPixels * 0.8).toInt()

        requireDialog().window?.attributes = layoutParams

        // 스탬프가 찍히지 않았으면 지금 시간으로 설정
        if (mWakeUpMinute == -1) {
            mWakeUpMinute = (System.currentTimeMillis() / 60000).toInt()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stamp_dialog, container, false)

        dateTextView = view.findViewById(R.id.text_current_date)
        timePicker = view.findViewById(R.id.time_picker)
        buttonOk = view.findViewById(R.id.button_ok)
        buttonCancel = view.findViewById(R.id.button_cancel)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDateText()

        timePicker.apply {
            this.hour = mWakeUpMinute / 60
            this.minute = mWakeUpMinute % 60
        }

        buttonOk.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(RESULT_MINUTES, timePicker.hour * 60 + timePicker.minute) // 분
            }

            parentFragmentManager.setFragmentResult(REQUEST_TODAY_STAMP, bundle)
            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    // 클릭된 날짜 텍스트 설정
    private fun initDateText() {
        dateTextView.text =
            DateTimeFormat.forPattern(getString(R.string.date_format)).print(dateMillis)
    }

    companion object {
        const val TAG = "StampDialog"

        const val ARG_MINUTE = "minute"

        const val RESULT_MINUTES = "minuteOfDay"

        fun newInstance(miracleDate: MiracleDate): StampDialogFragment {
            val bundle = Bundle().apply {
                putLong(ARG_MILLIS, miracleDate.dateTime.millis)
                putInt(ARG_MINUTE, miracleDate.wakeUpMinutes.value ?: -1)
            }

            return StampDialogFragment().apply {
                arguments = bundle
            }
        }
    }
}
