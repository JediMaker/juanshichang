package com.example.juanshichang.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned

/**
 * @作者：yzq
 * @创建时间：2019/9/3 16:20
 * @文件作用: 这是一个处理详情标题的工具类
 */
class TextAndPictureUtil {
    companion object {
        fun getText(context: Context, text: String, drawId: Int):SpannableString{
            val spannableString: SpannableString = SpannableString("  " + text)
            val drawable: Drawable = context.resources.getDrawable(drawId)
            drawable.setBounds(0, 0, 71, 30)
            spannableString.setSpan(VerticalImageSpan(drawable),0,1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spannableString
        }
    }
}