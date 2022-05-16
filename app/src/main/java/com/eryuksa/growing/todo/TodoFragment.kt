package com.eryuksa.growing.todo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.eryuksa.growing.databinding.FragmentTodoBinding
import com.eryuksa.growing.todo.add.AddTodoDialog
import kotlin.math.round

class TodoFragment : Fragment() {

    private lateinit var binding: FragmentTodoBinding
    private val todoViewModel: TodoViewModel by activityViewModels() // AddTodoDialog와 공유

    private val todoAdapter: TodoAdapter by lazy { TodoAdapter(todoViewModel, viewLifecycleOwner) }

    private var percentOfCenterX = 0.0
    private var insetBtmOfDialog = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = todoViewModel
        }
        binding.todoList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = todoAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calculateDialogInsetBtmAndPercentX()

        val scaleX = ObjectAnimator.ofFloat(binding.imgQuotation,"scaleX", 0.8f)
        scaleX.repeatCount = ValueAnimator.INFINITE
        scaleX.repeatMode = ValueAnimator.REVERSE
        scaleX.duration = 2000
        scaleX.start()

        val animSet = AnimatorSet()
        animSet.playTogether(scaleX)
        animSet.start()

        todoViewModel.tempListForUi.observe(viewLifecycleOwner) {
            todoAdapter.submitList(it)
        }
        /*todoViewModel.listForUi.observe(viewLifecycleOwner) {
            todoAdapter.submitList(it)
        }*/

        binding.floatingActionButton.setOnClickListener {
            val dialog = AddTodoDialog.newInstance(percentOfCenterX, insetBtmOfDialog)
            dialog.show(parentFragmentManager, AddTodoDialog.TAG)
        }
    }

    private fun calculateDialogInsetBtmAndPercentX() {
        /*var statusBarHeight = 0
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resId)
        }*/
        val fab = binding.floatingActionButton

        fab.doOnLayout {
            // status bar가 포함x
            val screenWidth = resources.displayMetrics.widthPixels
            val screenHeight = resources.displayMetrics.heightPixels
            // 근데 얘는 fab의 status bar가 포함된다;
            val fabLocation = intArrayOf(0, 0).also { fab.getLocationOnScreen(it) }
            val fabCenterX = fabLocation[0] + fab.width / 2 // fab의 중심 좌표
            val fabCenterY = fab.top + fab.height / 2

            // 다이얼로그를 fab의 중심에서부터 커지도록 하기 위한 변수들
            /* 중심의 X좌표 % 다이얼로그 너비 -> 다이얼로그 애니메이션의 pivotX로 사용
               (round() * 10^k) / 10^k -> 소수점 k+1번째 자리 수에서 반올림 */
            percentOfCenterX = round(fabCenterX.toDouble() / screenWidth * 100) / 100
            insetBtmOfDialog = screenHeight - fabCenterY // 다이얼로그에 fabCenterY 만큼 마진을 준다
            Log.d("로그", "$percentOfCenterX")
        }
    }

    companion object {
        fun newInstance(): TodoFragment {
            return TodoFragment()
        }
    }
}