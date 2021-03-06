package com.eryuksa.growing.miracle_morning.calendar

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import com.eryuksa.growing.miracle_morning.calendar.data.BaseCalendar
import com.eryuksa.growing.miracle_morning.calendar.data.CalendarRepository
import com.eryuksa.growing.miracle_morning.model.MiracleDate
import com.eryuksa.growing.miracle_morning.model.MiracleStamp
import com.eryuksa.growing.miracle_morning.stamp.StampDialogFragment
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import kotlin.math.ceil

class CalendarViewModel(_monthMillis: Long) : ViewModel(), DefaultLifecycleObserver {

    private val calendarRepository = CalendarRepository.get()

    private val baseCalendar: BaseCalendar = BaseCalendar(_monthMillis)

    val miracleDateList = mutableListOf<MiracleDate>()
    private val dateTimeList
        get() = baseCalendar.dateTimeList

    val currentDateTime get()= baseCalendar.currentDateTime
    private val prevDateTime get() = baseCalendar.prevDateTime
    private val nextDateTime get() = baseCalendar.nextDateTime

    private val updatedStampList: MutableList<MiracleStamp> = mutableListOf()

    private val _updatedPos = MutableLiveData<Int>()
    val updatedPos: LiveData<Int>
        get() = _updatedPos

    private var _isStampLoaded = MutableLiveData<Boolean>()
    val isStampLoaded: LiveData<Boolean>
        get() = _isStampLoaded

    private var _selectedDatePos = MutableLiveData<Int>(initSelectedPos())
    val selectedDatePos: LiveData<Int> get() = _selectedDatePos

    private var _prevSelectedPos = MutableLiveData<Int>()
    val prevSelectedPos get() = _prevSelectedPos

    init {
        setUpMiracleDateList()
    }

    /**
     * Adapter에서 사용할 MiracleDate 리스트 준비
     * 1. BaseCalendar에서 생성한 DateTime 객체를 넣는다
     * 2. 리포지토리에서 스탬프 객체를 가져와서 일어난 시간을 넣는다
     */
    private fun setUpMiracleDateList() {
        dateTimeList.forEach {
            miracleDateList.add(MiracleDate(it))
        }

        // 일어난 시간을 담고 있는 스탬프 객체들을 Room에서 가져온다
        viewModelScope.launch {
            loadStamps()
            _isStampLoaded.value = true // 스탬프 로딩 완료
        }
    }

    private suspend fun loadStamps() {
        // 첫 주, 끝 주에 보여줄 이전, 다음 달 날짜 범위
        val prevStartDay = baseCalendar.dateTimeList[0].dayOfMonth // 이전 달 시작 날짜
        val prevEndDay = prevStartDay + baseCalendar.prevMonthTailOffset - 1 // 이전 달 끝 날짜
        val nextEndDay = baseCalendar.dateTimeList.last().dayOfMonth // 다음 달 끝 날짜

        // 코루틴스코프로 하위 코루틴이 모두 끝나야 suspend 함수가 종료되도록 함
        coroutineScope {
            launch { loadPrevMonthTailStamps(prevStartDay, prevEndDay) }
            launch { loadCurrentMonthStamps() }
            launch { loadNextMonthHeadStamps(nextEndDay) }
        }
    }

    private suspend fun loadPrevMonthTailStamps(startDay: Int, endDay: Int) {
        val preMonthStamps =
            calendarRepository.getStamps(prevDateTime.millis, startDay, endDay)

        preMonthStamps.forEach { stamp ->
            val pos = stamp.dayOfMonth - dateTimeList[0].dayOfMonth
            miracleDateList[pos].wakeUpMinutes.value = stamp.wakeUpMinutes
        }
    }

    private suspend fun loadCurrentMonthStamps() {
        val stamps = calendarRepository.getMonthStamps(currentDateTime.millis)

        stamps.forEach { stamp ->
            val pos = baseCalendar.prevMonthTailOffset + stamp.dayOfMonth - 1
            miracleDateList[pos].wakeUpMinutes.value = stamp.wakeUpMinutes
        }
    }

    private suspend fun loadNextMonthHeadStamps(endDay: Int) {
        val nextMonthStamps = calendarRepository.getStamps(nextDateTime.millis, 1, endDay)

        nextMonthStamps.forEach { stamp ->
            val pos =
                (dateTimeList.size - baseCalendar.nextMonthHeadOffset) + stamp.dayOfMonth - 1
            miracleDateList[pos].wakeUpMinutes.value = stamp.wakeUpMinutes
        }
    }

    /**
     * 캘린더의 라인 개수에 따라 아이템 뷰 높이 설정
     */
   fun setItemViewHeight(itemView: View, parent: ViewGroup) {
        val lengthOfRow = ceil(dateTimeList.size.toDouble() / BaseCalendar.DAYS_OF_WEEK).toInt()
        itemView.layoutParams.height = parent.height / lengthOfRow
    }

    /**
     * 이번 달이면 오늘 날짜를, 아니면 1일의 DateHolder를 select
     */
    private fun initSelectedPos(): Int {
        return if (baseCalendar.currentDateTime.monthOfYear == todayDateTime.monthOfYear) {
            baseCalendar.prevMonthTailOffset + todayDateTime.dayOfMonth - 1
        } else {
            baseCalendar.prevMonthTailOffset
        }
    }

    /**
     * 선택된 DateHolder의 position 변경
     */
    fun changeSelectedPos(newPos: Int) {
        _prevSelectedPos.value = _selectedDatePos.value // 이전에 선택된 아이템뷰의 selected 해제
        // 현재 포지션의 아이템뷰로 변경
        // notifyItemChanged()를 처리하는데 시간이 걸리기 때문에 동작하는 것 같다.
        _selectedDatePos.value = newPos
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
            val minutesOfDay = result.getInt(StampDialogFragment.RESULT_MINUTES)

            // 캘린더 UI 업데이트
            updateItemUI(minutesOfDay)

            // 업데이트된 리스트에 추가
            selectedDatePos.value?.let {
                val monthMillis = dateTimeList[it].withDayOfMonth(1).millis
                val dayOfMonth = dateTimeList[it].dayOfMonth
                updatedStampList.add(MiracleStamp(monthMillis, dayOfMonth, minutesOfDay))
            }
        }
    }

    /**
     * 변경된 스탬프의 DateHolder UI 업데이트
     */
    private fun updateItemUI(wakeUpMinutes: Int) {
        selectedDatePos.value?.let {
            miracleDateList[it].wakeUpMinutes.value = wakeUpMinutes
        }

        _updatedPos.value = selectedDatePos.value
    }

    class Factory(private val monthMillis: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalendarViewModel(monthMillis) as T
        }
    }

    companion object {
        val todayDateTime = DateTime()
    }
}