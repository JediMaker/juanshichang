package com.example.juanshichang.adapter

import android.text.Layout
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.setPadding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.bean.ZyProduct
import com.example.juanshichang.utils.LogTool
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.widget.QMUIFloatLayout

/**
 * @作者: yzq
 * @创建日期: 2019/11/21 16:47
 * @文件作用: 商品详情弹出框 Adapter
 * https://github.com/Tencent/QMUI_Android/blob/1.x/qmuidemo/src/main/java/com/qmuiteam/qmuidemo/fragment/components/QDFloatLayoutFragment.java
 */
class ShopDetailsAdapter() :
    BaseQuickAdapter<ZyProduct.Option, BaseViewHolder>(R.layout.item_shop_details_dialog) {
    private val allList = ArrayList<HashMap<String,ArrayList<String>>>() //设置一个存储类
    override fun convert(helper: BaseViewHolder?, item: ZyProduct.Option?) {
        helper?.setText(R.id.itemTit, item?.name) //设置标题
        //设置列表
//        val ind = data.indexOf(item) //获取item下标
        val data = item?.product_option_value
        val floatLayout = helper?.getView<QMUIFloatLayout>(R.id.itemFloat)
        if (item?.type?.contentEquals("radio")!!) { //单选
            val radioList = ArrayList<String>(1) //初始化单选集合
            if (item.required.contentEquals("1")) {//为1 必选 0 可选
                var isSelect = false
                //校验一遍数据
                for (i in 0 until data?.size!!) {
                    if (!isSelect && data[i].is_select) {
                        isSelect = true
                        radioList.add(data[i].product_option_value_id) //todo 暂时先这么写
                    }
                    if (isSelect && data[i].is_select) {
                        data[i].is_select = false
                    }
                }
                if (!isSelect) {//没有选中的 默认选中0
                    data[0].is_select = true
                    item.product_option_value = data //把修改过的数据赋予给item
                    radioList.add(data[0].product_option_value_id) //todo 暂时先这么写
                }
                setBackground(floatLayout, item, 1)

            } else { //非必选
                setBackground(floatLayout, item, 2)
            }
            val map = HashMap<String,ArrayList<String>>()
            map.put("${item.product_option_id}",radioList)
            allList.add(map)
        } else if (item.type.contentEquals("checkbox")) { //多选
            val checkboxList = ArrayList<String>(item.product_option_value.size) //初始化多选集合
            if(item.required.contentEquals("1")){//为1 必选 0 可选
                var isSelect = false
                //校验一遍数据
                for (i in 0 until data?.size!!) {
                    if (!isSelect && data[i].is_select) {
                        isSelect = true
                        checkboxList.add(data[i].product_option_value_id) //todo 暂时先这么写
                    }
                    if (isSelect && data[i].is_select) {
                        data[i].is_select = false
                    }
                }
                if (!isSelect) {//没有选中的 默认选中0
                    data[0].is_select = true
                    item.product_option_value = data //把修改过的数据赋予给item
                    checkboxList.add(data[0].product_option_value_id) //todo 暂时先这么写
                }
                setBackground(floatLayout, item, 3)
            }else{
                setBackground(floatLayout, item, 4)
            }
            val map = HashMap<String,ArrayList<String>>()
            map.put("${item.product_option_id}",checkboxList)
            allList.add(map)
        }else{
            helper?.setVisible(R.id.item_main,false)
        }
    }

    /**
     * @param floatLayout 传入控件
     * @param data  数据源
     * @param type  1：必选单选 2：非必选单选 3：必选多选 4:非必选多选
     */
    private fun setBackground(floatLayout: QMUIFloatLayout?, data: ZyProduct.Option?, type: Int) {
        val floatData = data?.product_option_value  //数据源
        val id: String = data?.product_option_id.toString() //数据product_option_id
        floatData?.let {
            for (i in 0 until it.size) {
                addItemToFloatLayout(floatLayout, it, i, id, type)
            }
        }
    }

    private fun addItemToFloatLayout(
        floatLayout: QMUIFloatLayout?,
        data: List<ZyProduct.ProductOptionValue>,
        index: Int,
        fatherId: String,
        type: Int
    ) {
        val isSelect = data[index].is_select //是否选中
        val content = data[index].name  //内容
        val currentChildCount = floatLayout?.childCount //获取floatLayout item 个数
        val tv = TextView(mContext)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f) //设置字体大小
        tv.setText(content)
        if (isSelect) {//选中
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.indicatorRed))
            tv.setBackgroundResource(R.drawable.bg_topup_red)
        } else {
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.main_text))
            tv.setBackgroundResource(R.drawable.bg_shop_dialog)
        }
//        val lAndR = QMUIDisplayHelper.px2dp(mContext, 36)
//        val tAndB = QMUIDisplayHelper.px2dp(mContext, 11)
//        tv.setPadding(lAndR, tAndB, lAndR, tAndB)
        tv.setPadding(32,10,32,10)
        val lp = ViewGroup.LayoutParams(    //ViewGroup.LayoutParams.WRAP_CONTENT
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        //添加点击事件
        tv.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                when (type) {
                    1 -> {//必选单选
                        LogTool.e("tag1","index:$index")
                        if (data[index].is_select) { //选中态 就 不做操作
                            return
                        } else {
                            retState(data, false)
                            data[index].is_select = true
                            setMyData(data, fatherId)
                        }
                    }
                    2 -> {//非必选单选
                        val state = data[index].is_select
                        retState(data, false)
                        data[index].is_select = !state
                        setMyData(data, fatherId)
                        LogTool.e("tag2","index:$index")
                    }
                    3 -> {//必选多选
                        //先获取选中个数
                        var isSelectNum:Int = 0
                        for (i in 0 until data.size){
                            if(data[i].is_select){
                                isSelectNum++
                            }
                        }
                        //确定 ->取消
                        if(data[index].is_select && isSelectNum < 2){ //如果只剩一个选中
                            return
                        }else if(data[index].is_select && isSelectNum >= 2){ //如果还有多个
                            data[index].is_select = false
                            setMyData(data, fatherId)
                        }
                        //取消 -> 确定
                        if(!data[index].is_select){
                            data[index].is_select = true
                            setMyData(data, fatherId)
                        }
                        LogTool.e("tag3","index:$index")
                    }
                    4 -> {//非必选多选
                        val state = data[index].is_select
                        data[index].is_select = !state
                        setMyData(data, fatherId)
                        LogTool.e("tag4","index:$index")
                    }
                }
            }
        })
        floatLayout?.addView(tv,lp)
    }

    //单选状态全部变更
    private fun retState(data: List<ZyProduct.ProductOptionValue>?, valueState: Boolean = false) {
        for (i in 0 until data?.size!!) {
            data[i].is_select = valueState
        }
    }
    //更新数据源并刷新
    private fun setMyData(data: List<ZyProduct.ProductOptionValue>, fatherId: String) {
        val oldData: List<ZyProduct.Option> = mData
        oldData.let {
            //更新数据源
            val newData = ArrayList<ZyProduct.Option>()
            for (i in 0 until it.size) {
                if (it[i].product_option_id.contentEquals(fatherId)) {
                    val upd = it[i]
                    upd.product_option_value = data
                    newData.add(upd)
                } else {
                    newData.add(it[i])
                }
            }
            //更新列表数据
            updRetData(data,fatherId)
            //刷新列表
//            replaceData(newData)
            setNewData(newData)
        }
    }
    //更新返回的数据源
    private  fun updRetData(data: List<ZyProduct.ProductOptionValue>,fatherId: String){
        allList.let {
            var isExist = false
            for (i in 0 until it.size){
                if(it[i].containsKey(fatherId)){//如果有这个key
                    isExist = true //数据源存在
                    val value = it[i].get(fatherId)
                    value?.clear()
                    for (y in 0 until data.size){ //把选中的id 添加到返回列表
                        if(data[y].is_select){
                            value?.add(data[y].product_option_value_id) //todo 暂时先这么写
                        }
                    }
                }
            }
            if(!isExist){
                val list = ArrayList<String>()
                for (y in 0 until data.size){
                    if(data[y].is_select){
                        list.add(data[y].product_option_value_id) //todo 暂时先这么写
                    }
                }
                val map = HashMap<String,ArrayList<String>>()
                map.put(fatherId,list)
                allList.add(map)
            }
        }
    }
    //返回选中数据
    fun getAllCheck():ArrayList<HashMap<String,ArrayList<String>>>{
        return allList?:ArrayList<HashMap<String,ArrayList<String>>>()
    }
}