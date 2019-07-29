package com.example.juanshichang.widget

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView

class PriceTextView : TextView {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    fun initView() {
        paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
        paint.isAntiAlias = true //抗锯齿
    }
}