package com.example.juanshichang.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.ConOrderBean
import com.example.juanshichang.bean.OrderDetailBean
import com.example.juanshichang.bean.OrdersBeanT
import com.example.juanshichang.utils.glide.GlideUtil

/**
 * 历史订单列表
 */
class OrderListAdapter :
    BaseQuickAdapter<OrdersBeanT.Product?, BaseViewHolder>(R.layout.item_sub_order_o) {
    override fun convert(helper: BaseViewHolder?, item: OrdersBeanT.Product?) {
        var result: String = ""
        helper?.setGone(R.id.cargoPrice, true)
        helper?.setGone(R.id.endLayout, true)
        helper?.addOnClickListener(R.id.itemCon) //设置查看订单详情
        helper?.setText(R.id.cargoTit, item?.name)
            ?.setText(
                R.id.carOptionValue, "${

                if (item?.options?.size!! > 0) {
                    for (goosOption in item?.options!!) {
                        result += goosOption.value + " "
                    }
                    result
                } else {
                    ""
                }
                }"
            )
            ?.setText(R.id.iQuantity, "×${item?.quantity}")
        val iv = helper?.getView<ImageView>(R.id.cargoImg)
        val tv = helper?.getView<TextView>(R.id.endTit)
        val viewLinear = helper?.getView<View>(R.id.viewLinear)
        tv?.visibility = View.GONE
        viewLinear?.visibility = View.GONE
        GlideUtil.loadShopImg(
            mContext,
            item?.image,
            iv,
            iv?.drawable
        )

    }

}