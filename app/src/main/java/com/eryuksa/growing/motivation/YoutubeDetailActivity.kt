package com.eryuksa.growing.motivation

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.eryuksa.growing.R
import com.eryuksa.growing.databinding.ActivityYoutubeDetailBinding
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer

class YoutubeDetailActivity : AppCompatActivity() {

    private val binding: ActivityYoutubeDetailBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_youtube_detail)
    }
    private var youtubePlayer: YouTubePlayer? = null
    private var isPlayerFullScreen = false

    private var videoId: String? = null
    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractExtras()
        setUpPlayer()
        setUpTitle()
        setUpBackButton()
    }

    private fun extractExtras() {
        videoId = intent.getStringExtra(EXTRA_VIDEO_ID)
        title = intent.getStringExtra(EXTRA_TITLE)
    }

    private fun setUpPlayer() {
        if (videoId != null) {
            binding.youtubePlayerView.play(videoId!!)

            binding.youtubePlayerView.onInitializedListener =
                object : kr.co.prnd.YouTubePlayerView.OnInitializedListener {
                    override fun onInitializationFailure(
                        provider: YouTubePlayer.Provider,
                        result: YouTubeInitializationResult
                    ) {
                        Toast.makeText(
                            this@YoutubeDetailActivity,
                            "\"동영상 로딩 중 에러가 발생했습니다. 에러가 계속되면 문의 남겨주세요ㅠㅠ\"",
                            Toast.LENGTH_SHORT
                        ).show()

                        result.getErrorDialog(this@YoutubeDetailActivity, YouTubeInitializationResult.UNKNOWN_ERROR.ordinal)
                    }

                    override fun onInitializationSuccess(
                        provider: YouTubePlayer.Provider,
                        player: YouTubePlayer,
                        wasRestored: Boolean
                    ) {
                        youtubePlayer = player

                        // 전체화면 클릭/해제 했을 때 화면 방향 처리
                        player.setOnFullscreenListener {
                            requestedOrientation = if (it) {
                                ActivityInfo.SCREEN_ORIENTATION_LOCKED
                            } else {
                                ActivityInfo.SCREEN_ORIENTATION_USER
                            }

                            isPlayerFullScreen = it
                        }
                    }
                }
        }
    }

    private fun setUpTitle() {
        if (title != null) binding.textTitle.text = title
    }

    private fun setUpBackButton() {
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        // 전체 화면에서 백버튼 클릭 -> 전체 화면 해제
        if (isPlayerFullScreen && youtubePlayer != null) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
            youtubePlayer!!.setFullscreen(false)
        } else{
            super.onBackPressed()
        }
    }

    companion object {
        private const val EXTRA_VIDEO_ID = "extraVideoId"
        private const val EXTRA_TITLE = "extraTitle"

        fun newIntent(packageContext: Context, videoId: String, title: String): Intent {
            return Intent(packageContext, YoutubeDetailActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_ID, videoId)
                putExtra(EXTRA_TITLE, title)
            }
        }
    }
}