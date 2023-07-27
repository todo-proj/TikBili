package com.benyq.tikbili.ui.video

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.benyq.tikbili.R
import com.benyq.tikbili.bilibili.model.RecommendVideoModel
import com.benyq.tikbili.databinding.FragmentVideoPlayBinding
import com.benyq.tikbili.ext.fullScreen
import com.benyq.tikbili.ext.gone
import com.benyq.tikbili.ext.visible
import com.benyq.tikbili.ext.visibleOrGone
import com.benyq.tikbili.player.MediaCacheFactory
import com.benyq.tikbili.ui.LifeCycleLogObserver
import com.benyq.tikbili.ui.base.BaseFragment
import com.benyq.tikbili.ui.base.mvi.extension.collectSingleEvent
import com.benyq.tikbili.ui.base.mvi.extension.collectState
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.video.VideoSize

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
class FragmentVideoPlay :
    BaseFragment<FragmentVideoPlayBinding>(R.layout.fragment_video_play) {

    companion object {
        private const val EXTRA_VIDEO = "extraVideo"
        fun newInstance(recommendVideoModel: RecommendVideoModel): FragmentVideoPlay {
            return FragmentVideoPlay().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_VIDEO, recommendVideoModel)
                }
            }
        }
    }

    private val viewModel by viewModels<VideoPlayViewModel>()
    private lateinit var player: SimpleExoPlayer

    private lateinit var recommendVideoModel: RecommendVideoModel

    private var commentsAdapter = CommentsAdapter()

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycle.addObserver(LifeCycleLogObserver())
        recommendVideoModel = requireArguments().getParcelable(EXTRA_VIDEO)!!

        initComments()
        player = SimpleExoPlayer.Builder(requireContext()).build()
        player.addListener(object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
            }

            override fun onRenderedFirstFrame() {
            }
        })
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.playWhenReady = false
        dataBind.playerView.player = player
        dataBind.playerView.controllerAutoShow = false
        dataBind.playerView.setControllerVisibilityListener {
            viewModel.postIntent(VideoPlayIntent.ControllerVisibilityIntent(it == View.VISIBLE))
        }
        dataBind.tvLike.setOnClickListener {
            viewModel.postIntent(VideoPlayIntent.LikeVideoIntent(recommendVideoModel.bvid))
        }
        dataBind.ivFullscreen.setOnClickListener {
            viewModel.postIntent(VideoPlayIntent.FullScreenPlayIntent(true))
        }
        dataBind.ivFullscreenExit.setOnClickListener {
            viewModel.postIntent(VideoPlayIntent.FullScreenPlayIntent(false))
        }

        viewModel.container.uiStateFlow.collectState(viewLifecycleOwner) {
            collectPartial(
                VideoPlayState::fullScreen,
                VideoPlayState::controllerVisible
            ) { fullScreen, visible ->
                if (fullScreen) {
                    dataBind.llRightController.gone()
                    dataBind.flHeader.visibleOrGone(visible)
                } else {
                    dataBind.llRightController.visible()
                    dataBind.flHeader.gone()
                }
            }

            collectPartial(VideoPlayState::videoRotateMode) {
                //显示全屏播放按钮
                dataBind.ivFullscreen.visibleOrGone(it == VideoPlayState.VideoRotateMode.LANDSCAPE)
            }
            collectPartial(VideoPlayState::stat) {
                dataBind.tvCoin.text = it.coin
                dataBind.tvShare.text = it.share
                dataBind.tvLike.text = it.like
                dataBind.tvStar.text = it.favorite
                dataBind.tvReply.text = it.reply
            }
            collectPartial(VideoPlayState::title) {
                dataBind.tvTitle.text = it
            }
        }
        viewModel.container.singleEventFlow.collectSingleEvent(viewLifecycleOwner) { event ->
            when (event) {
                is VideoPlayEvent.VideoPlayUrlEvent -> {
                    if (event.videoUrl.isNotEmpty()) {
                        val mediaItem = MediaItem.Builder()
                            .setUri(event.videoUrl)
                            .build()
                        val mediaSource: ProgressiveMediaSource =
                            ProgressiveMediaSource.Factory(
                                MediaCacheFactory.getCacheFactory(
                                    requireContext()
                                )
                            ).createMediaSource(mediaItem)
                        player.setMediaSource(mediaSource)
                        player.prepare()
                    }
                }

                is VideoPlayEvent.FullScreenPlayEvent -> {
                    val fullScreen = event.fullScreen
                    requireActivity().requestedOrientation =
                        if (fullScreen) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    fullScreen(fullScreen)
                }
                is VideoPlayEvent.VideoCommentsEvent -> {
                    commentsAdapter.submitList(event.comments)
                }
            }
        }
        viewModel.queryVideoUrl(recommendVideoModel.bvid, recommendVideoModel.cid)
        viewModel.queryVideoDetail(recommendVideoModel.bvid)
        viewModel.queryVideoReply(recommendVideoModel.id)

        //初始化一些信息
        viewModel.postIntent(VideoPlayIntent.FullScreenPlayIntent(requireActivity().requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE))
    }

    override fun onResume() {
        super.onResume()
        player.play()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.postIntent(VideoPlayIntent.FullScreenPlayIntent(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE))
    }

    private fun initComments() {
        dataBind.rvComments.layoutManager = LinearLayoutManager(requireActivity())
        dataBind.rvComments.adapter = commentsAdapter
    }
}