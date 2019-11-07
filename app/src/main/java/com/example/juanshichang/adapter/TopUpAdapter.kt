package com.example.juanshichang.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.TopUpBean

class TopUpAdapter : BaseQuickAdapter<TopUpBean, BaseViewHolder>(R.layout.topup_money_item){
    private var selected  = 0
    override fun convert(helper: BaseViewHolder?, item: TopUpBean?) {
        if(helper?.adapterPosition == selected){ //设置选中的颜色 布局等
            helper.setTextColor(R.id.topPrice,mContext.resources.getColor(R.color.indicatorRed))
                .setTextColor(R.id.botPrice,mContext.resources.getColor(R.color.indicatorRed))
                .setBackgroundRes(R.id.itemTopUp,R.drawable.bg_topup_red)
        }else{
            helper?.setTextColor(R.id.topPrice,mContext.resources.getColor(R.color.main_text))
                ?.setTextColor(R.id.botPrice,mContext.resources.getColor(R.color.txt_gray))
                ?.setBackgroundRes(R.id.itemTopUp,R.color.ff_back)
        }
        if(!item?.botPrice!!.isEmpty()){//有优惠的产品处理
            helper?.setText(R.id.topPrice,item.topPrice)
                ?.setText(R.id.botPrice,item.botPrice)
                ?.setGone(R.id.botPrice,true)
        }else{//没有优惠
            helper?.setText(R.id.topPrice,item.topPrice)
                ?.setGone(R.id.botPrice,false)
        }
    }
    fun setNewSelect(index:Int){
        if(index != selected){
            selected = index
            notifyDataSetChanged()
        }
    }
    fun setMeNewData(data:List<TopUpBean>){
        replaceData(data)
        selected = 0
        notifyDataSetChanged()
    }
}