package com.example.juanshichang.adapter

import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ImageBannerNetAdapter.ImageHolder
import com.example.juanshichang.bean.ZyProduct
import com.example.juanshichang.utils.GlideImageLoader
import com.example.juanshichang.utils.glide.GlideUtil
import com.youth.banner.adapter.BannerAdapter

/**
 * 自定义布局，商品详情图片
 */
class GoodsImageAdapter(mDatas: List<ZyProduct.Image?>?) :
    BannerAdapter<ZyProduct.Image?, ImageHolder>(mDatas) {
    //更新数据
    fun updateData(data: List<ZyProduct.Image?>?) {
        //这里的代码自己发挥，比如如下的写法等等
        mDatas.clear()
        mDatas.addAll(data!!)
        notifyDataSetChanged()
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imageView = ImageView(parent.context)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return ImageHolder(imageView)
    }


    override fun onBindView(
        holder: ImageHolder?,
        data: ZyProduct.Image?,
        position: Int,
        size: Int
    ) {
        //通过图片加载器实现圆角，你也可以自己使用圆角的imageview，实现圆角的方法很多，自己尝试哈
        holder?.imageView?.let {
//            GlideUtil.loadImage(MyApp.getInstance()!!,data?.thumb,it,2) //设置高清图ARGB_8888
            GlideImageLoader(1).displayImage(MyApp.getInstance(),data?.popup,it)
        }
    }
}