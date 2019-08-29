package com.example.juanshichang.adapter

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.TabOneBean
import com.example.juanshichang.utils.glide.GlideUtil

class TwoGridAdapterT(data:List<TabOneBean.Category>) :
    BaseQuickAdapter<TabOneBean.Category, BaseViewHolder>(R.layout.item_two_grid,data){

    override fun convert(helper: BaseViewHolder?, item: TabOneBean.Category?) {
        helper?.setText(R.id.gTit,item?.name)
        if(item?.image.equals("all") && item?.category_id == Int.MAX_VALUE){
            helper?.setGone(R.id.gIv, false)
                ?.setGone(R.id.gIv2, true)
            GlideUtil.loadImage(mContext,R.drawable.all,helper?.getView(R.id.gIv2),0)
        }else{
            helper?.setGone(R.id.gIv, true)
                ?.setGone(R.id.gIv2, false)
            GlideUtil.loadImage(mContext,item?.image,helper?.getView(R.id.gIv),2)
        }
    }
}