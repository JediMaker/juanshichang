package com.example.juanshichang.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridView

class OrderConfirmGridView :GridView{
    constructor(context: Context):super(context)
    constructor(context: Context,attrs: AttributeSet):super(context, attrs)
    constructor(context: Context,attrs: AttributeSet,defStyleAttr:Int):super(context, attrs,defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var expandSpec:Int = MeasureSpec.makeMeasureSpec(
            Integer.MAX_VALUE shr 2,
            MeasureSpec.AT_MOST
        )
        super.onMeasure(widthMeasureSpec, expandSpec) //heightMeasureSpec
    }
}