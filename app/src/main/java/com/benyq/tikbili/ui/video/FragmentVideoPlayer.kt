package com.benyq.tikbili.ui.video

import android.os.Bundle
import android.util.Log
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.FragmentVideoPlayerBinding
import com.benyq.tikbili.ui.base.BaseFragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
class FragmentVideoPlayer: BaseFragment<FragmentVideoPlayerBinding>(R.layout.fragment_video_player) {

    private lateinit var player: ExoPlayer
    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        player = ExoPlayer.Builder(requireActivity()).build()
        player.repeatMode = Player.REPEAT_MODE_ALL
        dataBind.playerView.player = player
        player.setMediaItem(MediaItem.fromUri("https://sdk.faceunity.com/migu-tsg/assets/video1.mp4"))
        player.prepare()
        player.playWhenReady = false
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