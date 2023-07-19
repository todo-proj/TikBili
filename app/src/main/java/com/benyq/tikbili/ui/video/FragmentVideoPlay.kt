package com.benyq.tikbili.ui.video

import android.content.pm.ActivityInfo
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.view.View.OnLayoutChangeListener
import androidx.fragment.app.viewModels
import com.benyq.tikbili.R
import com.benyq.tikbili.bilibili.model.RecommendVideoModel
import com.benyq.tikbili.databinding.FragmentVideoPlayBinding
import com.benyq.tikbili.ext.fullScreen
import com.benyq.tikbili.ext.ifFalse
import com.benyq.tikbili.ext.ifTrue
import com.benyq.tikbili.ext.visibleOrGone
import com.benyq.tikbili.player.ExoVideoPlayer
import com.benyq.tikbili.ui.base.BaseFragment
import com.benyq.tikbili.ui.base.mvi.extension.collectState
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
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
    private lateinit var player: ExoVideoPlayer

    private lateinit var recommendVideoModel: RecommendVideoModel

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        recommendVideoModel = requireArguments().getParcelable(EXTRA_VIDEO)!!

        player = ExoVideoPlayer(requireContext(), true).apply {
            create()
            setVideoPlayListener(object : ExoVideoPlayer.OnVideoPlayListener {
                override fun onVideoSizeChanged(width: Int, height: Int) {
                    viewModel.postIntent(
                        VideoPlayIntent.ResizePlayViewIntent(
                            Size(
                                dataBind.playerView.width,
                                dataBind.playerView.height
                            ), Size(width, height)
                        )
                    )
                }

                override fun onPlayingChanged(isPlaying: Boolean) {
                    viewModel.postIntent(VideoPlayIntent.PlayPauseVideoIntent(isPlaying))
                }
            })
        }
        player.setRenderView(dataBind.playerView)
        dataBind.playerView.setOnClickListener {
            if (player.isVideoPlaying) {
                player.pauseVideo()
            }else {
                player.startVideo()
            }
        }
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
            collectPartial(VideoPlayState::videoUrl) {
                if (it.isNotEmpty()) {
                    player.playVideo(it)
                }
            }
            collectPartial(VideoPlayState::videoMatrix) { matrix->
                dataBind.playerView.let {
                    it.setTransform(matrix)
                    it.postInvalidate()
                }
            }
            collectPartial(VideoPlayState::isPlaying) {
                dataBind.ivPause.visibleOrGone(!it)
            }
        }

        viewModel.queryVideoUrl(recommendVideoModel.bvid, recommendVideoModel.cid)
        viewModel.queryVideoDetail(recommendVideoModel.bvid)
    }

    override fun onResume() {
        super.onResume()
        player.startVideo()
    }

    override fun onPause() {
        super.onPause()
        player.pauseVideo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
    }
}