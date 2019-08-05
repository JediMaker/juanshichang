package com.example.juanshichang.adapter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.CLB
import com.example.juanshichang.utils.TransformationUtils
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.glide.GlideUtil
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIViewHelper

class CargoListAdapter(layoutResId: Int, data: MutableList<CLB.Goods>?, context: Context) :
    BaseQuickAdapter<CLB.Goods, BaseViewHolder>(layoutResId, data) {
    var  mContexts:Context? = null
    init {
        this.mContexts = context
    }

    override fun convert(helper: BaseViewHolder?, item: CLB.Goods?) {
        helper?.setText(R.id.mTypeNumTv, "¥"+Util.getFloatPrice(item!!.min_group_price.toLong())) // 现价
            ?.setText(R.id.mTypeNumTvs, "¥"+Util.getFloatPrice(item?.min_normal_price.toLong()))  //原价
            ?.setText(R.id.mTypeIV, item!!.goods_name)  //标题
//        val utils = TransformationUtils(helper!!.getView(R.id.mTypeImg))
//        Glide.with(mContext).load(item?.goods_thumbnail_url)
//            .placeholder(R.drawable.c_defull_null)
//            .into(helper!!.getView(R.id.mTypeImg))
        GlideUtil.loadImage(mContexts,item?.goods_thumbnail_url,helper!!.getView(R.id.mTypeImg))
//        QMUIViewHelper.generateViewId() //View 工具类
//        QMUIDisplayHelper.getDisplayMetrics(mContext)//屏幕相关的工具类
    }

}