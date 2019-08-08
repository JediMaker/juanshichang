package com.example.juanshichang.adapter

import android.content.Context
import android.widget.Adapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.MainRecyclerBean
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.glide.GlideUtil

class MainRecyclerAdapter(layoutResId: Int,context: Context) :
    BaseQuickAdapter<MainRecyclerBean.Theme, BaseViewHolder>(layoutResId){
    var mContexts:Context? = null
    init {
        this.mContexts = context
    }
    override fun convert(helper: BaseViewHolder?, item: MainRecyclerBean.Theme?) {  //应添加数据判断
        helper?.setText(R.id.item_title,item?.theme_name) //填入主标题标题
//        GlideUtil.loadImage(mContexts,item?.theme_image_url,helper!!.getView(R.id.head_iv)) //填入主图
        GlideUtil.loadRoundImage(mContexts,10,item?.theme_image_url,helper!!.getView(R.id.head_iv))
        helper.setText(R.id.left_tv_title,item!!.theme_goods_list[0].goods_name)//填入左图
            .setText(R.id.left_price, Util.getFloatPrice(item.theme_goods_list[0].min_group_price.toLong()))
            .setText(R.id.left_mTypeNumTvs,Util.getFloatPrice(item.theme_goods_list[0].min_normal_price.toLong()))
//        GlideUtil.loadImage(mContexts,item.theme_goods_list[0].goods_thumbnail_url,helper.getView(R.id.left_iv)) //填入左图
        GlideUtil.loadHalfRoundImage(mContexts,8,item.theme_goods_list[0].goods_thumbnail_url,helper.getView(R.id.left_iv))
        helper.setText(R.id.right_tv_title,item.theme_goods_list[0].goods_name)//填入右图
            .setText(R.id.right_price, Util.getFloatPrice(item.theme_goods_list[0].min_group_price.toLong()))
            .setText(R.id.right_mTypeNumTvs,Util.getFloatPrice(item.theme_goods_list[0].min_normal_price.toLong()))
        GlideUtil.loadHalfRoundImage(mContexts,8,item.theme_goods_list[1].goods_thumbnail_url,helper.getView(R.id.right_iv))
//        GlideUtil.loadImage(mContexts,item.theme_goods_list[1].goods_thumbnail_url,helper.getView(R.id.right_iv)) //填入右图
        //设置点击事件
        helper.addOnClickListener(R.id.head_iv)
        helper.addOnClickListener(R.id.r1)
        helper.addOnClickListener(R.id.r2)

    }
}