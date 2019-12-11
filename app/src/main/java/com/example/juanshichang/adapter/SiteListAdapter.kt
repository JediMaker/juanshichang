package com.example.juanshichang.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.OrdersBean
import com.example.juanshichang.bean.SiteBean

/**
 * @作者: yzq
 * @创建日期: 2019/11/27 18:18
 * @文件作用: 收货地址
 */
class SiteListAdapter() : BaseQuickAdapter<SiteBean.Addresse, BaseViewHolder>(R.layout.item_site_list){
    override fun convert(helper: BaseViewHolder?, item: SiteBean.Addresse?) {
        val name = item?.name
        var oText = ""
        if(!TextUtils.isEmpty(name)){
            oText = name?.substring(0,1).toString()
        }
        helper?.setText(R.id.siteName,name)
            ?.setText(R.id.siteX,oText)
            ?.setText(R.id.siteDet,"${item?.city}  ${item?.address_detail}")
            ?.setText(R.id.sitePhone,item?.iphone)
        helper?.addOnClickListener(R.id.siteEdit) //给 编辑 按钮添加点击事件
        helper?.addOnClickListener(R.id.allCon) //给 选择收货地址 添加点击事件
    }
}