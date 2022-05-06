package com.eryuksa.growing.todo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.FragmentTodoBinding
import com.eryuksa.growing.todo.add.AddTodoDialog
import kotlin.math.round

class TodoFragment : Fragment() {

    private lateinit var binding: FragmentTodoBinding
    private val todoViewModel: TodoViewModel by activityViewModels() // AddTodoDialog와 공유

    private val todoAdapter: TodoAdapter by lazy { TodoAdapter(todoViewModel, viewLifecycleOwner) }

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

        val scaleX = ObjectAnimator.ofFloat(binding.imgQuotation,"scaleX", 0.8f)
        scaleX.repeatCount = ValueAnimator.INFINITE
        scaleX.repeatMode = ValueAnimator.REVERSE
        scaleX.duration = 2000
        scaleX.start()
        /*val scaleY = ObjectAnimator.ofFloat(binding.imgQuotation,"scaleY", 0.8f)
        scaleY.repeatCount = ValueAnimator.INFINITE
        scaleY.repeatMode = ValueAnimator.REVERSE
        scaleY.duration = 1000*/
        val animSet = AnimatorSet()
        animSet.playTogether(scaleX)
        animSet.start()

        todoViewModel.listForUi.observe(viewLifecycleOwner) {
            todoAdapter.submitList(it)
        }

        binding.floatingActionButton.setOnClickListener { fab ->
            val btmNavView = requireActivity().findViewById<View>(R.id.bottom_view)
            val fabLocation = intArrayOf(0, 0).also { fab.getLocationOnScreen(it)} // fab의 절대 좌표 (x,y)
            val fabCenterX = fabLocation[0] + fab.width / 2 // fab의 중심 좌표
            val fabCenterY = fab.top + fab.height / 2

            // 다이얼로그를 fab의 중심에서부터 생겨나도록 하기 위한 변수들
            /* 중심의 X좌표 % 스크린 전체 너비 -> 다이얼로그 애니메이션의 pivotX로 사용
               (round() * 10^k) / 10^k -> 소수점 k+1번째 자리 수에서 반올림 */
            val percentOfCenterX = round(fabCenterX.toDouble() / btmNavView.width) * 100 / 100
            val insetBtmOfDialog = btmNavView.bottom - fabCenterY // 다이얼로그에 fabCenterY 만큼 마진을 준다
            val dialog = AddTodoDialog.newInstance(percentOfCenterX, insetBtmOfDialog)
            dialog.show(parentFragmentManager, AddTodoDialog.TAG)
        }
    }

    companion object {
        fun newInstance(): TodoFragment {
            return TodoFragment()
        }
    }
}