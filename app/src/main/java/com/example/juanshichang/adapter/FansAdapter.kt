package com.example.juanshichang.adapter

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.bean.FansBean
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.glide.GlideUtil
import com.example.juanshichang.utils.glide.StaggeredBitmapTransform
import java.lang.Exception

class FansAdapter (layouts: Int) : BaseQuickAdapter<FansBean.Data, BaseViewHolder>(layouts){
    override fun convert(helper: BaseViewHolder?, item: FansBean.Data?) {
        helper?.setText(R.id.fansPhone,Util.getPhoneNTransition(item?.mobile!!))
            ?.setText(R.id.fansDate,Util.getTimedate(item.join_time.toLong()))
            ?.setText(R.id.fansFSum,"已推0人")
        if(!TextUtils.isEmpty(item?.avatar)){
            GlideUtil.loadHeadImage(mContext,item?.avatar!!,helper!!.getView(R.id.fansIv))
        }else{
            helper?.setImageResource(R.id.fansIv,R.drawable.head_img_def)
        }
    }
}