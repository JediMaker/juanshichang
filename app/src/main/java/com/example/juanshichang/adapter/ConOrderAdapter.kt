package com.example.juanshichang.adapter

import android.graphics.Color
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.ConOrderBean
import com.example.juanshichang.utils.glide.GlideUtil
/**
 * @作者: yzq
 * @创建日期: 2019/12/18 17:32
 * @文件作用: 与 OrderDetailAdapter 共用布局
 */
class ConOrderAdapter : BaseQuickAdapter<ConOrderBean.Product, BaseViewHolder>(R.layout.item_sub_order_o){
    private var fatData:ConOrderBean.ConOrderBeans? = null
    override fun convert(helper: BaseViewHolder?, item: ConOrderBean.Product?) {
        var result=""
        helper?.setText(R.id.cargoTit,item?.name)
            ?.setText(R.id.cargoPrice,item?.price)
            ?.setText(
                R.id.carOptionValue, "${

                if (item?.option?.size!! > 0) {
                    for (goosOption in item?.option!!) {
                        result += goosOption.value + " "
                    }
                    result
                } else {
                    ""
                }
                }"
            )            ?.setText(R.id.iQuantity,"×${item?.quantity}")
            ?.setText(R.id.endContent,item?.total)
        val iv = helper?.getView<ImageView>(R.id.cargoImg)
        GlideUtil.loadShopImg(
            mContext,
            item?.thumb,
            iv,
            iv?.drawable
        )

    }

    fun setMyData(myData:ConOrderBean.ConOrderBeans){
        this.fatData = myData
        val data = fatData?.data?.products
        setNewData(data)
    }
}