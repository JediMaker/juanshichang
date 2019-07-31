package com.example.juanshichang.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.BannnerDetailBean
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.glide.GlideUtil

class PromotionListAdapter(layoutResId: Int, data: MutableList<BannnerDetailBean.Goods>?) :
    BaseQuickAdapter<BannnerDetailBean.Goods, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder?, item: BannnerDetailBean.Goods?) {
        helper?.setText(R.id.mTypeNumTv, "¥"+ Util.getFloatPrice(item!!.min_group_price.toLong())) // 现价
            ?.setText(R.id.mTypeNumTvs, "¥"+ Util.getFloatPrice(item?.min_normal_price.toLong()))  //原价
            ?.setText(R.id.mTypeIV, item!!.goods_name)
        GlideUtil.loadImage(mContext,item?.goods_thumbnail_url,helper!!.getView(R.id.mTypeImg))
    }

}