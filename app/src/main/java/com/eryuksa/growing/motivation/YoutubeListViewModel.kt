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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class YoutubeListViewModel : ViewModel() {

    private val repository = YoutubeRepository.get()

    private var currentList = mutableListOf<YoutubeItem>()
    private var currentCategory = YoutubeCategory.ALL
    private var lastLoadedItem: YoutubeItem? = null

    private var _categoryChanged = false
    val categoryChanged get() = _categoryChanged

    private var isLoading = false
    var prevListSize = 0

    private val _youtubeList: MutableLiveData<List<YoutubeItem>> = MutableLiveData(emptyList())
    val youtubeList: LiveData<List<YoutubeItem>>
        get() = _youtubeList

    init{
        loadMoreItems()
    }

    fun loadMoreItems() {
        if (isLoading) return

        isLoading = true
        prevListSize = currentList.size

        CoroutineScope(viewModelScope.coroutineContext).launch {
            currentList.addAll(repository.getItems(currentCategory, lastLoadedItem))
            lastLoadedItem = currentList.last()
            _youtubeList.value = currentList
            isLoading = false
        }
    }

    private fun changeCategory(category: YoutubeCategory) {
        currentList.clear()
        _categoryChanged = true
        _youtubeList.value = currentList
        lastLoadedItem = null
        currentCategory = category
        _categoryChanged = false
    }

    fun onChipCheckedChanged(@IdRes checkedId: Int) {
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
