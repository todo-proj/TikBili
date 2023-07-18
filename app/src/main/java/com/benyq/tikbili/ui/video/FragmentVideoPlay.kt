package com.benyq.tikbili.ui.video

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.benyq.tikbili.R
import com.benyq.tikbili.bilibili.model.RecommendVideoData
import com.benyq.tikbili.bilibili.model.RecommendVideoModel
import com.benyq.tikbili.databinding.FragmentVideoPlayBinding
import com.benyq.tikbili.ext.fullScreen
import com.benyq.tikbili.ext.ifFalse
import com.benyq.tikbili.ext.ifTrue
import com.benyq.tikbili.player.MediaCacheFactory
import com.benyq.tikbili.ui.base.BaseFragment
import com.benyq.tikbili.ui.base.mvi.extension.collectState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource

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
    private lateinit var player: ExoPlayer

    private lateinit var recommendVideoModel: RecommendVideoModel

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        recommendVideoModel = requireArguments().getParcelable(EXTRA_VIDEO)!!

        player = ExoPlayer.Builder(requireActivity()).build()
        player.repeatMode = Player.REPEAT_MODE_ALL

        dataBind.playerView.player = player
        player.playWhenReady = false

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
                    val mediaItem = MediaItem.fromUri(it)
                    val mediaSource =
                        ProgressiveMediaSource.Factory(MediaCacheFactory.getCacheFactory(requireActivity()))
                            .createMediaSource(mediaItem)
                    player.setMediaSource(mediaSource)
                    player.prepare()
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