package com.eryuksa.growing.fragment.miracle_morning.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.eryuksa.growing.R

class GoalDialogFragment : DialogFragment() {

    private lateinit var timePicker: TimePicker
    private lateinit var buttonOk: Button
    private lateinit var buttonCancel: Button

    private var mMinuteOfDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        timePicker = view.findViewById(R.id.time_picker)
        buttonOk = view.findViewById(R.id.button_ok)
        buttonCancel = view.findViewById(R.id.button_cancel)

        buttonOk.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(RESULT_MINUTE, timePicker.hour * 60 + timePicker.minute) // 분
            }

            parentFragmentManager.setFragmentResult(REQUEST_GOAL_MINUTE, bundle)
            dismiss()
        }

        return view
    }

    companion object {
        const val TAG = "GoalDialogFragment"

        const val RESULT_MINUTE = "minuteOfDay"

        fun newInstance(): GoalDialogFragment {
            return GoalDialogFragment()
        }
    }
}
