package com.benyq.tikbili.ui.test

import android.graphics.Color
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.FragmentTestBinding
import com.benyq.tikbili.player.playback.PlaybackController
import com.benyq.tikbili.player.playback.VideoLayerHost
import com.benyq.tikbili.player.helper.DisplayModeHelper
import com.benyq.tikbili.player.playback.layer.PauseLayer
import com.benyq.tikbili.player.playback.layer.SimpleProgressBarLayer
import com.benyq.tikbili.ui.base.BaseFragment
import kotlinx.coroutines.launch

/**
 *
 * @author benyq
 * @date 7/26/2023
 *
 */
class TestFragment: BaseFragment<FragmentTestBinding>(R.layout.fragment_test) {
    private val viewModel by viewModels<TestViewModel>()

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        val shortPageView = dataBind.shortPage
        lifecycleScope.launch {
            viewModel.sharedEvent.collect {
                shortPageView.setItems(it)
            }
        }
        lifecycleScope.launch {
            viewModel.loadingState.collect {
                dataBind.loading.isVisible = it
            }
        }
        viewModel.getTestData()
    }
}