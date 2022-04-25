package com.eryuksa.growing.todo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.growing.databinding.FragmentTodoBinding

class TodoFragment : Fragment() {

    lateinit var binding: FragmentTodoBinding
    private val todoViewModel: TodoViewModel by lazy {
        ViewModelProvider(this)[TodoViewModel::class.java]
    }
    private val todoAdapter: TodoAdapter by lazy { TodoAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoBinding.inflate(inflater, container, false)
        binding.apply {
            viewModel = todoViewModel
            todoList.adapter = todoAdapter
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

        todoViewModel.listForAdapter.observe(viewLifecycleOwner) {
            todoAdapter.submitList(it)
        }
    }

    companion object {
        fun newInstance(): TodoFragment {
            return TodoFragment()
        }
    }
}