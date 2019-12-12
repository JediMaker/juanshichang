package com.example.juanshichang.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.HomeBean
import com.example.juanshichang.bean.ZyAllBean
import com.example.juanshichang.utils.glide.GlideUtil
/**
 * @作者: yzq
 * @创建日期: 2019/12/12 17:30
 * @文件作用: 此 adapter 与 ZyAllAdapter 公用布局
 */
class NewHomeSonAdapter: BaseQuickAdapter<HomeBean.Date, BaseViewHolder>(R.layout.item_zyall_goods)  {
    override fun convert(helper: BaseViewHolder?, item: HomeBean.Date?) {
        helper?.setText(R.id.zyTitle,item?.name)
            ?.setText(R.id.zyPrice,item?.price)
        GlideUtil.loadImage(mContext,item?.thumb,helper!!.getView(R.id.zyIv),2)
    }
}