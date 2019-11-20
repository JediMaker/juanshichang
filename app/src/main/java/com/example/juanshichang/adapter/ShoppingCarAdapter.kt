package com.example.juanshichang.adapter

import android.content.Context
import android.graphics.Color
import android.icu.text.DecimalFormat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.juanshichang.R
import com.example.juanshichang.bean.CartBean
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.UtilsBigDecimal
import com.example.juanshichang.utils.glide.GlideUtil

/**
 * @作者: yzq
 * @创建日期: 2019/11/16 16:04
 * @文件作用: 购物车适配器
 * ck: https://my.oschina.net/longxuanzhigu/blog/3063650
 *  https://blog.csdn.net/qq941263013/article/details/80901277
 */
class ShoppingCarAdapter : BaseExpandableListAdapter{
    private var context: Context? = null
    private var llSelectAll: LinearLayout? = null //全选
    private var isSelectAll: CheckBox? = null //全选按钮
    private var editor: TextView? = null //编辑
    private var total: TextView? = null  //合计
    private var goPay: TextView? = null  //去结算
    private var data: CartBean.CartBeans? = null
    private var allSelect: Boolean = false //全选

    constructor(
        context: Context?,
        llSelectAll: LinearLayout?,
        isSelectAll: CheckBox?,
        editor: TextView?,
        total: TextView?,
        goPay: TextView?
    ) {
        this.context = context
        this.llSelectAll = llSelectAll
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
    fun setData(data: CartBean.CartBeans?) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getGroupCount(): Int {
//        return data?.data?.products?.size ?: 0
        if (data?.data?.products?.size != 0) {
            return 1
        }
        return 0
    }

    override fun getGroup(p: Int): Any {
//        return data?.data?.products?.get(p)!!
        return data?.data?.products!!
    }

    override fun getGroupId(p: Int): Long {
        return p.toLong()
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val groupViewHolder: GroupViewHolder
        val v: View
        if (convertView == null) {
            v = View.inflate(context, R.layout.item_shopping_car_group, null)
            groupViewHolder = GroupViewHolder(v)
            v.tag = groupViewHolder //设置Tag
        } else {
            v = convertView
            groupViewHolder = v.tag as GroupViewHolder
        }
        //这里进行顶部操作逻辑...
        //todo  因为后台分配的数据结构问题  这里只能先写死...
        val fatherGroup = data?.data?.products
        fatherGroup?.let{
            //设置标题
            groupViewHolder.tv_store_name?.text = "萌象自营"

            //店铺内的商品都选中的时候，店铺的也要选中
            for (i in 0..it.size) {
                val isSelect = it[i].isSelect
                if (isSelect) {
                    //这里 应该给 父类Group 以 选中参数 因为暂时没有数据 -- 直接就选中
                    groupViewHolder.iv_select?.isSelected = true
                } else {
                    groupViewHolder.iv_select?.isSelected = false
                    break
                }
            }
            //店铺是否在购物车中被选中  --- 因为 没有父类Group数据  故放弃此段逻辑

            //店铺选择框的点击事件
            groupViewHolder.ll?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val select: Boolean = groupViewHolder.iv_select?.isSelected!!
                    groupViewHolder.iv_select?.isSelected = !select
                    //数据结构因素 ....
                    for (i in 0..it.size) {
                        it[i].isSelect = !select
                    }
                    notifyDataSetChanged()
                }
            })
            //当所有的选择框都是选中的时候，全选也要选中 todo --- 数据结构原因 逻辑改变
            for (i in 0..it.size) {
                val isSelect = it[i].isSelect
                if (isSelect) {
                    allSelect = true
                    isSelectAll?.isChecked = true
                } else {
                    allSelect = false
                    isSelectAll?.isChecked = false
                    break
                }
            }

            //全选的点击事件
            llSelectAll?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    allSelect = !allSelect
                    if (allSelect) {
                        for (i in 0..it.size) {
                            it[i].isSelect = true
                        }
                    } else {
                        for (i in 0..it.size) {
                            it[i].isSelect = false
                        }
                    }
                    notifyDataSetChanged()
                }
            })
            //合计的计算
            var total_price: Double = 0.0
            total?.text = getGaudyStr("¥0.00")
            for (i in 0..it.size) {
                val isSelect = it[i].isSelect
                if (isSelect) {
                    val num = it[i].quantity
                    val price = it[i].price
                    val v: Double = num.toDouble()
                    val v1: Double = price.toDouble()
                    total_price = total_price + UtilsBigDecimal.mul(v, v1, 2)
                    //让Double类型完整显示，不用科学计数法显示大写字母E
//                    val decimalFormat = DecimalFormat("0.00")
                    total?.text = "¥$total_price"
                }
            }
            //去结算的点击事件
            goPay?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    //创建临时的List，用于存储被选中的商品
                    val temp = ArrayList<CartBean.Product>()
                    for (i in 0..it.size) {
                        val isSelect = it[i].isSelect
                        if (isSelect) {
                            temp.add(it[i])
                        }
                    }
                    if (temp.size != 0) {
                        ToastUtil.showToast(context!!, "跳转到确认订单页面，完成后续订单流程 ${temp.size}")
                    } else {
                        ToastUtil.showToast(context!!, "请选择要购买的商品")
                    }
                }
            })
            //删除的点击事件
        }
        if(fatherGroup == null || fatherGroup?.size == 0){
            v.visibility = View.GONE
        }
        return v
    }

    class GroupViewHolder {
        var iv_select: ImageView? = null
        var tv_store_name: TextView? = null
        var tv_on_sale: TextView? = null
        var ll: LinearLayout? = null

        constructor(view: View) {
            iv_select = view.findViewById(R.id.iv_select)
            tv_store_name = view.findViewById(R.id.tv_store_name)
            tv_on_sale = view.findViewById(R.id.tv_on_sale)
            ll = view.findViewById(R.id.ll)
        }
    }

    //子布局...
    override fun getChildrenCount(p: Int): Int {
        return data?.data?.products?.size ?: 0
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return data?.data?.products?.get(childPosition)!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val childViewHolder: ChildViewHolder
        val v: View
        if (convertView == null) {
            v = View.inflate(context, R.layout.item_shopping_car_child, null)
            childViewHolder = ChildViewHolder(v)
            v.tag = childViewHolder
        } else {
            v = convertView
            childViewHolder = v.tag as ChildViewHolder
        }
        val fatherGroup = data?.data?.products
        fatherGroup.let {
            val goodsBean: CartBean.Product = it!![childPosition]
            //是否有货
            val isStock: Boolean = goodsBean.stock
            //商品图片
            val goods_image: String = goodsBean.thumb
            //商品ID
            val goods_id: String = goodsBean.cart_id
            //商品名称
            val goods_name: String = goodsBean.name
            //商品价格
            val goods_price: String = goodsBean.price
            //商品数量
            val goods_num: String = goodsBean.quantity
            //商品是否被选中
            val isSelect: Boolean = goodsBean.isSelect
            GlideUtil.loadImage(context, goods_image, childViewHolder.cargoImg, 0)
            childViewHolder.cargoTit?.text = goods_name ?: ""
            childViewHolder.cargoPrice?.text = goods_price ?: ""
            if (!isStock) {
                childViewHolder.llAmount?.visibility = View.INVISIBLE
                childViewHolder.iv_select?.isSelected = false
                childViewHolder.iv_select?.isEnabled = false  //设置无货商品 不可选中 todo 注意编辑模式 释放该权限
            } else {
                childViewHolder.amount?.text = goods_num ?: "1"
                childViewHolder.minusAmount?.isEnabled = false
                childViewHolder.iv_select?.isSelected = isSelect  //设置是否选中
            }
            //商品选择框的点击事件
            childViewHolder.iv_select?.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    goodsBean.isSelect = !isSelect
                    //这里 进行 父布局 变更 状态 变更
                    if(!isSelect == false){
                        //设置 父布局 选中态为  false
                    }
                    notifyDataSetChanged()
                }
            })
            //加号的点击事件
            childViewHolder.addAmount?.setOnClickListener(object :View.OnClickListener{
                override fun onClick(p0: View?) {
                    if(childViewHolder.minusAmount?.isEnabled == false){
                        childViewHolder.minusAmount?.isEnabled == true
                    }
                    val num = goodsBean.quantity
                    var integer = num.toInt()
                    integer++
                    goodsBean.quantity = "$integer"
                    notifyDataSetChanged()
                    //回调请求后台接口实现数量的加减
                    mChangeCountListener.onChangeCount(goods_id)
                }
            })
            //减号的点击事件
            childViewHolder.minusAmount?.setOnClickListener(object :View.OnClickListener{
                override fun onClick(p0: View?) {
                    val num = goodsBean.quantity
                    var integer = num.toInt()
                    if (integer>1){
                        integer--
                        goodsBean.quantity = "$integer"
                        //回调请求后台接口实现数量的加减
                        mChangeCountListener.onChangeCount(goods_id)
                        notifyDataSetChanged()
                    }else{
                       childViewHolder.minusAmount?.isEnabled = false
                       ToastUtil.showToast(context!!,"客官 不能再少了")
                    }
                }
            })
        }
        return v
    }

    class ChildViewHolder {
        var iv_select: ImageView? = null
        var cargoImg: ImageView? = null
        var cargoTit: TextView? = null
        var cargoPrice: TextView? = null
        var llAmount: LinearLayout? = null
        var minusAmount: TextView? = null
        var amount: TextView? = null
        var addAmount: TextView? = null

        constructor(view: View) {
            iv_select = view.findViewById(R.id.iv_select)
            cargoImg = view.findViewById(R.id.cargoImg)
            cargoTit = view.findViewById(R.id.cargoTit)
            cargoPrice = view.findViewById(R.id.cargoPrice)
            llAmount = view.findViewById(R.id.llAmount)
            minusAmount = view.findViewById(R.id.minusAmount)
            amount = view.findViewById(R.id.amount)
            addAmount = view.findViewById(R.id.addAmount)
        }
    }

    //设置子列表是否可选中(很重要！！！)，否则点击子项会出错
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    //花样设置 合计金额  return 设置后的数据
    private fun getGaudyStr(str: String): SpannableString {
        val spannableString = SpannableString(str)
        val textColor = ForegroundColorSpan(Color.parseColor("#F93736")) //文字颜色
//        StyleSpan : 字体样式：粗体、斜体等
        val dotInd = str.indexOf(".") //获取小数点的下标
        val frontSize = AbsoluteSizeSpan(56) //56px
        val behindSize = AbsoluteSizeSpan(72) //72px
        spannableString.setSpan(textColor, 0, str.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE) //设置颜色
        spannableString.setSpan(frontSize, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE) //设置人民币符号颜色  前面包括，后面不包括
        spannableString.setSpan(behindSize, 1, dotInd, Spannable.SPAN_INCLUSIVE_INCLUSIVE) //设置元符号颜色   前面包括，后面包括
        spannableString.setSpan(
            frontSize,
            dotInd,
            str.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        ) //设置人民币符号颜色  前面不包括，后面不包括
        //Spannable. SPAN_EXCLUSIVE_INCLUSIVE：前面不包括，后面包括
        return spannableString
    }

    //删除 回调
    interface OnDeleteListener {
        fun onDelete()
    }

    fun setOnDeleteListener(listener: OnDeleteListener) {
        mDeleteListener = listener
    }

    private lateinit var mDeleteListener: OnDeleteListener

    //修改商品数量的回调
    interface OnChangeCountListener {
        fun onChangeCount(cart_id: String)
    }

    fun setOnChangeCountListener(listener: OnChangeCountListener) {
        mChangeCountListener = listener
    }

    private lateinit var mChangeCountListener: OnChangeCountListener
}