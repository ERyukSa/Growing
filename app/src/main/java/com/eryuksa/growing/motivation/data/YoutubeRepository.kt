package com.eryuksa.growing.motivation.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.tasks.await

private const val TAG = "YoutubeRepository"

class YoutubeRepository private constructor() {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * 지정된 카테고리 아이템을 cursor 다음 위치부터 LIMIT_SIZE만큼 가져온다
     */
    suspend fun getItems(category: YoutubeCategory, lastItem: YoutubeItem?): List<YoutubeItem> {
        val query = buildQuery(category, lastItem)
        val resultList = mutableListOf<YoutubeItem>()

        query.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "$document")
                    resultList.add(createYoutubeItem(document))
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "loading youtube list failed: ${it.message}")
            }
            .await()

        return resultList
    }

    /**
     * cursor 다음 위치 ~ LIMIT_SIZE개 만큼 가져오는 Query 반환
     */
    private fun buildQuery(category: YoutubeCategory, lastLoadedItem: YoutubeItem?): Query {
        val query = if (category == YoutubeCategory.ALL) {
            fireStore.collection(YOUTUBE).orderBy(FIELD_VIDEO_ID).limit(LIMIT_SIZE.toLong())
        } else {
            fireStore.collection(YOUTUBE).whereArrayContains(FIELD_CATEGORY, category.name)
                .orderBy(FIELD_VIDEO_ID).limit(LIMIT_SIZE.toLong())
        }

        return if (lastLoadedItem == null) query
        else query.startAfter(lastLoadedItem.videoId)
    }

    /**
     * YoutubeItem 인스턴스 생성
     */
    private fun createYoutubeItem(document: DocumentSnapshot): YoutubeItem {
        val youtubeItem = document.toObject(YoutubeItem::class.java)!!

        // string으로 저장되어 있던 카테고리를 YoutubeCategory enum class로 변환
        for (str in document[FIELD_CATEGORY] as List<String>) {
            youtubeItem.categories.add(YoutubeCategory.valueOf(str))
        }

        return youtubeItem
    }

    fun onDestroy() {
        coroutineScope.cancel()
    }

    companion object {
        const val LIMIT_SIZE = 12

        private const val YOUTUBE = "youtube"
        private const val FIELD_CATEGORY = "category"
        private const val FIELD_VIDEO_ID = "videoId"

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