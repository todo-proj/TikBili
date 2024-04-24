package com.benyq.tikbili.scene

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.benyq.tikbili.R
import com.benyq.tikbili.base.ext.fullScreen
import com.benyq.tikbili.databinding.ActivityHorizontalVideoBinding
import com.benyq.tikbili.player.helper.DisplayModeHelper
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.VideoLayerHost
import com.benyq.tikbili.player.playback.layer.PauseLayer
import com.benyq.tikbili.player.playback.layer.SimpleProgressBarLayer
import com.benyq.tikbili.player.source.MediaSource
import com.benyq.tikbili.ui.base.BaseActivity

/**
 * @author benyq
 * @date 4/24/2024
 */
class HorizontalVideoActivity : BaseActivity<ActivityHorizontalVideoBinding>() {

    companion object {
        private const val EXTRA_TAG = "mediaSource"
        fun startActivity(context: Context, mediaSource: MediaSource) {
            context.startActivity(Intent(context, HorizontalVideoActivity::class.java).apply {
                putExtra(EXTRA_TAG, mediaSource)
            })
        }
    }

    private val playbackController = PlaybackController()


    override fun getLayoutId() = R.layout.activity_horizontal_video
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        fullScreen(true)

        val mediaSource: MediaSource = intent.getParcelableExtra(EXTRA_TAG) ?: let {
            finish()
            return
        }

        val layerHost = VideoLayerHost(this)
        layerHost.addLayer(SimpleProgressBarLayer())
        layerHost.addLayer(PauseLayer())
        layerHost.attachToVideoView(dataBind.videoView)
        dataBind.videoView.bindDataSource(mediaSource)
        playbackController.bind(dataBind.videoView)
        dataBind.videoView.setDisplayMode(DisplayModeHelper.DISPLAY_MODE_ASPECT_FILL_Y)
        dataBind.videoView.setupDisplayView()
        playbackController.startPlayback()
        playbackController.player()?.seekTo(3000)
    }
}