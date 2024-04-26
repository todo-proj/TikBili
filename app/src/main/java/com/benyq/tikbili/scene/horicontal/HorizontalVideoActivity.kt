package com.benyq.tikbili.scene.horicontal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.benyq.tikbili.R
import com.benyq.tikbili.base.ext.fullScreen
import com.benyq.tikbili.databinding.ActivityHorizontalVideoBinding
import com.benyq.tikbili.player.helper.DisplayModeHelper
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.VideoLayerHost
import com.benyq.tikbili.player.playback.layer.PauseLayer
import com.benyq.tikbili.player.source.MediaSource
import com.benyq.tikbili.scene.horicontal.layer.GestureLayer
import com.benyq.tikbili.scene.horicontal.layer.SpeedSelectDialogLayer
import com.benyq.tikbili.scene.horicontal.layer.TimeProgressBarLayer
import com.benyq.tikbili.scene.horicontal.layer.TitleBarLayer
import com.benyq.tikbili.ui.base.BaseActivity
import me.jessyan.autosize.internal.CustomAdapt

/**
 * @author benyq
 * @date 4/24/2024
 */
class HorizontalVideoActivity : BaseActivity<ActivityHorizontalVideoBinding>(), CustomAdapt {

    companion object {
        private const val EXTRA_SOURCE = "mediaSource"
        private const val EXTRA_CONTINUE = "continuePlay"
        private const val EXTRA_POSITION = "currentPosition"
        fun startActivity(context: Context, mediaSource: MediaSource, continuePlay: Boolean, currentPosition: Long) {
            context.startActivity(Intent(context, HorizontalVideoActivity::class.java).apply {
                putExtra(EXTRA_SOURCE, mediaSource)
                putExtra(EXTRA_CONTINUE, continuePlay)
                putExtra(EXTRA_POSITION, currentPosition)
            })
        }
    }

    private val playbackController = PlaybackController()


    override fun getLayoutId() = R.layout.activity_horizontal_video
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        fullScreen(true)

        val mediaSource: MediaSource = intent.getSerializableExtra(EXTRA_SOURCE) as? MediaSource ?: let {
            finish()
            return
        }
        val continuePlay = intent.getBooleanExtra(EXTRA_CONTINUE, false)
        val currentPosition = intent.getLongExtra(EXTRA_POSITION, 0L)

        val layerHost = VideoLayerHost(this)
        layerHost.addLayer(GestureLayer())
        layerHost.addLayer(TimeProgressBarLayer())
        layerHost.addLayer(TitleBarLayer())
        layerHost.addLayer(PauseLayer())
        layerHost.addLayer(SpeedSelectDialogLayer())
        layerHost.attachToVideoView(dataBind.videoView)
        dataBind.videoView.bindDataSource(mediaSource)
        playbackController.bind(dataBind.videoView)
        dataBind.videoView.setDisplayMode(DisplayModeHelper.DISPLAY_MODE_ASPECT_FILL_Y)
        dataBind.videoView.setupDisplayView()
        playbackController.startPlayback(continuePlay)
        playbackController.player()?.seekTo(currentPosition)


        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!layerHost.onBackPressed()) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }else {
                    isEnabled = true
                }
            }
        })
    }


    override fun isBaseOnWidth() = true

    override fun getSizeInDp() = 640f
}