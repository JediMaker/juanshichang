package com.example.juanshichang.adapter

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ImageBannerNetAdapter.ImageHolder
import com.example.juanshichang.bean.HomeBean
import com.example.juanshichang.utils.GlideImageLoader
import com.example.juanshichang.utils.glide.GlideUtil
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.util.BannerUtils

/**
 * 自定义布局，网络图片
 */
class ImageBannerNetAdapter(mDatas: List<HomeBean.Slideshow>) :
    BannerAdapter<HomeBean.Slideshow?, ImageHolder>(mDatas) {

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imageView =
            BannerUtils.getView(parent, R.layout.banner_image) as ImageView
   /*     //通过裁剪实现圆角
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BannerUtils.setBannerRound(imageView, 20f)
        }*/
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        return ImageHolder(imageView)
    }


     class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView

        init {
            imageView = view as ImageView
        }
    }

    override fun onBindView(
        holder: ImageHolder?,
        data: HomeBean.Slideshow?,
        position: Int,
        size: Int
    ) {
        //通过图片加载器实现圆角，你也可以自己使用圆角的imageview，实现圆角的方法很多，自己尝试哈
        holder?.imageView?.let {
            GlideImageLoader(0).displayImage(MyApp.getInstance(),data?.image,it)
//            GlideUtil.loadImage(MyApp.getInstance()!!,data?.image,it,3) //设置高清图ARGB_8888
        }
    }
}