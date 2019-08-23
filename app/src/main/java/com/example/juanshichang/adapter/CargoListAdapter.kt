package com.example.juanshichang.adapter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.BannnerDetailBean
import com.example.juanshichang.bean.CLB
import com.example.juanshichang.utils.TransformationUtils
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.UtilsBigDecimal
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
        /*helper?.setText(R.id.mTypeNumTv, "¥"+Util.getFloatPrice(item!!.min_group_price.toLong())) // 现价
            ?.setText(R.id.mTypeNumTvs, "¥"+Util.getFloatPrice(item?.min_normal_price.toLong()))  //原价
            ?.setText(R.id.mTypeIV, item!!.goods_name)  //标题
        GlideUtil.loadImage(mContexts,item?.goods_thumbnail_url,helper!!.getView(R.id.mTypeImg))
//        QMUIViewHelper.generateViewId() //View 工具类
//        QMUIDisplayHelper.getDisplayMetrics(mContext)//屏幕相关的工具类*/
        helper?.setText(R.id.left_price, Util.getFloatPrice((item!!.min_group_price-item!!.coupon_discount).toLong())) // 现价
            ?.setText(R.id.left_mTypeNumTvs, "¥"+ Util.getFloatPrice(item?.min_normal_price.toLong()))  //原价
            ?.setText(R.id.left_left_label,"拼多多")
            ?.setText(R.id.left_tv_title, item.goods_name)
        getIsVisible(helper!!,item?.has_coupon!!,item)
        GlideUtil.loadImage(mContext,item?.goods_thumbnail_url,helper!!.getView(R.id.left_iv))
    }


    fun getStoreName(s:String):String{ //待处理的方法
        when(s){
            "pdd" -> return "拼多多"
            "jd" -> return "京东"
            "tb" -> return "淘宝"
            else -> return "未知"
        }
    }
    fun getIsVisible(helper: BaseViewHolder, isTrue:Boolean, item: CLB.Goods){ //type == 0 左边 else 1 右边
        if(!isTrue){//没有优惠劵
            helper.setVisible(R.id.left_juan,false)
            helper.setText(R.id.left_right_label,"预估收益"+Util.getProportion(item.min_group_price.toLong(),0,item.promotion_rate,false)+"元")
        }else{ //填入优惠劵数据
            helper.setVisible(R.id.left_juan,true)
            helper.setText(R.id.left_juan_price,
                UtilsBigDecimal.div(item.coupon_discount.toDouble(),100.toDouble(),0).toInt().toString()+"元")
            helper.setText(R.id.left_right_label,"预估收益"+Util.getProportion(item.min_group_price.toLong(),item.coupon_discount.toLong(),item.promotion_rate,true)+"元")
        }
    }
}