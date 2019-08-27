package com.example.juanshichang.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.TabOneBean
import com.example.juanshichang.utils.glide.GlideUtil

class TwoGridAdapterT(data:List<TabOneBean.Category>) :
    BaseQuickAdapter<TabOneBean.Category, BaseViewHolder>(R.layout.item_two_grid,data){

    override fun convert(helper: BaseViewHolder?, item: TabOneBean.Category?) {
        helper?.setText(R.id.gTit,item?.name)
        GlideUtil.loadImage(mContext,item?.image,helper?.getView(R.id.gIv),0)
    }
}