package com.eryuksa.growing.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.growing.R
import com.eryuksa.growing.data.MainFragmentType
import com.eryuksa.growing.data.MainViewModel
import com.eryuksa.growing.databinding.ActivityMainBinding
import com.eryuksa.growing.fragment.miracle_morning.ui.MiracleMorningFragment
import com.eryuksa.growing.fragment.motivation.MotivationFragment
import com.eryuksa.growing.fragment.together.TogetherFragment

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        observeFragmentType()
    }

    private fun initBinding() {
        binding.viewModel = viewModel
    }

    // 프래그먼트 타입이 변경되면, 화면에 보여줄 프래그먼트를 변경한다
    private fun observeFragmentType() {
        viewModel.currentFragmentType.observe(this) {
            setFragment(it)
        }

    }

    // 선택된 프래그먼트로 변경
    private fun setFragment(fragmentType: MainFragmentType) {
        var fragment = supportFragmentManager.findFragmentByTag(fragmentType.name)
        val transaction = supportFragmentManager.beginTransaction()

        // 앱에 처음 진입하는 경우
        if (fragment == null) {
            fragment = when (fragmentType) {
                MainFragmentType.TOGETHER -> TogetherFragment.newInstance()
                MainFragmentType.MOTIVATION -> MotivationFragment.newInstance()
                MainFragmentType.MIRACLE_MORNING -> MiracleMorningFragment.newInstance()
            }

            // 프래그먼트 인스턴스를 처음 생성해서, FM의 프래그먼트 리스트에 추가한다
            transaction.add(R.id.fragment_container, fragment, fragmentType.name)
        }

        transaction.show(fragment)
        hideElseFragments(fragmentType, transaction) // 나머지 프래그먼트들을 숨긴다
        transaction.commit()
    }

    // 선택되지 않은 프래그먼트들을 숨긴다
    private fun hideElseFragments(
        fragmentType: MainFragmentType,
        transaction: FragmentTransaction
    ) {
        MainFragmentType.values()
            .filterNot { it == fragmentType }
            .forEach {
                val toHideFragment = supportFragmentManager.findFragmentByTag(it.name)
                toHideFragment?.let {
                    transaction.hide(toHideFragment)
                }
            }
    }
}