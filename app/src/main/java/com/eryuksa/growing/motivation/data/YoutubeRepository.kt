package com.eryuksa.growing.motivation.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.tasks.await
import java.util.*

private const val TAG = "YoutubeRepository"

class YoutubeRepository private constructor() {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val youtubeMap: MutableMap<YoutubeCategory, List<YoutubeItem>> =
        EnumMap(YoutubeCategory::class.java)

    suspend fun getList(category: YoutubeCategory): List<YoutubeItem> {

        return if (youtubeMap.contains(category)) {
            youtubeMap[category]!!
        } else {
            val list = if (category == YoutubeCategory.ALL) {
                loadAllItems()
            } else {
                youtubeMap[YoutubeCategory.ALL]!!.filter { category in it.categories }
            }

            youtubeMap[category] = list
            list
        }
    }

    private suspend fun loadAllItems(): List<YoutubeItem> {
        val list = mutableListOf<YoutubeItem>()

        Log.d("Log", "3")

        fireStore.collection("youtube").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    list.add(createYoutubeItem(document))
                }
            }.addOnFailureListener {
                Log.d(TAG, "loading youtube list failed: ${it.message}")
            }.await()

        Log.d("Log", "4")

        return list
    }

    private fun createYoutubeItem(document: DocumentSnapshot): YoutubeItem {
        val youtubeItem = document.toObject(YoutubeItem::class.java)!!

        // string으로 저장되어 있던 카테고리를 YoutubeCategory enum class로 변환
        for (str in document["category"] as List<String>) {
            youtubeItem.categories.add(YoutubeCategory.valueOf(str))
        }

        return youtubeItem
    }

    fun onDestroy() {
        coroutineScope.cancel()
    }

    companion object {
        private var INSTANCE: YoutubeRepository? = null

        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = YoutubeRepository()
            }
        }

        fun get(): YoutubeRepository {
            return INSTANCE ?: throw IllegalStateException("YoutubeRepository must be initialized")
        }
    }
}