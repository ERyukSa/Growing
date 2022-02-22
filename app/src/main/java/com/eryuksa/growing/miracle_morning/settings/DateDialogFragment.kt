package com.eryuksa.growing.miracle_morning.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.eryuksa.growing.R
import com.eryuksa.growing.config.GrowingApplication
import org.joda.time.DateTime
import java.util.*

class DateDialogFragment : DialogFragment() {

    private lateinit var datePicker: DatePicker
    private lateinit var buttonOk: Button
    private lateinit var buttonCancel: Button

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 처음 보여줄 날짜 - 세팅되어 있으면 그 날을, 처음이면 오늘을
        GrowingApplication.startDateInMillis?.let {
            calendar.timeInMillis = it
        }
    }

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
        setUpDatePicker()

        buttonOk = view.findViewById(R.id.button_ok)
        buttonCancel = view.findViewById(R.id.button_cancel)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonOk.setOnClickListener {
            sendChangedDate() // 이전과 달라졌을 때만 결과 전달
            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setUpDatePicker() {
        datePicker.updateDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DATE)
        )
        removePickerHeader() // date picker header 삭제
    }

    private fun removePickerHeader() {
        val pickerHeaderId = datePicker.getChildAt(0)
            .resources.getIdentifier("date_picker_header", "id", "android")
        datePicker.findViewById<View>(pickerHeaderId).visibility = View.GONE
    }

    /**
     * SettingDialogFragment에 변경된 시작 날짜를 전달,
     * 이전과 바뀌었을 때만 전달한다
     */
    private fun sendChangedDate(){
        val timeInMillis = with(datePicker) {
            DateTime(year, month+1, dayOfMonth, 0, 0).millis
        }

        // 변경됐다면
        if (GrowingApplication.startDateInMillis != timeInMillis) {
            val bundle = bundleOf(RESULT_MILLIS to timeInMillis)
            parentFragmentManager.setFragmentResult(REQUEST_START_DATES, bundle)
        }
    }

    companion object {
        const val TAG = "DateDialogFragment"

        const val RESULT_MILLIS = "millis"

        fun newInstance(): DateDialogFragment {
            return DateDialogFragment()
        }
    }
}
