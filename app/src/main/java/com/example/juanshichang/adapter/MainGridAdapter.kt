package com.example.juanshichang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.juanshichang.R
import com.example.juanshichang.bean.GridItemBean

class MainGridAdapter(context: Context, data: List<GridItemBean.Channel>) : BaseAdapter() {
    var context: Context? = null
    var datas: List<GridItemBean.Channel>? = null
    var textList = mutableListOf<String>("热销榜","收益榜","1.9包邮","今日爆款","品牌清仓","限时秒杀","充值中心","电器城"," "," "," "," ")
    init {
        this.context = context
        this.datas = data
    }

    override fun getView(p: Int, v: View?, vg: ViewGroup?): View {
        var view = v
        var holder:ViewHolder? = null
        if(v == null){
            view =LayoutInflater.from(context).inflate(R.layout.item_main_gridview,vg,false)
            holder = ViewHolder()
            holder.tv = view.findViewById(R.id.item_tv)
            holder.iv = view.findViewById(R.id.item_iv)
            view.tag = holder
        }else{
            holder = (view?.tag) as ViewHolder
        }
        holder.tv.text = textList[p]
        when(p){
            0->{
                Glide.with(context!!).load(datas!![p].image_url)
                    .placeholder( R.drawable.m1) //加载成功前显示的图片
                    .fallback( R.drawable.m1) //url为空的时候,显示的图片
                    .into(holder.iv)
            }
            1->{
                Glide.with(context!!).load(datas!![p].image_url)
                    .placeholder( R.drawable.m2) //加载成功前显示的图片
                    .fallback( R.drawable.m2) //url为空的时候,显示的图片
                    .into(holder.iv)
            }
            2->{
                Glide.with(context!!).load(datas!![p].image_url)
                    .placeholder( R.drawable.m3) //加载成功前显示的图片
                    .fallback( R.drawable.m3) //url为空的时候,显示的图片
                    .into(holder.iv)
            }
            3->{
                Glide.with(context!!).load(datas!![p].image_url)
                    .placeholder( R.drawable.m4) //加载成功前显示的图片
                    .fallback( R.drawable.m4) //url为空的时候,显示的图片
                    .into(holder.iv)
            }
            4->{
                Glide.with(context!!).load(datas!![p].image_url)
                    .placeholder( R.drawable.m5) //加载成功前显示的图片
                    .fallback( R.drawable.m5) //url为空的时候,显示的图片
                    .into(holder.iv)
            }
            5->{
                Glide.with(context!!).load(datas!![p].image_url)
                    .placeholder( R.drawable.m6) //加载成功前显示的图片
                    .fallback( R.drawable.m6) //url为空的时候,显示的图片
                    .into(holder.iv)
            }
            6->{
                Glide.with(context!!).load(datas!![p].image_url)
                    .placeholder( R.drawable.m7) //加载成功前显示的图片
                    .fallback( R.drawable.m7) //url为空的时候,显示的图片
                    .into(holder.iv)
            }
            7->{
                Glide.with(context!!).load(datas!![p].image_url)
                    .placeholder( R.drawable.m8) //加载成功前显示的图片
                    .fallback( R.drawable.m8) //url为空的时候,显示的图片
                    .into(holder.iv)
            }
            else ->{

                Glide.with(context!!).load(datas!![p].image_url)
                    .into(holder.iv)
            }
        }

        return view!!
    }

    override fun getItem(p: Int): Any? {
        return datas!![p]
    }

    override fun getItemId(p: Int): Long {
        return p.toLong()
    }

    override fun getCount(): Int {
        return datas!!.size
    }

    inner class ViewHolder {
        lateinit var iv: ImageView
        lateinit var tv: TextView
    }
}