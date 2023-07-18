package com.benyq.tikbili.ui.login

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.ActivityLoginBinding
import com.benyq.tikbili.ext.MMKVValue
import com.benyq.tikbili.ext.fromM
import com.benyq.tikbili.ui.base.BaseActivity

/**
 * @author benyq
 * @date 7/17/2023
 * web 登录页面
 */
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private val windowsUA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 Edg/98.0.1108.55"

    private var biliCookie by MMKVValue("biliCookie", "")

    private lateinit var mWebView: WebView

    override fun getLayoutId() = R.layout.activity_login

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        dataBind.ivBack.setOnClickListener { finish() }
        mWebView = WebView(this)
        dataBind.llContainer.addView(mWebView)
        initWebView()
        mWebView.loadUrl("https://passport.bilibili.com/login")
    }

    private fun initWebView() {
        val webSettings = mWebView.settings
        webSettings.userAgentString = windowsUA
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setSupportMultipleWindows(true)
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        if (fromM()) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        mWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                //监听获取cookie
                val cookie = CookieManager.getInstance().getCookie(url)
                Log.d("benyq", "onPageFinished: cookie: $cookie")
                biliCookie = cookie
            }
            override fun shouldInterceptRequest(
                view: WebView?,
                url: String,
            ): WebResourceResponse? {

                return null
            }
        }

        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    dataBind.progressBar.progress = 100
                    dataBind.progressBar.visibility = View.GONE
                } else {
                    if (dataBind.progressBar.visibility == View.GONE) dataBind.progressBar.visibility =
                        View.VISIBLE
                    dataBind.progressBar.progress = newProgress
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
            }
        }
    }


    override fun onDestroy() {
        // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
        // destory()
        val parent = mWebView.parent
        if (parent != null) {
            (parent as ViewGroup).removeView(mWebView)
        }
        mWebView.stopLoading()
        // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
        mWebView.settings.javaScriptEnabled = false
        mWebView.clearHistory()
        mWebView.clearView()
        mWebView.removeAllViews()
        mWebView.destroy()
        super.onDestroy()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}

