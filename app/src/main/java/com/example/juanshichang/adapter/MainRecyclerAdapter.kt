package com.example.juanshichang.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.MainRecyclerBean
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.UtilsBigDecimal
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
            .setText(R.id.left_left_label,getStoreName("pdd")) //todo
            .setText(R.id.left_right_label,item.theme_goods_list[0].mall_name)
            .setText(R.id.left_volume,"销量"+item.theme_goods_list[0].sales_tip)
            getIsVisible(helper,item.theme_goods_list[0].has_coupon,item,0)
//        GlideUtil.loadImage(mContexts,item.theme_goods_list[0].goods_thumbnail_url,helper.getView(R.id.left_iv)) //填入左图
        GlideUtil.loadImage(mContexts,item.theme_goods_list[0].goods_thumbnail_url,helper.getView(R.id.left_iv))
        helper.setText(R.id.right_tv_title,item.theme_goods_list[1].goods_name)//填入右图
            .setText(R.id.right_price, Util.getFloatPrice(item.theme_goods_list[1].min_group_price.toLong()))
            .setText(R.id.right_mTypeNumTvs,Util.getFloatPrice(item.theme_goods_list[1].min_normal_price.toLong()))
            .setText(R.id.right_left_label,getStoreName("pdd"))  //todo
            .setText(R.id.right_right_label,item.theme_goods_list[1].mall_name)
            .setText(R.id.right_volume,"销量"+item.theme_goods_list[1].sales_tip)
        getIsVisible(helper,item.theme_goods_list[1].has_coupon,item,1)
        GlideUtil.loadImage(mContexts,item.theme_goods_list[1].goods_thumbnail_url,helper.getView(R.id.right_iv))
//        GlideUtil.loadImage(mContexts,item.theme_goods_list[1].goods_thumbnail_url,helper.getView(R.id.right_iv)) //填入右图
        //设置点击事件
        helper.addOnClickListener(R.id.head_iv)
        helper.addOnClickListener(R.id.r1)
        helper.addOnClickListener(R.id.r2)

    }


    fun getStoreName(s:String):String{ //待处理的方法
        when(s){
            "pdd" -> return "拼多多"
            "jd" -> return "京东"
            "tb" -> return "淘宝"
            else -> return "未知"
        }
    }
    fun getIsVisible(helper: BaseViewHolder,isTrue:Boolean,item: MainRecyclerBean.Theme,type:Int){ //type == 0 左边 else 1 右边
        if(!isTrue){//没有优惠劵
            if(type == 0){
                helper.setVisible(R.id.left_juan,false)
            }else if(type == 1){
                helper.setVisible(R.id.right_juan,false)
            }
        }else{ //填入优惠劵数据
            if(type == 0){
                helper.setText(R.id.left_juan_price,UtilsBigDecimal.div(item.theme_goods_list[0].coupon_discount.toDouble(),100.toDouble(),0).toString()+"元")
            }else if(type == 1){
                helper.setText(R.id.right_juan_price,UtilsBigDecimal.div(item.theme_goods_list[1].coupon_discount.toDouble(),100.toDouble(),0).toString()+"元")
            }
        }
        //填入预估佣金
        if(type == 0){
            helper.setText(R.id.left_promotion_rate,getProportion(item.theme_goods_list[0].promotion_rate.toLong(),item.theme_goods_list[0].min_group_price.toLong()))
        }else if(type == 1){
            helper.setText(R.id.right_promotion_rate,getProportion(item.theme_goods_list[1].promotion_rate.toLong(),item.theme_goods_list[1].min_group_price.toLong()))
        }
    }
    //计算千分之比率 先按照劵后算
    fun getProportion(rate : Long,price :Long):String{
//        val qFo =   UtilsBigDecimal.div(price.toDouble(),1000.toDouble(),3) //先计算出千分之一  保留3位小数
//        val zF =  UtilsBigDecimal.mul(qFo,rate.toDouble(),2)//计算出 总额 单位 /分
        val zf = price/1000*rate
        val prices = Util.getFloatPrice(zf.toLong())
        return "预估收益"+prices+"元"
    }
}