package com.example.juanshichang.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.juanshichang.R
import com.example.juanshichang.bean.CartBean

/**
 * @作者: yzq
 * @创建日期: 2019/11/16 16:04
 * @文件作用: 购物车适配器
 * ck: https://my.oschina.net/longxuanzhigu/blog/3063650
 */
class ShoppingCarAdapter : BaseExpandableListAdapter{
    private var context:Context? = null
    private var isSelectAll:CheckBox? = null //全选
    private var editor:TextView? = null //编辑
    private var total:TextView? = null  //合计
    private var goPay:TextView? = null  //去结算
    private var data:CartBean.CartBeans? = null
    constructor(context:Context,isSelectAll:CheckBox,editor:TextView,total:TextView,goPay:TextView){
        this.context = context
        this.isSelectAll = isSelectAll
        this.editor = editor
        this.total = total
        this.goPay = goPay
    }

    /**
     * 自定义设置数据方法；
     * 通过notifyDataSetChanged()刷新数据，可保持当前位置
     * @param data 需要刷新的数据
     */
    fun setData(data:CartBean.CartBeans?){
        this.data = data
        notifyDataSetChanged()
    }
    override fun getGroupCount(): Int {
        return data?.data?.products?.size?: 0
    }
    override fun getGroup(p: Int): Any {
        return data?.data?.products?.get(p)!!
    }
    override fun getGroupId(p: Int): Long {
        return  p.toLong()
    }
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val groupViewHolder:GroupViewHolder
        val v:View
        if(convertView == null){
            v = View.inflate(context, R.layout.item_shopping_car_group,null)
            groupViewHolder = GroupViewHolder(v)
            v.tag = groupViewHolder //设置Tag
        }else{
            v = convertView
            groupViewHolder = v.tag as GroupViewHolder
        }
        //这里进行顶部操作逻辑...
        return  v
    }
    class GroupViewHolder{
        var iv_select:ImageView? = null
        var tv_store_name:TextView? = null
        var tv_on_sale:TextView? = null
        constructor(view:View){
            iv_select = view.findViewById(R.id.iv_select)
            tv_store_name = view.findViewById(R.id.tv_store_name)
            tv_on_sale = view.findViewById(R.id.tv_on_sale)
        }
    }
    override fun getChildrenCount(p0: Int): Int {
        return  0
    }

    override fun getChild(p0: Int, p1: Int): Any {
        return  0
    }
    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return  false
    }

    override fun hasStableIds(): Boolean {
        return  false
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        return  View(context)
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return 0
    }

}