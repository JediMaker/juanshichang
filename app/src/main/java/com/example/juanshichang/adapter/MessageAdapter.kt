package com.example.juanshichang.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.MainRecyclerBean
import com.example.juanshichang.bean.MessageBean
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.glide.GlideUtil

class MessageAdapter () :
    BaseQuickAdapter<MessageBean.Message, BaseViewHolder>(R.layout.item_message_one){

    override fun convert(helper: BaseViewHolder?, item: MessageBean.Message?) {
        helper?.setText(R.id.mesTitle,item?.type)
            ?.setText(R.id.mesData,Util.getTimedateThree(item?.create_time!!.toLong()))
            ?.setText(R.id.botTit,item.title)
            ?.setText(R.id.botDetails,item.content)
        if(item?.type.equals("系统通知")){
            GlideUtil.loadImage(mContext,R.drawable.news_notice,helper?.getView(R.id.mesTopIv),1)
        }else{
            // todo 这里根据情况处理
            GlideUtil.loadImage(mContext,R.drawable.news_notice,helper?.getView(R.id.mesTopIv),1)
        }
    }
}