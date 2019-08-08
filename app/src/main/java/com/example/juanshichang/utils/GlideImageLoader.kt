package com.example.juanshichang.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.utils.glide.GlideRoundedCornersTransform
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.youth.banner.loader.ImageLoader

class GlideImageLoader : ImageLoader() {

    override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
        //Glide 加载图片简单用法
        //DiskCacheStrategy.RESOURCE 只缓存原始文件
        //DiskCacheStrategy.ALL 缓存所有size的图片和源文件
        //DiskCacheStrategy.RESULT 缓存最后的结果文件
        //DiskCacheStrategy.NONE 撒都不缓存
        var options = RequestOptions()
//            .transform(StaggeredBitmapTransform(MyApp.getInstance()))
            .optionalTransform(
                GlideRoundedCornersTransform(
                    QMUIDisplayHelper.dp2px(context, 8).toFloat(),
                    GlideRoundedCornersTransform.CornerType.ALL)
            )
            .error(R.drawable.c_error)//加载失败显示图片
            .placeholder(R.drawable.c_error)//预加载图片
            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .transform(GlideRoundTransform(8, 0))
            .priority(Priority.HIGH) //优先级
        Glide.with(context!!).load(path).apply(options).into(imageView!!)
    }

}