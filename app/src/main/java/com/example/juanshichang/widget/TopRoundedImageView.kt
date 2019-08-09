package com.example.juanshichang.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView
import com.example.juanshichang.R

class TopRoundedImageView : ImageView{
    /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/
    var mFloat:Float = 10f
    var rids = arrayOf(18f,18f,18f,18f,0.0f,0.0f,0.0f,0.0f)
    constructor(context: Context):super(context)
    constructor(context: Context,attrs: AttributeSet):super(context,attrs)
    constructor(context: Context,attrs: AttributeSet,defStyleAttr:Int):super(context,attrs,defStyleAttr){
        val ta  = context.obtainStyledAttributes(attrs, R.styleable.TopRoundedImageView)
        val rf = ta.getInteger(R.styleable.TopRoundedImageView_topRounded,3)
        ta.recycle()
        mFloat = rf.toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        val path = Path()
        val w = this.width
        val h = this.height
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
        path.addRoundRect(RectF(0f,0f,w.toFloat(),h.toFloat()),rids.toFloatArray(),Path.Direction.CW)
//        path.addRoundRect(RectF(0f,0f,w.toFloat(),h.toFloat()), radius, radius, Path.Direction.CW) //四角
        canvas!!.clipPath(path)
        super.onDraw(canvas)
    }
    fun setRadius(float: Float){
        this.mFloat = float
    }
}