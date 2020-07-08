package com.example.juanshichang.adapter

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.activity.OrderDetailActivity
import com.example.juanshichang.activity.SettleAccActivity
import com.example.juanshichang.bean.OrdersBeanT
import com.example.juanshichang.utils.glide.GlideUtil
import kotlinx.android.synthetic.main.activity_order_detail.*
import org.jetbrains.anko.textColorResource

class OrdersAdapter() :
    BaseQuickAdapter<OrdersBeanT.Data, BaseViewHolder>(R.layout.item_orders) {//OrdersBean.Data

    override fun convert(help: BaseViewHolder?, data: OrdersBeanT.Data?) {
        val status = data?.status
        val ordersStateOne = help?.getView<TextView>(R.id.ordersStateOne)
        val goPayOrDelete = help?.getView<TextView>(R.id.goPay)
        val oDList = help?.getView<RecyclerView>(R.id.oDList)
        help?.setGone(R.id.botT, true)  //待付款页面页面 隐藏
        help?.setGone(R.id.botO, false)  //订单编号页面 隐藏
        help?.setGone(R.id.goPay, false)//立即支付按钮 隐藏
        help?.setGone(R.id.ensureReceived, false)  //确认收货按钮 隐藏
        var oDAdapter = OrderListAdapter()
        oDList?.adapter = oDAdapter
        goPayOrDelete?.text = "立即支付"
        oDAdapter?.setNewData(data?.products)
        oDAdapter.setOnItemChildClickListener(object : BaseQuickAdapter.OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                when (view?.id) {
                    R.id.itemCon
                    -> { //查看订单详情
                        data?.let {
                            val intent =
                                Intent(mContext, OrderDetailActivity::class.java)
                            intent.putExtra("orderid", data.order_id)
                            mContext.startActivity(intent)

                        }
                    }
                }
            }
        })
        when (status) {
            //等待付款
            "已提交" -> {
                ordersStateOne?.textColorResource = R.color.indicatorRed  //红色
                ordersStateOne?.text = "等待付款"
                help?.setGone(R.id.botT, true)
                help?.setVisible(R.id.goPay, true)
                help?.setText(R.id.oPrices, data.total)
                help?.addOnClickListener(R.id.goPay)  //设置支付按钮点击事件
            }
            "退货中" -> {
                ordersStateOne?.textColorResource = R.color.indicatorRed  //红色
                ordersStateOne?.text = "退货中"
            }
            "已付款", "已收货" -> {
                ordersStateOne?.textColorResource = R.color.orders_state  //绿色
                ordersStateOne?.text = status
            }
            "已发货" -> {
                ordersStateOne?.textColorResource = R.color.orders_state  //绿色
                ordersStateOne?.text = status
                goPayOrDelete?.text = "确认收货"
                help?.setVisible(R.id.goPay, true)
                help?.addOnClickListener(R.id.goPay)  //设置支付按钮点击事件
            }
            "已失效" -> {
                ordersStateOne?.textColorResource = R.color.home_gray  //灰色
                ordersStateOne?.text = "交易关闭"
                goPayOrDelete?.text = "删除订单"
                help?.setVisible(R.id.goPay, true)
                help?.addOnClickListener(R.id.goPay)  //设置支付按钮点击事件
            }
            else -> {
                ordersStateOne?.text = status
                ordersStateOne?.textColorResource = R.color.orders_state  //绿色
            }
        }
        help?.setText(R.id.oPrices, data?.total)
        help?.addOnClickListener(R.id.orderGo) //设置查看订单详情
        help?.addOnClickListener(R.id.ensureReceived) //设置查看订单详情
        //设置公共的参数
        help?.setText(R.id.createDate, data?.date_added)
            ?.setText(R.id.ordersTitle, data?.products!![0].name)
            ?.setText(R.id.ordersPrice, data.total)
            ?.setText(R.id.ordersNum, data.order_num)
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