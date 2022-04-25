package com.eryuksa.growing.motivation

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.growing.R
import com.eryuksa.growing.motivation.data.YoutubeCategory
import com.eryuksa.growing.motivation.data.YoutubeItem
import com.eryuksa.growing.motivation.data.YoutubeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class YoutubeListViewModel : ViewModel() {

    private val repository = YoutubeRepository.get()

    private var currentList = mutableListOf<YoutubeItem>()
    val currentListSize get() = currentList.size
    var prevListSize = 0 // 변경되기 전의 리스트 사이즈

    private var currentCategory = YoutubeCategory.ALL
    private var lastLoadedItem: YoutubeItem? = null // DB의 cursor를 위한 변수. 리포지토리로 옮기는 게 더 좋을 것 같다는 생각이 든다

    // 리스트가 변경됐을 때 데이터가 추가된 것인지, 카테고리가 변경된 것인지 View에서 구분하기 위함
    // 이 부분도 ViewModel에서 다 처리할 수 있으면 더 좋을 것 같다.
    private var _categoryChanged = false
    val categoryChanged get() = _categoryChanged

    private lateinit var loadingJob: Job // 리포지토리에 데이터 요청하는 코루틴
    private var isLoading = false

    private val _youtubeList: MutableLiveData<List<YoutubeItem>> = MutableLiveData(emptyList())
    val youtubeList: LiveData<List<YoutubeItem>>
        get() = _youtubeList

    init {
        loadMoreItems()
    }

    /**
     * 리스트 아이템을 (더) 가져온다
     * 빠르게 스크롤 할 때를 대비해서 isLoading 변수 사용
     */
    fun loadMoreItems() {
        if (isLoading) return

        isLoading = true
        prevListSize = currentList.size // Fragment에서 참조 -> 추가된 데이터 만큼만 notify

        loadingJob = viewModelScope.launch {
            currentList.addAll(repository.getItems(currentCategory, lastLoadedItem))
            lastLoadedItem = currentList.last()
            _youtubeList.value = currentList
            isLoading = false
        }
    }

    /**
     * 이전에 요청했던 데이터 로딩 작업 중단하는 함수
     * 여러 chip을 빠르게 클릭할 때를 대비한다.
     */
    private fun stopLoadingItems() {
        loadingJob.cancel()
        isLoading = false
    }

    private fun changeCategory(category: YoutubeCategory) {
        currentList.clear()
        _categoryChanged = true // 프래그먼트에서 참조 -> 카테고리가 바뀌었을 땐 전체 번경: notifyDataSetChanged()
        _youtubeList.value = currentList // 빈 리스트 리사이클러 뷰에 반영
        lastLoadedItem = null // 첫 번째 아이템부터 필요하다는 의미 -> 리포지토리에서 Firestore cursor를 첫번째 아이템으로 세팅
        currentCategory = category
        _categoryChanged = false
    }

    fun onChipCheckedChanged(@IdRes checkedId: Int) {
        if (isLoading) {
            stopLoadingItems()
        }

        val category = getCategory(checkedId)
        changeCategory(category)
        loadMoreItems()
    }

    private fun getCategory(@IdRes chipId: Int): YoutubeCategory {
        return when (chipId) {
            R.id.chip_all -> YoutubeCategory.ALL
            R.id.chip_athlete -> YoutubeCategory.ATHLETE
            R.id.chip_believe -> YoutubeCategory.BELIEVE_IN
            R.id.chip_bitter -> YoutubeCategory.BITTER_WORDS
            R.id.chip_challenge -> YoutubeCategory.CHALLENGE
            R.id.chip_change -> YoutubeCategory.CHANGE
            R.id.chip_confidence -> YoutubeCategory.CONFIDENCE
            R.id.chip_consolation -> YoutubeCategory.CONSOLATION
            R.id.chip_entrepreneur -> YoutubeCategory.ENTREPRENEUR
            R.id.chip_famous_line -> YoutubeCategory.FAMOUS_LINE
            R.id.chip_give_up -> YoutubeCategory.GIVE_UP
            R.id.chip_passion -> YoutubeCategory.PASSION
            R.id.chip_rich -> YoutubeCategory.RICH
            R.id.chip_self_esteem -> YoutubeCategory.SELF_ESTEEM
            R.id.chip_study -> YoutubeCategory.STUDY
            R.id.chip_success -> YoutubeCategory.SUCCESS
            else -> throw IllegalArgumentException("not valid chipId")
        }
    }
}
