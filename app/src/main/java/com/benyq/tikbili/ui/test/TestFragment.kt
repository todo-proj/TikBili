package com.benyq.tikbili.ui.test

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.FragmentTestBinding
import com.benyq.tikbili.ui.base.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *
 * @author benyq
 * @date 7/26/2023
 *
 */
class TestFragment: BaseFragment<FragmentTestBinding>(R.layout.fragment_test) {
    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        dataBind.refreshView.setOnSlideBottomListener {
            true
        }
        dataBind.refreshView.setOnLoadingListener {
            lifecycleScope.launch {
                delay(4000)
                dataBind.refreshView.finishLoading()
            }
        }

    }
}