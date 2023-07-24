package com.benyq.tikbili.ui.video

import android.content.pm.ActivityInfo
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.viewModels
import com.benyq.tikbili.R
import com.benyq.tikbili.bilibili.model.RecommendVideoModel
import com.benyq.tikbili.databinding.FragmentVideoPlayBinding
import com.benyq.tikbili.ext.fullScreen
import com.benyq.tikbili.ext.ifFalse
import com.benyq.tikbili.ext.ifTrue
import com.benyq.tikbili.ext.visibleOrGone
import com.benyq.tikbili.player.ExoVideoPlayer
import com.benyq.tikbili.player.MediaCacheFactory
import com.benyq.tikbili.ui.LifeCycleLogObserver
import com.benyq.tikbili.ui.base.BaseFragment
import com.benyq.tikbili.ui.base.mvi.extension.collectSingleEvent
import com.benyq.tikbili.ui.base.mvi.extension.collectState
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import kotlin.math.log
import kotlin.math.min

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

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycle.addObserver(LifeCycleLogObserver())
        recommendVideoModel = requireArguments().getParcelable(EXTRA_VIDEO)!!

        player = SimpleExoPlayer.Builder(requireContext()).build()
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.playWhenReady = false
        dataBind.playerView.player = player
        dataBind.tvLike.setOnClickListener {
            viewModel.postIntent(VideoPlayIntent.LikeVideoIntent(recommendVideoModel.bvid))
        }
        dataBind.ivFullscreen.setOnClickListener {
            viewModel.postIntent(VideoPlayIntent.FillScreenPlayIntent)
        }
        viewModel.container.uiStateFlow.collectState(viewLifecycleOwner) {
            collectPartial(VideoPlayState::fullScreen) {
                it.ifTrue {
                    requireActivity().requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
                it.ifFalse {
                    requireActivity().requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                fullScreen(it)
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
        }
        viewModel.container.singleEventFlow.collectSingleEvent(viewLifecycleOwner) { event ->
            when(event) {
                is VideoPlayEvent.VideoPlayUrlEvent -> {
                    if (event.videoUrl.isNotEmpty()) {
                        val mediaItem = MediaItem.Builder()
                            .setUri(event.videoUrl)
                            .build()
                        val mediaSource: ProgressiveMediaSource =
                            ProgressiveMediaSource.Factory(MediaCacheFactory.getCacheFactory(requireContext()))
                                .createMediaSource(mediaItem)
                        player.setMediaSource(mediaSource)
                        player.prepare()
                    }
                }
            }
        }
        viewModel.queryVideoUrl(recommendVideoModel.bvid, recommendVideoModel.cid)
        viewModel.queryVideoDetail(recommendVideoModel.bvid)
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
}