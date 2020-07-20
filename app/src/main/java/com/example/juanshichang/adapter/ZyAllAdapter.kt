package com.example.juanshichang.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.ZyAllBean
import com.example.juanshichang.utils.glide.GlideUtil
/**
 * @作者: yzq
 * @创建日期: 2019/12/7 17:59
 * @文件作用: 此 adapter 与 NewHomeSonAdapter 公用布局
 */
class ZyAllAdapter : BaseQuickAdapter<ZyAllBean.Product, BaseViewHolder>(R.layout.item_zyall_goods)  {
    override fun convert(helper: BaseViewHolder?, item: ZyAllBean.Product?) {
        helper?.setText(R.id.zyTitle,item?.name)
            ?.setText(R.id.zyPrice,item?.price)
        GlideUtil.loadImage(mContext,item?.thumb,helper!!.getView(R.id.zyIv),6)
    }
}