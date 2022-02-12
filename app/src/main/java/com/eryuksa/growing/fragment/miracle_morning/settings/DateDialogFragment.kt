package com.eryuksa.growing.fragment.miracle_morning.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.eryuksa.growing.R
import com.eryuksa.growing.config.GrowingApplication

class DateDialogFragment : DialogFragment() {

    private lateinit var datePicker: DatePicker
    private lateinit var buttonOk: Button
    private lateinit var buttonCancel: Button

    private val startDate: GrowingApplication.StartDate?
        get() = GrowingApplication.startDate
    
    override fun onStart() {
        super.onStart()

        // 다이얼로그의 자체 백그라운드 투명하게 설정
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val layoutParams = requireDialog().window?.attributes
        layoutParams?.width = (resources.displayMetrics.widthPixels * 0.85).toInt()

        requireDialog().window?.attributes = layoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_date_dialog, container, false)

        datePicker = view.findViewById(R.id.date_picker)
        // date picker header 삭제
        val pickerHeaderId = datePicker.getChildAt(0)
            .resources.getIdentifier("date_picker_header", "id", "android")
        datePicker.findViewById<View>(pickerHeaderId).visibility = View.GONE


        buttonOk = view.findViewById(R.id.button_ok)
        buttonCancel = view.findViewById(R.id.button_cancel)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 이전과 달라졌을 때만 결과 전달
        buttonOk.setOnClickListener {
            if (startDate?.year != datePicker.year ||
                startDate?.month != datePicker.month ||
                startDate?.date != datePicker.dayOfMonth
            ) {

                val bundle = Bundle().apply {
                    putInt(RESULT_YEAR, datePicker.year)
                    putInt(RESULT_MONTH, datePicker.month + 1)
                    putInt(RESULT_DATE, datePicker.dayOfMonth)
                }

                parentFragmentManager.setFragmentResult(REQUEST_START_DATES, bundle)
            }

            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "DateDialogFragment"

        const val RESULT_YEAR = "year"
        const val RESULT_MONTH = "month"
        const val RESULT_DATE = "date"

        fun newInstance(): DateDialogFragment {
            return DateDialogFragment()
        }
    }
}
