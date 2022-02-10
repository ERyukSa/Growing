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

        initBinding(savedInstanceState)
        observeFragmentType()
    }

    private fun initBinding(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        // 앱에 처음 진입할 때 보여줄 화면
        // 액티비티가 재생성 됐을 때는 동작x
        if (savedInstanceState == null) {
            binding.bottomView.selectedItemId = R.id.menu_miracle_morning
        }
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

        // 앱을 실행하고 선택한 화면을 처음 보여줄 때
        if (fragment == null) {
            fragment = when (fragmentType) {
                MainFragmentType.TOGETHER -> TogetherFragment.newInstance()
                MainFragmentType.MOTIVATION -> MotivationFragment.newInstance()
                MainFragmentType.MIRACLE_MORNING -> MiracleMorningFragment.newInstance()
            }

            // 프래그먼트 인스턴스를 생성해서 fm의 프래그먼트 리스트에 추가한다
            // replace를 하면 그전에 add 되었던 프래그먼트가 삭제된다
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
            .filterNot { it == fragmentType } // fragmentType이 아닌 type들로 필터링
            .forEach {
                val toHideFragment = supportFragmentManager.findFragmentByTag(it.name)
                toHideFragment?.let {
                    transaction.hide(toHideFragment)
                }
            }
    }
}