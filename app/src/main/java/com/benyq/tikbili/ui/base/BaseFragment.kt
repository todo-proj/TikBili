package com.benyq.tikbili.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
abstract class BaseFragment<DB : ViewDataBinding>(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private var _dataBind: DB? = null
    protected val dataBind: DB get() = _dataBind!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _dataBind = DataBindingUtil.bind(view)
        onFragmentCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dataBind = null
    }

    abstract fun onFragmentCreated(savedInstanceState: Bundle?)

}