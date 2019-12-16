package com.example.juanshichang.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.ZySearchBean
import com.example.juanshichang.utils.glide.GlideUtil
/**
 * @作者: yzq
 * @创建日期: 2019/12/16 14:48
 * @文件作用: 与ZyAllAdapter 共用 相同item
 */
class ZySearchAdapter : BaseQuickAdapter<ZySearchBean.Product, BaseViewHolder>(R.layout.item_zyall_goods)  {
    override fun convert(helper: BaseViewHolder?, item: ZySearchBean.Product?) {
        helper?.setText(R.id.zyTitle,item?.name)
            ?.setText(R.id.zyPrice,item?.price)
        GlideUtil.loadImage(mContext,item?.thumb,helper!!.getView(R.id.zyIv),2)
    }
}