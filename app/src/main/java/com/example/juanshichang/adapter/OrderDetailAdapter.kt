package com.example.juanshichang.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.ConOrderBean
import com.example.juanshichang.bean.OrderDetailBean
import com.example.juanshichang.utils.glide.GlideUtil

/**
 * @作者: yzq
 * @创建日期: 2019/12/18 17:33
 * @文件作用:与 ConOrderAdapter 共用布局
 */
class OrderDetailAdapter : BaseQuickAdapter<OrderDetailBean.Product, BaseViewHolder>(R.layout.item_sub_order_o){
    override fun convert(helper: BaseViewHolder?, item: OrderDetailBean.Product?) {
        helper?.setText(R.id.cargoTit,item?.name)
            ?.setText(R.id.cargoPrice,item?.price)
            ?.setText(R.id.iQuantity,"×${item?.quantity}")
            ?.setText(R.id.endContent,item?.total)
        val iv = helper?.getView<ImageView>(R.id.cargoImg)
        GlideUtil.loadShopImg(
            mContext,
            item?.image,
            iv,
            iv?.drawable
        )

    }
}