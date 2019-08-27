package com.example.juanshichang.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridView

class OrderConfirmGridView :GridView{
    constructor(context: Context):super(context)
    constructor(context: Context,attrs: AttributeSet):super(context, attrs)
    constructor(context: Context,attrs: AttributeSet,defStyleAttr:Int):super(context, attrs,defStyleAttr)
    var isOneMeasure = false
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var expandSpec:Int = MeasureSpec.makeMeasureSpec(
            Integer.MAX_VALUE shr 2,
            MeasureSpec.AT_MOST
        )
        isOneMeasure = false
        super.onMeasure(widthMeasureSpec, expandSpec) //heightMeasureSpec
    }
    //添加这个方法 以及 isOneMeasure 字段是为了 防止 getView 多次调用
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        isOneMeasure = true
        super.onLayout(changed, l, t, r, b)
    }
}