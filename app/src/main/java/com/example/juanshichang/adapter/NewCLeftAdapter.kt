package com.example.juanshichang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.bean.NewClassifyBean
import com.zhy.autolayout.AutoRelativeLayout
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.textColorResource

/**
 * @作者: yzq
 * @创建日期: 2019/12/7 16:07
 * @文件作用:
 */
class NewCLeftAdapter(context:Context):BaseAdapter(){
    private var mContext:Context? = null
    private var mList:List<NewClassifyBean.Data>? = null
    private var isSelected:Int = 0
    init {
        this.mContext = context
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder:ViewHolder
        val v:View
        if(convertView == null){
            holder = ViewHolder()
//            v = View.inflate(mContext, R.layout.m_list_item,null)
            v = LayoutInflater.from(mContext).inflate(R.layout.m_list_item,parent,false)
            holder.aR= v.findViewById(R.id.itemR)  //相对布局
            holder.tv = v.findViewById(R.id.textList)
            holder.v = v.findViewById(R.id.leftV)
            //设置Tag
            v.tag = holder
        }else{
            v = convertView
            //获取 tag 并 强转
            holder = v.tag as ViewHolder
        }
        val selectParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        selectParams.setMargins(1,1,0,0)
        val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        params.setMargins(1,0,1,0)
        if(position == isSelected){
            holder.aR?.backgroundColorResource = R.color.white
            holder.tv?.textColorResource = R.color.listcolor
            holder.v?.visibility = View.VISIBLE
//            holder.aR?.layoutParams = selectParams
        }else{
            holder.aR?.backgroundColorResource = R.color.ff_back
            holder.tv?.textColorResource = R.color.main_text
            holder.v?.visibility = View.INVISIBLE
//            holder.aR?.layoutParams = params
        }
        mList?.let {
            holder.tv?.text = it[position].name
        }
        return v
    }

    override fun getItem(position: Int): Any {
        if(mList == null)
            return 0
        return  mList!![position]
    }

    override fun getItemId(position: Int): Long {
        return  position.toLong()
    }

    override fun getCount(): Int {
        if(mList == null)
            return  0
        return  mList!!.size
    }
    private class ViewHolder(){
        var aR: AutoRelativeLayout? = null
        var v:View? = null
        var tv: TextView? = null
    }
    fun setSelect(index:Int){
        this.isSelected = index
        notifyDataSetChanged()
    }
    fun getSelect():Int{
        return  this.isSelected
    }
    fun setNewData(list:List<NewClassifyBean.Data>?){
        this.mList = list
        notifyDataSetChanged()
    }
}