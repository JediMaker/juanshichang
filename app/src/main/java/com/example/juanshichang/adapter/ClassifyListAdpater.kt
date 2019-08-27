package com.example.juanshichang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.juanshichang.R
import com.example.juanshichang.bean.TabOneBean
import com.zhy.autolayout.AutoRelativeLayout
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.textColorResource

class ClassifyListAdpater(context:Context) : BaseAdapter() {
    var data:List<TabOneBean.Category>? = ArrayList()
    var context:Context? = null
    var selected:Int = 0
    init {
        this.context = context
    }
    override fun getView(p: Int, convertView: View?, vp: ViewGroup?): View {
        var holder:ViewHolder? = null
        //重用View  Kotlin 与 Java 覆写的区别
        var v:View
        if(convertView == null){
//            convertView = View.inflate(context, R.layout.m_list_item,null)
            holder = ViewHolder()
            v = LayoutInflater.from(context).inflate(R.layout.m_list_item,vp,false)
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
        //https://blog.csdn.net/weixin_37940567/article/details/78483648
        val selectParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        selectParams.setMargins(1,1,0,0)
        val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        params.setMargins(1,0,1,0)
        if(p == selected){
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
        holder.tv?.text = data!![p].name
        return v
    }

    override fun getItem(p: Int): Any {
        return data!![p]
    }

    override fun getItemId(p: Int): Long {
        return  p.toLong()
    }

    override fun getCount(): Int {
        return data?.size!!
    }

    public fun setSelect(index:Int){
        selected = index
    }

    private class ViewHolder(){
        var aR:AutoRelativeLayout? = null
        var v:View? = null
        var tv:TextView? = null
    }
    public fun setDatas(datas:List<TabOneBean.Category>){
        this.data = datas
        notifyDataSetChanged()
    }
}