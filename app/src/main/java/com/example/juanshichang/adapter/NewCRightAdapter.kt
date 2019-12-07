package com.example.juanshichang.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.NewClassifyBean
import com.example.juanshichang.utils.glide.GlideUtil

class NewCRightAdapter :
    BaseQuickAdapter<NewClassifyBean.Data, BaseViewHolder>(R.layout.item_two_grid) {
    override fun convert(helper: BaseViewHolder?, item: NewClassifyBean.Data?) {
        helper?.setText(R.id.gTit, item?.name)
        helper?.setGone(R.id.gIv, true)
            ?.setGone(R.id.gIv2, false)
        GlideUtil.loadImage(mContext, item?.image, helper?.getView(R.id.gIv), 2)
    }
}