package com.example.juanshichang.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.OrdersBean
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.glide.GlideUtil

class OrdersAdapter(layouts: Int) : BaseQuickAdapter<OrdersBean.Data, BaseViewHolder>(layouts) {

    override fun convert(help: BaseViewHolder?, data: OrdersBean.Data?) {
        help?.setText(R.id.createDate,Util.getTimedate(data?.create_time!!.toLong()))
            ?.setText(R.id.ordersTitle,data?.goods_name)
            ?.setText(R.id.ordersPrice,"￥"+Util.getFloatPrice(data?.goods_price!!.toLong()))
            ?.setText(R.id.ordersZq,"赚佣"+data?.promotion_rate+"元") //待运算
            ?.setText(R.id.ordersNum,data?.order_sn)
        GlideUtil.loadImage(mContext,data?.goods_thumbnail_url,help?.getView(R.id.ordersImage))
    }
}