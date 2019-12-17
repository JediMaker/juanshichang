package com.example.juanshichang.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.OrdersBean
import com.example.juanshichang.bean.OrdersBeanT
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.glide.GlideUtil
import org.jetbrains.anko.textColorResource

class OrdersAdapter() :
    BaseQuickAdapter<OrdersBeanT.Data, BaseViewHolder>(R.layout.item_orders) {//OrdersBean.Data

    override fun convert(help: BaseViewHolder?, data: OrdersBeanT.Data?) {
        val status = data?.status
        val ordersStateOne = help?.getView<TextView>(R.id.ordersStateOne)
        help?.setGone(R.id.botT, false)  //待付款页面 隐藏
        when (status) {
            //等待付款
            "已提交" -> {
                ordersStateOne?.textColorResource = R.color.indicatorRed  //红色
                ordersStateOne?.text = "等待付款"
                help?.setGone(R.id.botT, true)
                help?.setText(R.id.oPrices, data.total)
                help?.addOnClickListener(R.id.goPay)  //设置支付按钮点击事件
            }
            "退货中" -> {
                ordersStateOne?.textColorResource = R.color.indicatorRed  //红色
                ordersStateOne?.text = "退货中"
            }
            "已付款", "已发货", "已收货" -> {
                ordersStateOne?.textColorResource = R.color.orders_state  //绿色
                ordersStateOne?.text = status
            }
            "已取消" -> {
                ordersStateOne?.textColorResource = R.color.home_gray  //灰色
                ordersStateOne?.text = "已失效"
            }
            else -> {
                ordersStateOne?.textColorResource = R.color.orders_state  //绿色
            }
        }
        help?.addOnClickListener(R.id.orderGo) //设置查看订单详情
        //设置公共的参数
        help?.setText(R.id.createDate, data?.date_added)
            ?.setText(R.id.ordersTitle, data?.products!![0].name)
            ?.setText(R.id.ordersPrice, data.total)
        val iv = help?.getView<ImageView>(R.id.ordersImage)
        GlideUtil.loadShopImg(mContext, data?.products!![0].image, iv, iv?.drawable)
        /*help?.setText(R.id.createDate,Util.getTimedate(data?.create_time!!.toLong()))
            ?.setText(R.id.ordersTitle,data?.goods_name)
            ?.setText(R.id.ordersPrice,"￥"+Util.getFloatPrice(data?.order_amount!!.toLong()))
            ?.setText(R.id.ordersZq,"赚佣"+Util.getFloatPrice(data?.promotion_amount.toLong())+"元") //待运算
            ?.setText(R.id.ordersNum,data?.order_sn)
        GlideUtil.loadImage(mContext,data?.goods_thumbnail_url,help?.getView(R.id.ordersImage))
        help?.setText(R.id.ordersStateOne,data?.order_status_desc)*/
    }
}