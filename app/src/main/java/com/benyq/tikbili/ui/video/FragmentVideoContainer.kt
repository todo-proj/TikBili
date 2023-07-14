package com.benyq.tikbili.ui.video

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.FragmentVideoContainerBinding
import com.benyq.tikbili.ext.overScrollNever
import com.benyq.tikbili.ui.base.BaseFragment

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
class FragmentVideoContainer: BaseFragment<FragmentVideoContainerBinding>(R.layout.fragment_video_container) {

    private val viewModel by viewModels<VideoPlayerViewModel>()

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        dataBind.vpVideo.offscreenPageLimit = 3
        dataBind.vpVideo.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 5
            }

            override fun createFragment(position: Int): Fragment {
                return FragmentVideoPlayer()
            }
        }
        dataBind.vpVideo.orientation = ViewPager2.ORIENTATION_VERTICAL
        dataBind.vpVideo.overScrollNever()
    }
}