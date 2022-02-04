package com.eryuksa.growing.fragment.miracle_morning.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.eryuksa.growing.R
import com.eryuksa.growing.fragment.miracle_morning.data.model.MonthType
import org.joda.time.DateTime

class StampDialogFragment : DialogFragment() {

    private lateinit var timePicker: TimePicker
    private lateinit var buttonOk: Button
    private lateinit var buttonCancel: Button

    private var millis: Long = 0
    private var monthTypeOrdinal = 1
    private var mDayOfMonth = 1

    private var mMinuteOfDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            millis = it.getLong(ARG_MILLIS, System.currentTimeMillis())
            monthTypeOrdinal = it.getInt(ARG_MONTH_TYPE, 1)
            mDayOfMonth = it.getInt(ARG_DATE, 1)
        }

    }

    override fun onStart() {
        super.onStart()

        val layoutParams = requireDialog().window?.attributes
        layoutParams?.width = (resources.displayMetrics.widthPixels * 0.9).toInt()

        requireDialog().window?.attributes = layoutParams

        with(DateTime()) {
            mMinuteOfDay = this.minuteOfDay
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stamp_dialog, container, false)

        timePicker = view.findViewById(R.id.time_picker)
        buttonOk = view.findViewById(R.id.button_ok)
        buttonCancel = view.findViewById(R.id.button_cancel)

        timePicker.setOnTimeChangedListener { timePicker, hour, minute ->
            mMinuteOfDay = hour * 60 + minute
        }

        buttonOk.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(RESULT_MINUTES, mMinuteOfDay)
                putInt(RESULT_DATE, mDayOfMonth)
                putInt(RESULT_MONTH_TYPE, monthTypeOrdinal)
            }
            parentFragmentManager.setFragmentResult(REQUEST_TODAY_STAMP, bundle)
            dismiss()
        }


        val monthMillis = when (monthTypeOrdinal) {
            MonthType.PREV.ordinal -> DateTime(millis).minusMonths(1).millis
            MonthType.CURRENT.ordinal -> millis
            else -> DateTime(millis).plusMonths(1).millis
        }

        return view
    }

    companion object {
        const val TAG = "StampDialog"

        const val ARG_MONTH_TYPE = "month"
        const val ARG_DATE = "dayOfMonth"

        const val RESULT_MINUTES = "minuteOfDay"
        const val RESULT_MONTH_TYPE = "month"
        const val RESULT_DATE = "dayOfMonth"

        fun newInstance(millis: Long, monthTypeInt: Int, dayOfMonth: Int): StampDialogFragment {
            val bundle = Bundle().apply {
                putLong(ARG_MILLIS, millis)
                putInt(ARG_MONTH_TYPE, monthTypeInt)
                putInt(ARG_DATE, dayOfMonth)
            }

            return StampDialogFragment().apply {
                arguments = bundle
            }
        }
    }
}
