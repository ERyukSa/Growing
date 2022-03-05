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

    private val _youtubeList: MutableLiveData<List<YoutubeItem>> = MutableLiveData(emptyList())
    val youtubeList: LiveData<List<YoutubeItem>>
        get() = _youtubeList

    init{
        changeList(YoutubeCategory.ALL)
    }

    fun onChipCheckedChanged(@IdRes checkedId: Int) {
        val category = getCategory(checkedId)
        changeList(category)
    }

    private fun changeList(category: YoutubeCategory) {
        CoroutineScope(viewModelScope.coroutineContext).launch {
            _youtubeList.value = repository.getList(category)
        }
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
