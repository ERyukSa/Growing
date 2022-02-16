package com.eryuksa.growing.miracle_morning.calendar

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import com.eryuksa.growing.miracle_morning.data.CalendarRepository
import com.eryuksa.growing.miracle_morning.calendar.model.MiracleStamp
import com.eryuksa.growing.miracle_morning.stamp.StampDialogFragment
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class CalendarViewModel(_monthMillis: Long) : ViewModel(), DefaultLifecycleObserver {

    private val calendarRepository = CalendarRepository.get()

    val miracleCalendar: MiracleCalendar = MiracleCalendar(_monthMillis)
    private val dateList
        get() = miracleCalendar.dateList

    val monthMillis = _monthMillis
    val prevMonthMillis get() = miracleCalendar.preMonthMillis
    val nextMonthMillis get() = miracleCalendar.nextMonthMillis

    private val updatedStampList: MutableList<MiracleStamp> = mutableListOf()
    private val _updatedStamp = MutableLiveData<MiracleStamp>()
    val updatedStamp: LiveData<MiracleStamp>
        get() = _updatedStamp
    var isStampLoaded = MutableLiveData<Boolean>()

    init {
        // 기상 시간을 담고 있는 스탬프 객체들을 Room에서 가져온다
        viewModelScope.launch {
            loadStamps()
            isStampLoaded.value = true // 스탬프 로딩 완료
        }
    }

    private suspend fun loadStamps() {
        val preStartDay = miracleCalendar.dateList[0].dayOfMonth
        val preEndDay = preStartDay + miracleCalendar.prevMonthTailOffset - 1
        val nextEndDay = miracleCalendar.dateList.last().dayOfMonth

        val job1 = viewModelScope.launch {
            loadPrevMonthStamps(preStartDay, preEndDay)
        }

        val job2 = viewModelScope.launch {
            loadCurrentMonthStamps()
        }

        val job3 = viewModelScope.launch {
            loadNextMonthStamps(nextEndDay)
        }

        joinAll(job1, job2, job3)
    }

    private suspend fun loadPrevMonthStamps(startDay: Int, endDay: Int) {
        val preMonthStamps =
            calendarRepository.getStamps(prevMonthMillis, startDay, endDay)

        preMonthStamps.forEach { stamp ->
            Log.d("CalendarViewModel", "prevStamp: $stamp")
            val pos = stamp.dayOfMonth - dateList[0].dayOfMonth
            dateList[pos].wakeUpMinutes.value = stamp.wakeUpMinutes
        }
    }

    private suspend fun loadCurrentMonthStamps() {
        val stamps = calendarRepository.getMonthStamps(monthMillis)

        stamps.forEach { stamp ->
            Log.d("CalendarViewModel", "currentStamp: $stamp")
            val pos = miracleCalendar.prevMonthTailOffset + stamp.dayOfMonth - 1
            dateList[pos].wakeUpMinutes.value = stamp.wakeUpMinutes
            Log.d("CalendarViewModel", "pos: $pos, ${dateList[pos].wakeUpMinutes.value}")
        }
    }

    private suspend fun loadNextMonthStamps(endDay: Int) {
        val nextMonthStamps = calendarRepository.getStamps(nextMonthMillis, 1, endDay)

        nextMonthStamps.forEach { stamp ->
            Log.d("CalendarViewModel", "nextStamp: $stamp")
            val pos =
                (dateList.size - miracleCalendar.nextMonthHeadOffset) + stamp.dayOfMonth - 1
            dateList[pos].wakeUpMinutes.value = stamp.wakeUpMinutes
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        if (updatedStampList.isNotEmpty()) {
            val temp = mutableListOf<MiracleStamp>().apply { addAll(updatedStampList) }
            calendarRepository.updateStamp(temp)

            updatedStampList.clear()
        }

        super.onStop(owner)
    }

    fun onFragmentResult(requestKey: String, result: Bundle) {

        // 스탬프 다이얼로그에서 기상 시간이 기록됐을 때
        if (requestKey == REQUEST_TODAY_STAMP) {
            val monthMillis = result.getLong(StampDialogFragment.RESULT_MILLIS)
            val dayOfMonth = result.getInt(StampDialogFragment.RESULT_DATE)
            val minutesOfDay = result.getInt(StampDialogFragment.RESULT_MINUTES)

            // 캘린더 UI 업데이트
            _updatedStamp.value = MiracleStamp(monthMillis, dayOfMonth, minutesOfDay)

            // 업데이트된 리스트에 추가
            updatedStampList.add(MiracleStamp(monthMillis, dayOfMonth, minutesOfDay))
        }
    }

    class Factory(private val monthMillis: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalendarViewModel(monthMillis) as T
        }
    }
}