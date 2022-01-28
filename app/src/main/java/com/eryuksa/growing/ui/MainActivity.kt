package com.eryuksa.growing.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.growing.R
import com.eryuksa.growing.data.MainViewModel
import com.eryuksa.growing.data.MainViewModelFactory
import com.eryuksa.growing.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModelFactory(supportFragmentManager) // 프래그먼트 매니저를 인자로 전달
        )[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this // binding에 LiveData 사용
        binding.viewModel = viewModel
    }
}