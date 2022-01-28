package com.eryuksa.growing.fragment.miracle_morning.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eryuksa.growing.R

class MiracleMorningFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_miracle_morning, container, false)
        return view
    }

    companion object {
        fun newInstance(): MiracleMorningFragment {
            return MiracleMorningFragment()
        }
    }
}