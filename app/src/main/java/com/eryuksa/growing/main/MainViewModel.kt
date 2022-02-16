package com.eryuksa.growing.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eryuksa.growing.R

private const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {

    private val _currentFragmentType = MutableLiveData(MainFragmentType.MIRACLE_MORNING)

    val currentFragmentType: LiveData<MainFragmentType>
        get() = _currentFragmentType

    // 현재 보여줄 프래그먼트 타입 변경
    fun setFragmentType(menuItemId: Int): Boolean {
        return try{
            _currentFragmentType.value = getFragmentType(menuItemId)
            true
        } catch (e: java.lang.IllegalArgumentException) {
            Log.e(TAG, e.message ?: "error occurred in setFragmentType()")
            false
        }
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
}