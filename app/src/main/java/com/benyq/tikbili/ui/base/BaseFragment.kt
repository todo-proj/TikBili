package com.benyq.tikbili.ui.base

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.benyq.tikbili.ui.LifeCycleLogObserver

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
abstract class BaseFragment<DB : ViewDataBinding>(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private var _dataBind: DB? = null
    protected val dataBind: DB get() = _dataBind!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBackPressedHandler()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _dataBind = DataBindingUtil.bind(view)
        viewLifecycleOwner.lifecycle.addObserver(LifeCycleLogObserver())
        onFragmentCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dataBind = null
    }

    abstract fun onFragmentCreated(savedInstanceState: Bundle?)

    open fun onBackPressed(): Boolean {
        return false
    }

    private fun initBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!onBackPressed()) {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }else {
                    isEnabled = true
                }
            }
        })
    }
}