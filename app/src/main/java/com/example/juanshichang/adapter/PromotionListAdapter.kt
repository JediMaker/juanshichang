package com.example.juanshichang.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.BannnerDetailBean
import com.example.juanshichang.bean.MainRecyclerBean
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.UtilsBigDecimal
import com.example.juanshichang.utils.glide.GlideUtil
import com.qmuiteam.qmui.layout.QMUIFrameLayout
import com.qmuiteam.qmui.layout.QMUILayoutHelper

class PromotionListAdapter(layoutResId: Int, data: MutableList<BannnerDetailBean.X>?) :
    BaseQuickAdapter<BannnerDetailBean.X, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder?, item: BannnerDetailBean.X?) {
        val QF = helper?.getView<QMUIFrameLayout>(R.id.mTypeLayout)

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
    fun getIsVisible(helper: BaseViewHolder, isTrue:Boolean, item: BannnerDetailBean.X){ //type == 0 左边 else 1 右边
        if(!isTrue){//没有优惠劵
                helper.setVisible(R.id.left_juan,false)
                helper.setText(R.id.left_right_label,"预估收益"+Util.getProportion(item.min_group_price.toLong(),0,item.promotion_rate,false)+"元")
        }else{ //填入优惠劵数据
                helper.setVisible(R.id.left_juan,true)
                helper.setText(R.id.left_juan_price,
                    UtilsBigDecimal.div(item.coupon_discount.toDouble(),100.toDouble(),0).toInt().toString()+"元")
                helper.setText(R.id.left_right_label,"预估收益"+Util.getProportion(item.min_group_price.toLong(),item.coupon_discount.toLong(),item.promotion_rate,true)+"元")
        }
        /*//填入预估佣金
        if(type == 0){
            helper.setText(R.id.left_promotion_rate,getProportion(item.theme_goods_list[0].promotion_rate.toLong(),item.theme_goods_list[0].min_group_price.toLong()))
        }else if(type == 1){
            helper.setText(R.id.right_promotion_rate,getProportion(item.theme_goods_list[1].promotion_rate.toLong(),item.theme_goods_list[1].min_group_price.toLong()))
        }*/
    }
}