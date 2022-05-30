package com.eryuksa.growing.todo.add

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.DialogAddTodoBinding
import com.eryuksa.growing.todo.TodoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddTodoDialog : DialogFragment() {

    private lateinit var binding: DialogAddTodoBinding
    private val sharedViewModel: TodoViewModel by activityViewModels() // TodoFragment와 공유
    private val dialogViewModel: AddTodoViewModel by lazy {
        ViewModelProvider(this)[AddTodoViewModel::class.java]
    }

    private var percentOfPivotX = 0.0
    private var insetBtm = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let{
            percentOfPivotX = it.getDouble(PERCENT_PIVOT_X, 0.87)
            insetBtm = it.getInt(INSET_BTM, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddTodoBinding.inflate(inflater, container, false)
        setUpBinding()
        // 키보드가 올라오면 window의 bottomInset을 없애서 키보드 위에 붙인다
        lifecycleScope.launch {
            delay(600)
            showInputMethod()
            delay(300)
            removeInsetBtmOfWindow()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpDialogWindow()

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            requireDialog().window?.setDecorFitsSystemWindows(false)
            requireDialog().window?.decorView?.setOnApplyWindowInsetsListener { _, windowInsets ->
                val imeHeight = windowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                val statusHeight = windowInsets.getInsets(WindowInsets.Type.systemBars()).top
                view.setPadding(0, statusHeight, 0, imeHeight)
                windowInsets
            }
        } else {
            requireDialog().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }*/
    }

    private fun setUpBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.sharedViewModel = sharedViewModel
        binding.dialogViewModel = dialogViewModel
        binding.variableEditTextTodo = binding.editText
    }


    private fun showInputMethod(){
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(binding.editText, InputMethodManager.SHOW_FORCED)
    }

    /**
     * // 키보드 up -> insetBottom을 제거해서 키보드와 붙인다
     */
    private fun removeInsetBtmOfWindow() {
        val colorDrawable = ColorDrawable(Color.TRANSPARENT)
        val insetDrawable = InsetDrawable(colorDrawable, 0, 0, 0, 0)
        requireDialog().window?.setBackgroundDrawable(insetDrawable)
    }

    /**
     * 다이얼로그 윈도우 애니메이션, 크기, 위치 설정
     */
    private fun setUpDialogWindow() {
        setWindowAnimation()

        requireDialog().window?.apply {
            // 다이얼로그 윈도우 크기와 gravity 설정
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.BOTTOM)

            // 다이얼로그 윈도우의 bottom을 fab 중앙에 맞춘다
            val colorDrawable = ColorDrawable(Color.TRANSPARENT)
            val insetDrawable = InsetDrawable(colorDrawable, 0, 0, 0, insetBtm)
            setBackgroundDrawable(insetDrawable)
        }
    }

    /**
     * 다이얼로그가 생성되는 시작점의 x좌표(pivotX)를 fab의 중앙에 맞추기 위해
     * fab 중심의 x좌표와 screen width 비율에 맞는 애니메이션을 설정한다
     */
    private fun setWindowAnimation() {
        requireDialog().window?.apply{
            when(percentOfPivotX) {
                0.8 -> setWindowAnimations(R.style.addTodoDialogAnimation80)
                0.81 -> setWindowAnimations(R.style.addTodoDialogAnimation81)
                0.82 -> setWindowAnimations(R.style.addTodoDialogAnimation82)
                0.83 -> setWindowAnimations(R.style.addTodoDialogAnimation83)
                0.84 -> setWindowAnimations(R.style.addTodoDialogAnimation84)
                0.85 -> setWindowAnimations(R.style.addTodoDialogAnimation85)
                0.86 -> setWindowAnimations(R.style.addTodoDialogAnimation86)
                0.87 -> setWindowAnimations(R.style.addTodoDialogAnimation87)
                0.88 -> setWindowAnimations(R.style.addTodoDialogAnimation88)
                0.89 -> setWindowAnimations(R.style.addTodoDialogAnimation89)
                0.90 -> setWindowAnimations(R.style.addTodoDialogAnimation90)
                0.91 -> setWindowAnimations(R.style.addTodoDialogAnimation91)
                else -> setWindowAnimations(R.style.addTodoDialogAnimation92)
            }
        }
    }

    companion object {
        const val TAG = "ADD_TODO"

        private const val PERCENT_PIVOT_X = "PPX"
        private const val INSET_BTM = "IB"

        fun newInstance(percentOfPivotX: Double, insetBtm: Int): AddTodoDialog {
            val bundle = Bundle().apply {
                putDouble(PERCENT_PIVOT_X, percentOfPivotX)
                putInt(INSET_BTM, insetBtm)
            }

            return AddTodoDialog().apply {
                arguments = bundle
            }
        }
    }
}