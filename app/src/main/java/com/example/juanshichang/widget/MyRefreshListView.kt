package com.example.juanshichang.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ListView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * author:翊-yzq
 * type: MyRefreshListView.kt
 * details: 这是一个自定义的ListView 防止 组件之间的滑动冲突
 *  本类详细用法 请参考 https://blog.csdn.net/u014752325/article/details/53556234
 * create-date:2019/8/27 16:28
 */
class MyRefreshListView : ListView{
//   另外 与 RefreshLayout 配合 的解决参考 https://blog.csdn.net/qq_34983989/article/details/77197662
    constructor(context: Context):super(context)
    constructor(context: Context,attrs:AttributeSet):super(context,attrs)
    constructor(context: Context,attrs:AttributeSet,defStyle:Int):super(context,attrs,defStyle)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        //判断是否滑动到顶部
        if(firstVisiblePosition == 0 && getChildAt(0).top == 0){ //到顶部了
            //返回触摸事件
            parent.requestDisallowInterceptTouchEvent(false)
        }else{
            //拦截触摸事件
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return super.onInterceptTouchEvent(ev)
    }
}