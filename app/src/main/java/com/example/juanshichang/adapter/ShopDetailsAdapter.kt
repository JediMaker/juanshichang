package com.example.juanshichang.adapter

import android.text.Layout
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.ZyProduct
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.widget.QMUIFloatLayout

/**
 * @作者: yzq
 * @创建日期: 2019/11/21 16:47
 * @文件作用: 商品详情弹出框 Adapter
 * https://github.com/Tencent/QMUI_Android/blob/1.x/qmuidemo/src/main/java/com/qmuiteam/qmuidemo/fragment/components/QDFloatLayoutFragment.java
 */
class ShopDetailsAdapter() : BaseQuickAdapter<ZyProduct.Option, BaseViewHolder>(R.layout.item_shop_details_dialog){
    val allList = ArrayList<ArrayList<String>>() //设置一个存储类
    override fun convert(helper: BaseViewHolder?, item: ZyProduct.Option?) {
        val ind = data.indexOf(item) //获取item下标
        val data = item?.product_option_value
        if (item?.type?.contentEquals("radio")!!){ //单选
            if(item.required.contentEquals("1")){//为1 必选 0 可选
                var isSelect = false
                //校验一遍数据
                for (i in 0..data?.size!!){
                    if(!isSelect && data[i].is_select){
                        isSelect = true
                    }
                    if(isSelect && data[i].is_select){
                        data[i].is_select = false
                    }
                }
                helper?.setText(R.id.itemTit,item.name) //设置标题
                setBackground(helper,item)
            }else{ //非必选

            }
        }else if(item.type.contentEquals("checkbox")) { //多选


        }
    }
    private  fun setBackground(helper: BaseViewHolder?,data:ZyProduct.Option?){
        val floatLayout = helper?.getView<QMUIFloatLayout>(R.id.itemFloat)
        val floatData = data?.product_option_value  //数据源
        floatData?.let {
            for (i in 0..it.size){
                addItemToFloatLayout(floatLayout,it[i].is_select,it[i].name)
            }
        }
    }

    private fun addItemToFloatLayout(floatLayout:QMUIFloatLayout?,isSelect:Boolean,content:String){
        val currentChildCount = floatLayout?.childCount //获取个数
        val lAndR = QMUIDisplayHelper.px2dp(mContext,36)
        val tAndB = QMUIDisplayHelper.px2dp(mContext,11)
        val tv = TextView(mContext)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,14f) //设置字体大小
        tv.setText(content)
        tv.setPadding(lAndR,tAndB,lAndR,tAndB)
        if(isSelect){//选中
            tv.setTextColor(ContextCompat.getColor(mContext,R.color.indicatorRed))
            tv.setBackgroundResource(R.drawable.bg_topup_red)
        }else{
            tv.setTextColor(ContextCompat.getColor(mContext,R.color.main_text))
            tv.setBackgroundResource(R.drawable.bg_shop_dialog)
        }
        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        floatLayout?.addView(tv,lp)
    }
}