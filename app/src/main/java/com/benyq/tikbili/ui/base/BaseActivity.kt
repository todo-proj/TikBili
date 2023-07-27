package com.benyq.tikbili.ui.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
abstract class BaseActivity<DB: ViewDataBinding>: AppCompatActivity() {

    private var _dataBind: DB? = null
    protected val dataBind: DB get() = checkNotNull(_dataBind) { "初始化binding失败" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _dataBind = DataBindingUtil.setContentView(
            this, getLayoutId()
        )
        lifecycle.addObserver(object: DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                _dataBind = null
            }
        })
        onActivityCreated(savedInstanceState)
    }

    abstract fun getLayoutId(): Int

    abstract fun onActivityCreated(savedInstanceState: Bundle?)
}
