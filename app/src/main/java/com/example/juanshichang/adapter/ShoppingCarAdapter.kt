package com.example.juanshichang.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.juanshichang.R
import com.example.juanshichang.activity.ConOrderActivity
import com.example.juanshichang.activity.ShangPinZyContains
import com.example.juanshichang.bean.CartBean
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.UtilsBigDecimal
import com.example.juanshichang.utils.glide.GlideUtil
import java.math.BigDecimal

/**
 * @作者: yzq
 * @创建日期: 2019/11/16 16:04
 * @文件作用: 购物车适配器
 * ck: https://my.oschina.net/longxuanzhigu/blog/3063650
 *  https://blog.csdn.net/qq941263013/article/details/80901277
 */
@Suppress("IMPLICIT_CAST_TO_ANY")
class ShoppingCarAdapter : BaseExpandableListAdapter {
    private var context: Context? = null
    private var llSelectAll: LinearLayout? = null //全选
    private var isSelectAll: CheckBox? = null //全选按钮
    private var editor: TextView? = null //编辑
    private var total: TextView? = null  //合计
    private var goPay: TextView? = null  //去结算
    private var data: ArrayList<CartBean.CartBeans?> = arrayListOf()
    private var allSelect: Boolean = false //全选

    private val SHOPCARFINISH: Int = 1   //完成状态  按钮显示编辑
    private val SHOPCAREDIT: Int = 2  //编辑状态  按钮显示完成
    private var shopType: Int = SHOPCARFINISH
    private var cardIdList: ArrayList<String>? = null //这是存放选中的cardid集合

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
        this.cardIdList = arrayListOf()
    }

    /**
     * 自定义设置数据方法；
     * 通过notifyDataSetChanged()刷新数据，可保持当前位置
     * @param data 需要刷新的数据
     */
    fun setData(
        catBean: CartBean.CartBeans?,
        needChange: Boolean
    ) {
        //把之前购物车商品的状态copy一份
        for (z in 0 until (catBean?.data?.products?.size ?: 0)) {
            for (i in 0 until this.data.size) {
                for (y in 0 until data[i]?.data?.products?.size!!) {
                    if (catBean?.data?.products?.get(z)?.cart_id.equals(data[i]?.data?.products!![y].cart_id)) {
                        catBean?.data?.products?.get(z)?.isSelect =
                            data[i]?.data?.products!![y].isSelect;
                    }
                }
            }

        }
        this.data.clear()
        this.data.add(catBean)
        if (needChange) {
            shopType = SHOPCARFINISH //切换为完成状态
            editor?.text = "编辑"
        }

        LogTool.e("shopcar", "数据填充完成....")
        notifyDataSetChanged()

    }

    override fun getGroupCount(): Int {
//        if (data?.data?.products?.size != 0) { //这里目前只能这么写...
//            return 1
//        }else{
//        }
        return data.size ?: 0
    }

    override fun getGroup(p: Int): Any {
//        return data?.data?.products?.get(p)!!
        return data[p]!!
    }

    override fun getGroupId(p: Int): Long {
        return p.toLong()
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var groupViewHolder: GroupViewHolder? = null
        var v: View? = null
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
        val fatherGroup = data[groupPosition]?.data?.products
        LogTool.e("shopCarFat", "DATA :  $fatherGroup")
        fatherGroup?.let {
            //设置标题
            groupViewHolder.tv_store_name?.text = "商城自营"

            //店铺内的商品都选中的时候，店铺的也要选中
            for (i in 0 until it.size) {
                val isSelect = it[i].isSelect
                if (isSelect) {
                    //这里 应该给 父类Group 以 选中参数 因为暂时没有数据 -- 直接就选中
                    groupViewHolder.iv_select?.isChecked = true
                } else {
                    groupViewHolder.iv_select?.isChecked = false
                    break
                }
            }
            //店铺是否在购物车中被选中  --- 因为 没有父类Group数据  故放弃此段逻辑

            //店铺选择框的点击事件
            groupViewHolder.ll?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    var state: Boolean = false
                    val select: Boolean = groupViewHolder.iv_select?.isChecked!!
                    state = !select
                    groupViewHolder.iv_select?.isChecked = state
                    //数据结构因素 ....
                    for (i in 0 until it.size) {
                        it[i].isSelect = state
                    }
                    notifyDataSetChanged()
                }
            })
            //当所有的选择框都是选中的时候，全选也要选中 todo --- 数据结构原因 逻辑改变
            for (i in 0 until it.size) {
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
            //编辑的点击事件
            editor?.setOnClickListener {
                if (shopType == SHOPCARFINISH) {//完成状态  按钮显示编辑
                    shopType = SHOPCAREDIT //切换为编辑状态
                    editor?.text = "完成"
                } else {
                    shopType = SHOPCARFINISH //切换为完成状态
                    editor?.text = "编辑"
                }
                notifyDataSetChanged()
            }
            //全选的点击事件
            llSelectAll?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    allSelect = !allSelect
                    if (allSelect) {
                        for (i in 0 until data?.size) {
                            for (y in 0 until data[i]?.data?.products?.size!!) {
                                data[i]?.data?.products!![y].isSelect = true
                            }
                        }
                        isSelectAll?.isChecked = true
                    } else {
                        for (i in 0 until data?.size) {
                            for (y in 0 until data[i]?.data?.products?.size!!) {
                                data[i]?.data?.products!![y].isSelect = false
                            }
                        }
                        isSelectAll?.isChecked = false
                    }
                    notifyDataSetChanged()
                }
            })
            //合计的计算
//            var total_price: Double = 0.0
            var total_price: Double = 0.0
//            total?.text = getGaudyStr("${data[groupPosition]?.data?.totals!![0].text}")
            for (i in 0 until it.size) {
                val isSelect = it[i].isSelect
                if (isSelect) {
                    val num = it[i].quantity
                    var price = it[i].price
                    val rmbInd = price.indexOf("¥") //获取人民币符号的下标
                    price = price.substring(rmbInd + 1, price.length)
                    price = price.replace(",", "")
                    val v: BigDecimal = num.toBigDecimal()
                    val v1: BigDecimal = price.toBigDecimal()
//                    total_price = total_price + UtilsBigDecimal.mul2(v, v1, 2)
                    total_price = UtilsBigDecimal.add(total_price, UtilsBigDecimal.mul2(v, v1, 2))
                    //让Double类型完整显示，不用科学计数法显示大写字母E
//                    val decimalFormat = DecimalFormat("0.00")
                    total?.text = "¥$total_price"
                }
            }
            total?.text = "¥$total_price"

            //去结算的点击事件
            goPay?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    //创建临时的List，用于存储被选中的商品
                    val temp = arrayListOf<String>()
                    for (i in 0 until it.size) {
                        val isSelect = it[i].isSelect
                        if (isSelect) {
                            temp.add(it[i].cart_id)
                        }
                    }
                    if (temp.size != 0) {
                        LogTool.e(
                            "adapterDate",
                            "跳转到确认订单页面，完成后续订单流程 ${temp.size}  内容：${temp.toString()}"
                        )
                        val intent = Intent(context, ConOrderActivity::class.java)
                        val bundle = Bundle()
                        bundle.putStringArrayList("checkAll", temp)
                        intent.putExtra("bundle", bundle)
                        context?.startActivity(intent)
                    } else {
                        ToastUtil.showToast(context!!, "请选择要购买的商品")
                    }
                }
            })
            //删除的点击事件
        }
        if (fatherGroup?.size == 0) {
            v?.visibility = View.GONE
        } else {
            v?.visibility = View.VISIBLE
        }
        LogTool.e("shopCarFat", v.toString())
        return v!!
    }

    class GroupViewHolder {
        var iv_select: CheckBox? = null
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
    override fun getChildrenCount(groupPosition: Int): Int {
        val count = data[groupPosition]?.data?.products?.size ?: 0
        LogTool.e("shopCarChild", "getChildrenCount：$count")
        return count
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        LogTool.e("shopCarChild", "groupPosition：$groupPosition  childPosition:$childPosition")
        return data[groupPosition]?.data?.products?.get(childPosition)!!
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
        var childViewHolder: ChildViewHolder? = null
        var v: View? = null
        if (convertView == null) {
            v = View.inflate(context, R.layout.item_shopping_car_child, null)
            childViewHolder = ChildViewHolder(v)
            v.tag = childViewHolder
        } else {
            v = convertView
            childViewHolder = v.tag as ChildViewHolder
        }
        val fatherGroup = data[groupPosition]?.data?.products as ArrayList<CartBean.Product>
        LogTool.e("shopCarChild", "data: $fatherGroup")
        fatherGroup.let {
            LogTool.e(
                "shopCarChild",
                "groupPosition：$groupPosition  childPosition:$childPosition"
            )
            val goodsBean: CartBean.Product = it[childPosition]
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
            val goodsOptionsValue: String = ""
            childViewHolder.cargoImg?.tag = goods_image
            if (childViewHolder.cargoImg?.tag != null && childViewHolder.cargoImg?.tag!!.equals(
                    goods_image
                )
            ) {
                GlideUtil.loadShopImg(
                    context,
                    goods_image,
                    childViewHolder.cargoImg,
                    childViewHolder.cargoImg?.drawable
                )
            }
            var result = ""
            if (goodsBean.option.size > 0) {
                for (goosOption in goodsBean.option) {
                    result += goosOption.value + " "
                }
            } else {
                ""
            }
            childViewHolder.cargoTit?.text = goods_name ?: ""
            childViewHolder.cargoPrice?.text = goods_price ?: ""
            childViewHolder.amount?.text = goods_num
            childViewHolder.carOptionValue?.text = result
            if (shopType == SHOPCARFINISH) { //完成状态  按钮显示编辑
                childViewHolder.llAmount?.visibility = View.VISIBLE
                childViewHolder.cargoDele?.visibility = View.GONE
//                childViewHolder.noStock?.text="库存不足"
            } else if (shopType == SHOPCAREDIT) { //编辑状态  按钮显示完成
                childViewHolder.llAmount?.visibility = View.GONE
                childViewHolder.cargoDele?.visibility = View.VISIBLE
            }
            if (goodsBean.quantity.toInt() > 1) { //判断减号能否点击
                childViewHolder.minusAmount?.isEnabled = true
            } else {
                childViewHolder.minusAmount?.isEnabled = false
            }
            if (goodsBean.quantity.toInt() > 1) {

            } else {
                childViewHolder.minusAmount?.isEnabled = false
            }
            if (isLastChild) {//如果是最后一个
                LogTool.e("shopcar", "最后一个？: $isLastChild")
                childViewHolder.endLayout?.visibility = View.GONE
                childViewHolder.endContent?.text =
                    data[groupPosition]?.data?.totals!![0].text //小计
            } else {
                childViewHolder.endLayout?.visibility = View.GONE
            }
            if (!isStock) { //设置是否有货的状态
//                childViewHolder.llAmount?.visibility = View.INVISIBLE
//                childViewHolder.noStock?.visibility = View.VISIBLE
                childViewHolder.llAmount?.visibility = View.VISIBLE
                childViewHolder.stockStatus?.visibility = View.VISIBLE
                childViewHolder.iv_select?.isSelected = false
                childViewHolder.iv_select?.isEnabled = false  //设置无货商品 不可选中 todo 注意编辑模式 释放该权限
//                childViewHolder.noStock?.text="库存不足"
                childViewHolder.addAmount?.isEnabled = false
//                childViewHolder.amount?.text = (goodsBean.quantity.toInt() - 1).toString()
                goodsBean.isSelect = false  //取消选中数据
                childViewHolder.stockStatus?.text = "库存不足"
                //设置双按钮 都不可点击
                if (shopType == SHOPCAREDIT) { //在编辑模式下
                    //待开发的功能....
                    childViewHolder.llAmount?.visibility = View.INVISIBLE
                }
            } else {
                childViewHolder.stockStatus?.visibility = View.INVISIBLE
                childViewHolder.minusAmount?.isEnabled = false
                childViewHolder.iv_select?.isChecked = isSelect  //设置是否选中
                childViewHolder.addAmount?.isEnabled = true
                childViewHolder.iv_select?.isEnabled = true
                if (isSelect) {
                    cardIdList?.add(goods_id)
                }
            }
            //商品选择框的点击事件
            childViewHolder.iv_select?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    goodsBean.isSelect = !isSelect
                    //这里 进行 父布局 变更 状态 变更
                    if (!isSelect == false) {
                        //设置 父布局 选中态为  false
                        isSelectAll?.isChecked = false
                    } else {
                        var check: Boolean = true
                        for (i in 0 until data.size) {
                            for (y in 0 until data[i]?.data?.products?.size!!) {
                                if (data[i]?.data?.products!![y].isSelect == false) {
                                    check = false
                                    break
                                }
                            }
                        }
                        if (check) {
                            isSelectAll?.isChecked = true
                        }
                    }
                    notifyDataSetChanged()
                }
            })

            val num = goodsBean.quantity
            var integer = num.toInt()
            if (integer > 1) {
                childViewHolder.minusAmount?.isEnabled = true
            }
            //减号的点击事件
            childViewHolder.minusAmount?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    childViewHolder.addAmount?.isEnabled = true
                    if (integer > 1) {
                        integer--
//                        goodsBean.quantity = "$integer"
//                        childViewHolder.amount?.text = "$integer"
                        //回调请求后台接口实现数量的加减
                        if (Util.isFastClick()) {
                            return
                        } else {
                            mChangeCountListener.onChangeCount(goods_id, integer)
                        }
//                        notifyDataSetChanged()
                    } else {
                        childViewHolder.minusAmount?.isEnabled = false
                        ToastUtil.showToast(context!!, "客官 不能再少了")
                    }
                }
            })
            //加号的点击事件
            childViewHolder.addAmount?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    if (goodsBean.minTotals.toString()
                            .toInt() - childViewHolder.amount?.text.toString()
                            .toInt() <= 0
                    ) {
                        childViewHolder.addAmount?.isEnabled = false
                        ToastUtil.showToast(context!!, "该商品不能购买更多了")
                        return
                    } else {
                        integer++
                        if (!childViewHolder.minusAmount?.isEnabled!! || integer > 1) {
                            childViewHolder.minusAmount?.isEnabled = true
                        }
//                        goodsBean.quantity = "$integer"
//                        childViewHolder.amount?.text = "$integer"
                        //回调请求后台接口实现数量的加减
                        if (Util.isFastClick()) {
                            return
                        } else {
                            mChangeCountListener.onChangeCount(goods_id, integer)
                        }
//                    notifyDataSetChanged()
                    }

                }
            })
            //删除按钮的点击事件
            childViewHolder.cargoDele?.setOnClickListener {
                mDeleteListener.onDelete(goods_id, goods_num)
            }
            //删除按钮的点击事件
            childViewHolder.itemCon?.setOnClickListener {//跳转到商品详情页
                val intent = Intent(context, ShangPinZyContains::class.java)
                intent.putExtra("product_id", goodsBean.product_id)
                context?.startActivity(intent)
            }
        }
        LogTool.e("shopCarChild", v.toString())
        return v!!
    }

    class ChildViewHolder {
        var iv_select: CheckBox? = null
        var cargoImg: ImageView? = null
        var cargoTit: TextView? = null
        var carOptionValue: TextView? = null
        var cargoPrice: TextView? = null
        var llAmount: LinearLayout? = null
        var minusAmount: TextView? = null
        var amount: TextView? = null
        var addAmount: TextView? = null
        var stockStatus: TextView? = null
        var endLayout: ConstraintLayout? = null
        var endContent: TextView? = null
        var cargoDele: View? = null
        var itemCon: View? = null

        constructor(view: View) {
            iv_select = view.findViewById(R.id.iv_select)
            cargoImg = view.findViewById(R.id.cargoImg)
            cargoTit = view.findViewById(R.id.cargoTit)
            carOptionValue = view.findViewById(R.id.carOptionValue)
            cargoPrice = view.findViewById(R.id.cargoPrice)
            stockStatus = view.findViewById(R.id.stockStatus)
            llAmount = view.findViewById(R.id.llAmount)
            stockStatus = view.findViewById(R.id.stockStatus)
            minusAmount = view.findViewById(R.id.minusAmount)
            amount = view.findViewById(R.id.amount)
            addAmount = view.findViewById(R.id.addAmount)
            endLayout = view.findViewById(R.id.endLayout)
            endContent = view.findViewById(R.id.endContent)
            cargoDele = view.findViewById(R.id.cargoDele)
            itemCon = view.findViewById(R.id.itemCon)
        }
    }

    //设置子列表是否可选中(很重要！！！)，否则点击子项会出错
    override fun isChildSelectable(
        groupPosition: Int,
        childPosition: Int
    ): Boolean {// 指定位置的子视图是否可选择
        return true
    }

    override fun hasStableIds(): Boolean {// 是否指定分组视图及其子视图的Id对应的后台数据改变也会保持该Id
        return false
    }

    //花样设置 合计金额  return 设置后的数据
    private fun getGaudyStr(str: String): SpannableString {
        val spannableString = SpannableString(str)
        val textColor = ForegroundColorSpan(Color.parseColor("#F93736")) //文字颜色
//        StyleSpan : 字体样式：粗体、斜体等
        val dotInd = str.indexOf(".") //获取小数点的下标
        val rmbInd = str.indexOf("¥") //获取人民币符号的下标
        val frontSize = AbsoluteSizeSpan(56) //56px
        val behindSize = AbsoluteSizeSpan(72) //72px
        spannableString.setSpan(
            textColor,
            0,
            str.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        ) //设置颜色
        if (rmbInd != -1) {
            spannableString.setSpan(
                frontSize,
                rmbInd,
                rmbInd + 1,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            ) //设置人民币符号大小 前面包括，后面不包括
        }
        if (dotInd != -1) {
            spannableString.setSpan(
                behindSize,
                1,
                dotInd,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            ) //设置元符号大小   前面包括，后面包括
            spannableString.setSpan(
                frontSize,
                dotInd,
                str.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            ) //设置人民币符号颜色  前面不包括，后面不包括
        }
        //Spannable. SPAN_EXCLUSIVE_INCLUSIVE：前面不包括，后面包括
        return spannableString
    }

    //删除 回调
    interface OnDeleteListener {
        fun onDelete(cart_id: String, num: String)
    }

    fun setOnDeleteListener(listener: OnDeleteListener) {
        mDeleteListener = listener
    }

    private lateinit var mDeleteListener: OnDeleteListener

    //修改商品数量的回调
    interface OnChangeCountListener {
        fun onChangeCount(cart_id: String, count: Int)
    }

    fun setOnChangeCountListener(listener: OnChangeCountListener) {
        mChangeCountListener = listener
    }

    private lateinit var mChangeCountListener: OnChangeCountListener

    fun getCheckedList(): List<String> { //这个用于多店铺选中数据返回...
        //先校验一遍
        for (i in 0 until data.size) {
            val di = data[i]?.data?.products
            for (y in 0 until di?.size!!) {
                val dy = data[i]?.data?.products!![y]
                if (dy.isSelect) { //如果数据为选中
                    if (!cardIdList?.contains(dy.cart_id)!!) {
                        cardIdList?.add(dy.cart_id)
                    }
                }
            }
        }
        return cardIdList as List<String>
    }
}