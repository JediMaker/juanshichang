package com.example.juanshichang.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
/**
 * @作者: yzq
 * @创建日期: 2019/7/19 11:55
 * @文件作用:禁止左右滑动 包括连续滑动也不响应 同时不影响setCurrentItem
 */
open class CustomViewPager : ViewPager{
    private var enable = true
    constructor(context:Context):super(context)
    constructor(context: Context,attrs: AttributeSet):super(context,attrs)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {// 触摸事件不触发  事件消费方法
        if(enable){
            return super.onTouchEvent(ev)
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {// 不处理触摸拦截事件  事件拦截方法
        if(enable){
            return super.onInterceptTouchEvent(ev)
        }
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {// 分发事件，这个是必须要的，如果把这个方法覆盖了，那么ViewPager的子View就接收不到事件了
        return super.dispatchTouchEvent(ev)
    }

    /**
     * @param enabled true 可滑动 false 不可滑动
     */
    public fun setPagingEnabled(enabled: Boolean) {
        enable = enabled
    }
}