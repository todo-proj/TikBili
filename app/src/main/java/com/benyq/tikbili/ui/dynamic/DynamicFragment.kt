package com.benyq.tikbili.ui.dynamic

import android.os.Bundle
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.FragmentDynamicBinding
import com.benyq.tikbili.ui.base.BaseFragment

/**
 *
 * @author benyq
 * @date 6/4/2024
 * 动态
 */
class DynamicFragment: BaseFragment<FragmentDynamicBinding>(R.layout.fragment_dynamic) {
    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        dataBind.rvDynamic
    }
}