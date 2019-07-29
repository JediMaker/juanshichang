package com.example.juanshichang.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.utils.glide.StaggeredBitmapTransform
import com.youth.banner.loader.ImageLoader

class GlideImageLoader : ImageLoader() {

    override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
        //Glide 加载图片简单用法
        var options = RequestOptions().transform(StaggeredBitmapTransform(MyApp.getInstance()))
            .error(R.drawable.c_error)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(context!!).load(path).apply(options).into(imageView!!)
    }

}