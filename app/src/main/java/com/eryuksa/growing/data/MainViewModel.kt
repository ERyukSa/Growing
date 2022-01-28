package com.eryuksa.growing.data

import android.view.MenuItem
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.growing.R
import com.eryuksa.growing.fragment.miracle_morning.ui.MiracleMorningFragment
import com.eryuksa.growing.fragment.motivation.MotivationFragment
import com.eryuksa.growing.fragment.together.TogetherFragment

class MainViewModel(private val fm: FragmentManager) : ViewModel() {

    // 바텀 네비게이션 뷰가 클릭되면, 데이터 바인딩으로 동작
    fun setFragment(menuItem: MenuItem) {
        val fragmentType = getFragmentType(menuItem.itemId)
        var fragment = fm.findFragmentByTag(fragmentType.name)
        val transaction = fm.beginTransaction()

        // 앱에 처음 진입하는 경우
        if (fragment == null) {
            fragment = when (fragmentType) {
                MainFragmentType.TOGETHER -> TogetherFragment.newInstance()
                MainFragmentType.MOTIVATION -> MotivationFragment.newInstance()
                MainFragmentType.MIRACLE_MORNING -> MiracleMorningFragment.newInstance()
            }
        }

        transaction
            .add(R.id.fragment_container, fragment, fragmentType.name)
            .show(fragment)
        hideElseFragments(fragmentType, transaction) // 나머지 프래그먼트들을 숨긴다
        transaction.commit()
    }

    // 바텀 메뉴 아이템에 맞게 프래그먼트 타입을 가져온다
    private fun getFragmentType(itemId: Int): MainFragmentType {
        return when (itemId) {
            R.id.menu_together -> MainFragmentType.TOGETHER
            R.id.menu_motivation -> MainFragmentType.MOTIVATION
            R.id.menu_miracle_morning -> MainFragmentType.MIRACLE_MORNING
            else -> throw IllegalArgumentException("not found main fragment Id")
        }
    }

    // 선택되지 않은 프래그먼트들을 숨긴다
    private fun hideElseFragments(fragmentType: MainFragmentType, transaction: FragmentTransaction) {
        MainFragmentType.values()
            .filterNot { it == fragmentType }
            .forEach {
                val toHideFragment = fm.findFragmentByTag(it.name)
                toHideFragment?.let {
                    transaction.hide(toHideFragment)
                }
            }
    }
}

class MainViewModelFactory(private val fm: FragmentManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(fm) as T
        } else {
            throw IllegalArgumentException()
        }
    }

}