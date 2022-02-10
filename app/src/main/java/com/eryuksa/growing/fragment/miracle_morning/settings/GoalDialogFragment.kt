package com.eryuksa.growing.fragment.miracle_morning.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.eryuksa.growing.R
import com.eryuksa.growing.config.GrowingApplication

class GoalDialogFragment : DialogFragment() {

    private lateinit var timePicker: TimePicker
    private lateinit var buttonOk: Button
    private lateinit var buttonCancel: Button

    override fun onStart() {
        super.onStart()

        val layoutParams = requireDialog().window?.attributes
        layoutParams?.width = (resources.displayMetrics.widthPixels * 0.9).toInt()

        requireDialog().window?.attributes = layoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stamp_dialog, container, false)

        view.findViewById<TextView>(R.id.text_title).text = getString(R.string.goal_minute)

        // 목표시간이 설정되어 있으면 설정된 시간을, 안돼있으면 현재 시간을 보여준다
        timePicker = view.findViewById<TimePicker>(R.id.time_picker).apply {
            if (GrowingApplication.goalMinutes != null) {
                this.hour = GrowingApplication.goalMinutes!! / 60
                this.minute = GrowingApplication.goalMinutes!! % 60
            }
        }

        buttonOk = view.findViewById(R.id.button_ok)
        buttonCancel = view.findViewById(R.id.button_cancel)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 이전 설정과 달라졌을 때만 결과 전달
        buttonOk.setOnClickListener {
            val changedMinute = timePicker.hour * 60 + timePicker.minute

            if (changedMinute != GrowingApplication.goalMinutes) {
                val bundle = Bundle().apply {
                    putInt(RESULT_MINUTE, timePicker.hour * 60 + timePicker.minute) // 분
                }

                parentFragmentManager.setFragmentResult(REQUEST_GOAL_MINUTE, bundle)
            }

            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "GoalDialogFragment"

        const val RESULT_MINUTE = "minuteOfDay"

        fun newInstance(): GoalDialogFragment {
            return GoalDialogFragment()
        }
    }
}
