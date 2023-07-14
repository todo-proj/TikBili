package com.benyq.tikbili.ui.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
abstract class BaseActivity<DB: ViewDataBinding>: AppCompatActivity() {

    protected lateinit var dataBind: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBind = DataBindingUtil.setContentView(
            this, getLayoutId()
        )
        onActivityCreated(savedInstanceState)
    }

    abstract fun getLayoutId(): Int

    abstract fun onActivityCreated(savedInstanceState: Bundle?)
}
