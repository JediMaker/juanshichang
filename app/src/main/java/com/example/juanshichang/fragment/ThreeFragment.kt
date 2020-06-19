package com.example.juanshichang.fragment


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.*
import android.widget.LinearLayout

import com.example.juanshichang.R
import com.example.juanshichang.base.Api.Companion.BASEURL
import com.example.juanshichang.base.BaseFragment

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:54
 * @文件作用:  小店
 */
class ThreeFragment : BaseFragment() {
    private var mWebView:WebView? = null
    private var layoutEmpty: LinearLayout? = null
    private var isLoadError = false
    override fun getLayoutId(): Int {
        return R.layout.fragment_three
    }

    override fun initViews(savedInstanceState: Bundle) {
        mWebView= mBaseView?.findViewById<WebView>(R.id.thWeb)
        layoutEmpty =mBaseView?. findViewById<View>(R.id.layout_empty) as LinearLayout
        val settings = mWebView!!.settings
        settings.javaScriptEnabled = true
        settings.allowContentAccess = true
        settings.setAppCacheMaxSize(1024)
        mWebView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                isLoadError = true
                if (isLoadError) {
                    layoutEmpty!!.visibility = View.VISIBLE
                } else {
                    layoutEmpty!!.visibility = View.GONE
                }
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                isLoadError = true
                if (isLoadError) {
                    layoutEmpty!!.visibility = View.VISIBLE
                } else {
                    layoutEmpty!!.visibility = View.GONE
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
//                layoutLoading.setVisibility(View.GONE)
                mContext?.dismissProgressDialog()
                if (isLoadError) {
                    layoutEmpty!!.visibility = View.VISIBLE
                } else {
                    layoutEmpty!!.visibility = View.GONE
                }
            }
        }

        mWebView!!.setWebChromeClient(object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                if (!TextUtils.isEmpty(title) && title.toLowerCase().contains("404")) {
//                    layoutLoading.setVisibility(View.GONE)
                    mContext?.dismissProgressDialog()
                    isLoadError = true
                    if (isLoadError) {
                        layoutEmpty!!.visibility = View.VISIBLE
                    } else {
                        layoutEmpty!!.visibility = View.GONE
                    }
                }
            }
        })

        layoutEmpty!!.setOnClickListener {
            mWebView!!.reload()
        }
    }

    override fun initData() {
        mWebView?.settings?.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        setWebView(BASEURL+"/faq.html")
    }

    override fun onResume() {
        super.onResume()
        mWebView?.loadUrl(BASEURL+"/faq.html")
        mContext?.showProgressDialog()
    }
    private fun setWebView(urls: String) {
        //这里 可以 考虑 对 其网页做更完善的处理
    }
}
