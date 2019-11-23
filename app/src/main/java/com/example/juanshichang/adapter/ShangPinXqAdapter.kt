package com.example.juanshichang.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.utils.glide.GlideUtil
import kotlinx.android.synthetic.main.item_shangpin_xiangqing.view.*

class ShangPinXqAdapter():BaseQuickAdapter<String,BaseViewHolder>(R.layout.item_shangpin_xiangqing){
    override fun convert(viewHolder: BaseViewHolder?, str: String?) {
        GlideUtil.loadImage(mContext,str,viewHolder?.getView(R.id.sPIv),2) //设置高清图ARGB_8888
    }
}