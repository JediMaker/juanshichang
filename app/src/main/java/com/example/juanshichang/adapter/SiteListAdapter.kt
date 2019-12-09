package com.example.juanshichang.adapter

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
        helper?.setText(R.id.siteName,"哈哈哈")
            ?.setText(R.id.siteX,"哈")
            ?.setText(R.id.siteDet,"${item?.city}  ${item?.address_detail}")
            ?.setText(R.id.sitePhone,item?.iphone)
        helper?.addOnClickListener(R.id.siteEdit) //给 编辑 按钮添加点击事件
    }
}