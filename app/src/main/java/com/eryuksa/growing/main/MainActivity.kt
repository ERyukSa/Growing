package com.eryuksa.growing.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.ActivityMainBinding
import com.eryuksa.growing.miracle_morning.MiracleMorningFragment
import com.eryuksa.growing.motivation.MotivationFragment
import com.eryuksa.growing.todo.TodoFragment

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel

        observeFragmentType()
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
                MainFragmentType.TODO -> TodoFragment.newInstance()
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