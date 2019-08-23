package com.example.juanshichang.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.utils.glide.GlideUtil
import kotlinx.android.synthetic.main.item_shangpin_xiangqing.view.*

class ShangPinXqAdapter(layoutResId: Int,spStr:MutableList<String>):BaseQuickAdapter<String,BaseViewHolder>(layoutResId,spStr){
    override fun convert(viewHolder: BaseViewHolder?, str: String?) {
        GlideUtil.loadImage(mContext,str,viewHolder?.getView(R.id.sPIv),2) //设置高清图ARGB_8888
    }
}