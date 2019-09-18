package com.example.juanshichang.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.TakeOutBean
import com.example.juanshichang.utils.Util

class TakeOutAdapter() : BaseQuickAdapter<TakeOutBean.Withdraw, BaseViewHolder>(R.layout.item_txxq) {
    override fun convert(helper: BaseViewHolder?, item: TakeOutBean.Withdraw?) {
        if(item?.type.equals("ali_pay")){
            helper?.setText(R.id.io,"支付宝")
        }else{
            helper?.setText(R.id.io,"未知")
        }
        if (item?.is_success.equals("1")){
            helper?.setText(R.id.it,"成功")
        }
        helper?.setText(R.id.ith,"¥${item?.amount}")
        helper?.setText(R.id.ifour, Util.getPhoneNTransition(item?.account!!))
    }
}