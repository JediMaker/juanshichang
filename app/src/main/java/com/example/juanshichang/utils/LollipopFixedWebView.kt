package com.example.juanshichang.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebView

/**
 * @作者: yzq
 * @创建日期: 2019/12/20 14:43
 * @文件作用: 用于解决部分机型---系统版本 WebView爆出
 *    android.view.InflateException:
 *    Binary XML file line #13:
 *    Error inflating class android.webkit.WebView
 *    问题
 */
class LollipopFixedWebView : WebView{
    constructor(context: Context):super(getFixedContext(context))
    constructor(context: Context,attrs:AttributeSet):super(getFixedContext(context),attrs)
    constructor(context: Context,attrs:AttributeSet,defStyleAttr:Int):super(getFixedContext(context),attrs,defStyleAttr)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context,attrs:AttributeSet,defStyleAttr:Int,defStyleRes:Int):super(getFixedContext(context),attrs,defStyleAttr,defStyleRes)
    constructor(context: Context,attrs:AttributeSet,defStyleAttr:Int,privateBrowsing:Boolean):super(getFixedContext(context),attrs,defStyleAttr,privateBrowsing)
    companion object{
        fun getFixedContext(context: Context):Context{
            return context.createConfigurationContext(Configuration())
        }
    }
}