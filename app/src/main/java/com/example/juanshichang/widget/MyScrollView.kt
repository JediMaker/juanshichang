package com.example.juanshichang.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class MyScrollView : NestedScrollView{
    private var onScrollListeners:OnScrollListener?=null
    private var scrollY:Int? = null
    constructor(context: Context):super(context,null){
    }
    constructor(context: Context,attrs: AttributeSet):super(context,attrs,0){
    }
    constructor(context: Context,attrs: AttributeSet,defStyle:Int):super(context,attrs,defStyle){
    }
    /**
     * 设置滚动接口
     * @param onScrollListener
     */
    public fun setOnScrollListener(onScrollListener:OnScrollListener){
        this.onScrollListeners = onScrollListener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        scrollY = getScrollY()
        if(onScrollListeners != null){
            onScrollListeners!!.onScroll(scrollY!!)
        }
    }
    public interface OnScrollListener{
        /**
         * 回调方法， 返回MyScrollView滑动的Y方向距离
         * @param scrollY            、
         */
        fun onScroll(scrollY:Int)
    }
}