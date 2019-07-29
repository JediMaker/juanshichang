package com.example.juanshichang.utils

import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.target.ImageViewTarget

/**
 * @作者: yzq
 * @创建日期: 2019/7/26 11:28
 * @文件作用: 等比缩放图片
 */
class TransformationUtils : ImageViewTarget<Bitmap>{
    var target:ImageView? = null
    constructor(target:ImageView):super(target){
        this.target = target
    }

    override fun setResource(resource: Bitmap?) {
        view.setImageBitmap(resource)
        //获取原图的宽高
        val width:Int = resource?.width!!
        val height:Int = resource.height
        //获取imageView的宽
        val imageViewWidth:Int = target?.height!!
        //计算缩放比例
        val sy : Float = ((imageViewWidth * 0.1) / (width * 0.1)).toFloat()
        //计算图片等比例放大后的高
        val imageViewHeight:Int = (height * sy) as Int
        val params: ViewGroup.LayoutParams = target!!.layoutParams
        params.height = imageViewHeight
        target!!.layoutParams = params
    }
}