package com.example.juanshichang.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.Api.Companion.BASEURL
import com.example.juanshichang.base.BaseFragment
import com.example.juanshichang.base.Parameter
import com.qmuiteam.qmui.widget.webview.QMUIWebView

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:54
 * @文件作用:  小店
 */
class ThreeFragment : BaseFragment() {
    private var web:QMUIWebView? = null
    override fun getLayoutId(): Int {
        return R.layout.fragment_three
    }

    override fun initViews(savedInstanceState: Bundle) {
        web= mBaseView?.findViewById<QMUIWebView>(R.id.thWeb)
    }

    override fun initData() {
        web?.loadUrl(BASEURL+"/faq.html")
        setWebView(BASEURL+"/faq.html")
    }

    private fun setWebView(urls: String) {
        //这里 可以 考虑 对 其网页做更完善的处理
    }
}
