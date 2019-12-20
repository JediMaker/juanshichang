package com.example.juanshichang.fragment


import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView

import com.example.juanshichang.R
import com.example.juanshichang.base.Api.Companion.BASEURL
import com.example.juanshichang.base.BaseFragment

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:54
 * @文件作用:  小店
 */
class ThreeFragment : BaseFragment() {
    private var web:WebView? = null
    override fun getLayoutId(): Int {
        return R.layout.fragment_three
    }

    override fun initViews(savedInstanceState: Bundle) {
        web= mBaseView?.findViewById<WebView>(R.id.thWeb)
    }

    override fun initData() {
        web?.settings?.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        setWebView(BASEURL+"/faq.html")
    }

    override fun onResume() {
        super.onResume()
        web?.loadUrl(BASEURL+"/faq.html")
    }
    private fun setWebView(urls: String) {
        //这里 可以 考虑 对 其网页做更完善的处理
    }
}
