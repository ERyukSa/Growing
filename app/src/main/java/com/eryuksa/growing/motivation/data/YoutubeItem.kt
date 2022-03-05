package com.eryuksa.growing.motivation.data

data class YoutubeItem(
    val categories: MutableList<YoutubeCategory> = mutableListOf(),
    val title: String = "",
    val thumbnailUrl: String = "",
    val videoId: String = "",
    val duration: String = ""
)
