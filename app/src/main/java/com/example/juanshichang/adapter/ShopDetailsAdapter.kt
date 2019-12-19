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
import java.util.concurrent.ConcurrentHashMap

/**
 * @作者: yzq
 * @创建日期: 2019/11/21 16:47
 * @文件作用: 商品详情弹出框 Adapter
 * https://github.com/Tencent/QMUI_Android/blob/1.x/qmuidemo/src/main/java/com/qmuiteam/qmuidemo/fragment/components/QDFloatLayoutFragment.java
 */
class ShopDetailsAdapter() :
    BaseQuickAdapter<ZyProduct.Option, BaseViewHolder>(R.layout.item_shop_details_dialog) {
    //    private var allList = ArrayList<HashMap<String, ArrayList<String>>>() //设置一个存储类
    private var theData: List<ZyProduct.Option>? = null
    private var allMap: ConcurrentHashMap<String, ArrayList<String>>? = null//设置一个存储的类 hashMapOf()

    init {
        allMap = ConcurrentHashMap()
    }

    override fun convert(helper: BaseViewHolder?, item: ZyProduct.Option?) {
        helper?.setText(R.id.itemTit, item?.name) //设置标题
        //设置列表
//        val ind = data.indexOf(item) //获取item下标
        val fatData = item
        val data = item?.product_option_value
        val floatLayout = helper?.getView<QMUIFloatLayout>(R.id.itemFloat)
        if (item?.type?.contentEquals("radio")!! || item.type.contentEquals("select")) { //单选
            val radioList: ArrayList<String> = arrayListOf() //初始化单选集合
            if (item.required.contentEquals("1")) {//为1 必选 0 可选
                if (!allMap?.containsKey("${item.product_option_id}")!! && data?.size!=0) {
                    radioList.add(data!![0].product_option_value_id) //todo 暂时先这么写
                    allMap?.put("${item.product_option_id}", radioList)
                }
                setBackground(floatLayout, fatData, 1)
            } else { //非必选
                if (!allMap?.containsKey("${item.product_option_id}")!!) {
                    allMap?.put("${item.product_option_id}", radioList)
                }
                setBackground(floatLayout, fatData, 2)
            }
        } else if (item.type.contentEquals("checkbox")) { //多选
            val checkboxList: ArrayList<String> = arrayListOf() //初始化多选集合
            if (item.required.contentEquals("1")) {//为1 必选 0 可选
                if (!allMap?.containsKey("${item.product_option_id}")!! && data?.size!=0) {
                    checkboxList.add(data!![0]?.product_option_value_id) //todo 暂时先这么写
                    allMap?.put("${item.product_option_id}", checkboxList)
                }
                setBackground(floatLayout, item, 3)
            } else {
                if (!allMap?.containsKey("${item.product_option_id}")!!) {
                    allMap?.put("${item.product_option_id}", checkboxList)
                }
                setBackground(floatLayout, item, 4)
            }
        } else {
            helper?.setVisible(R.id.item_main, false)
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
        val currentChildCount = floatLayout?.childCount //获取floatLayout item 个数
        floatData?.let {
            floatLayout?.removeAllViews()
            for (i in 0 until it.size) {
                addItemToFloatLayout(floatLayout, it, i, id, type)
            }
        }
    }

    //问题解决思路
    private fun addItemToFloatLayout(
        floatLayout: QMUIFloatLayout?,
        data: List<ZyProduct.ProductOptionValue>,
        index: Int,
        fatherId: String,
        type: Int
    ) {
        val content = data[index].name  //内容
        val currentChildCount = floatLayout?.childCount //获取floatLayout item 个数
        val tv = TextView(mContext)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f) //设置字体大小
        tv.setText(content)
        if (selectCheck(fatherId, data[index].product_option_value_id)) {//选中
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.indicatorRed))
            tv.setBackgroundResource(R.drawable.bg_topup_red)
        } else {
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.main_text))
            tv.setBackgroundResource(R.drawable.bg_shop_dialog)
        }
//        val lAndR = QMUIDisplayHelper.px2dp(mContext, 36)
//        val tAndB = QMUIDisplayHelper.px2dp(mContext, 11)
//        tv.setPadding(lAndR, tAndB, lAndR, tAndB)
        tv.setPadding(32, 10, 32, 10)
        val lp = ViewGroup.LayoutParams(    //ViewGroup.LayoutParams.WRAP_CONTENT
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

//        //添加点击事件
        /*tv.setOnClickListener {
            var list: ArrayList<String>? = null
            when (type) {
                1 -> {//必选单选
                    val isCheck = selectCheck(fatherId, data[index].product_option_value_id)
                    if (isCheck) { //选中态 就 不做操作
                        return@setOnClickListener
                    } else {
                        floatLayout?.removeAllViews()
                        LogTool.e("tagMap", allMap.toString())
                        for ((k, v) in allMap) {
                            if (k == fatherId) {
                                list = v
                                allMap.remove(k)
                                LogTool.e("tagMap", allMap.toString())
                                break
                            }
                        }
                        list?.let {
                            it.clear()
                            it.add(data[index].product_option_value_id)
                            allMap.put(fatherId, it)
                            LogTool.e("tagMap", allMap.toString())
                        }
//                        addCheck(fatherId,data[index].product_option_value_id,1)
                    }
                    LogTool.e("tag1", "isCheck：$isCheck  fatherId:$fatherId  index:$index")
                }
                2 -> {//非必选单选
                    floatLayout?.removeAllViews()
                    val isCheck = selectCheck(fatherId, data[index].product_option_value_id)
                    if (!isCheck) {//如果开始未选中
                        for ((k, v) in allMap) {
                            if (k == fatherId) {
                                v.clear()
                                v.add(data[index].product_option_value_id)
                                allMap.remove(k)
                                allMap.put(fatherId, v)
                                break
                            }
                        }
//                        addCheck(fatherId, data[index].product_option_value_id, 2)
                    } else {
                        for ((k, v) in allMap) {
                            if (k == fatherId) {
                                v.clear()
                                break
                            }
                        }
//                        removeCheck(fatherId, data[index].product_option_value_id, 2)
                    }
                }
                3 -> {//必选多选
                    //先获取选中个数
                    val isSelectNum: Int = getCheckSize(fatherId)
                    val isCheck: Boolean =
                        selectCheck(fatherId, data[index].product_option_value_id)
                    //取消 -> 确定
                    LogTool.e("tagMap", allMap.toString())
                    if (!isCheck) {
                        floatLayout?.removeAllViews()
                        for ((k, v) in allMap) {
                            if (k == fatherId) {
                                LogTool.e("tagMap", allMap.toString())
                                v.add(data[index].product_option_value_id)
                                break
                            }
                        }
//                        addCheck(fatherId, data[index].product_option_value_id, 3)
                    }
                    //确定 ->取消
                    if (isCheck && isSelectNum < 2) { //如果只剩一个选中
                        return@setOnClickListener
                    } else if (isCheck && isSelectNum >= 2) { //如果还有多个
                        floatLayout?.removeAllViews()
                        for ((k, v) in allMap) {
                            if (k == fatherId && v.contains(data[index].product_option_value_id)) {
                                LogTool.e("tagMap", allMap.toString())
                                v.remove(data[index].product_option_value_id)
                                break
                            }
                        }
//                        removeCheck(fatherId, data[index].product_option_value_id, 3)
                    }
                    LogTool.e(
                        "tag3",
                        "isSelectNum:$isSelectNum  isCheck：$isCheck  fatherId:$fatherId  index:$index"
                    )
                }
                4 -> {//非必选多选
                    floatLayout?.removeAllViews()
                    val isCheck: Boolean =
                        selectCheck(fatherId, data[index].product_option_value_id)
                    if (!isCheck) { //未选中
                        for ((k, v) in allMap) {
                            if (k == fatherId) {
                                v.add(data[index].product_option_value_id)
                                break
                            }
                        }
//                        addCheck(fatherId, data[index].product_option_value_id, 4)
                    } else {
                        for ((k, v) in allMap) {
                            if (k == fatherId && v.contains(data[index].product_option_value_id)) {
                                v.remove(data[index].product_option_value_id)
                                break
                            }
                        }
//                        removeCheck(fatherId, data[index].product_option_value_id, 4)
                    }
                }
            }
            notifyDataSetChanged()
        }*/
        floatLayout?.addView(tv, lp)
        floatLayout?.get(index)?.setOnClickListener {
            var list: ArrayList<String>? = null
            val isCheck = selectCheck(fatherId, data[index].product_option_value_id)
            when (type) {
                1 -> {//必选单选
                    if (isCheck) { //选中态 就 不做操作
                        return@setOnClickListener
                    } else {
                        floatLayout.removeAllViews()
                        LogTool.e("tagMap", allMap.toString())
                        for ((k, v) in allMap!!) {
                            if (k == fatherId) {
                                v.clear()
                                LogTool.e("tagMap", allMap.toString())
                                v.add(data[index].product_option_value_id)
                                LogTool.e("tagMap", allMap.toString())
                                break
                            }
                        }
//                        addCheck(fatherId,data[index].product_option_value_id,1)
                    }
                    LogTool.e("tag1", "isCheck：$isCheck  fatherId:$fatherId  index:$index")
                }
                2 -> {//非必选单选
                    floatLayout.removeAllViews()
                    if (!isCheck) {//如果开始未选中
                        for ((k, v) in allMap!!) {
                            if (k == fatherId) {
                                v.clear()
                                v.add(data[index].product_option_value_id)
                                allMap?.remove(k)
                                allMap?.put(fatherId, v)
                            }
                        }
//                        addCheck(fatherId, data[index].product_option_value_id, 2)
                    } else {
                        for ((k, v) in allMap!!) {
                            if (k == fatherId) {
                                v.clear()
                            }
                        }
//                        removeCheck(fatherId, data[index].product_option_value_id, 2)
                    }
                }
                3 -> {//必选多选
                    //先获取选中个数
                    val isSelectNum: Int = getCheckSize(fatherId)
                    //取消 -> 确定
                    LogTool.e("tagMap", allMap.toString())
                    if (!isCheck) {
                        floatLayout.removeAllViews()
                        for ((k, v) in allMap!!) {
                            if (k == fatherId) {
                                v.add(data[index].product_option_value_id)
                                LogTool.e("tagMap", allMap.toString())
                                break
                            }
                        }
//                        addCheck(fatherId, data[index].product_option_value_id, 3)
                    }
                    //确定 ->取消
                    if (isCheck && isSelectNum < 2) { //如果只剩一个选中
                        return@setOnClickListener
                    } else if (isCheck && isSelectNum >= 2) { //如果还有多个
                        floatLayout.removeAllViews()
                        for ((k, v) in allMap!!) {
                            if (k == fatherId && v.contains(data[index].product_option_value_id)) {
                                LogTool.e("tagMap", allMap.toString())
                                v.remove(data[index].product_option_value_id)
                                break
                            }
                        }
//                        removeCheck(fatherId, data[index].product_option_value_id, 3)
                    }
                    LogTool.e(
                        "tag3",
                        "isSelectNum:$isSelectNum  isCheck：$isCheck  fatherId:$fatherId  index:$index"
                    )
                }
                4 -> {//非必选多选
                    floatLayout.removeAllViews()
                    if (!isCheck) { //未选中
                        for ((k, v) in allMap!!) {
                            if (k == fatherId) {
                                v.add(data[index].product_option_value_id)
                            }
                        }
//                        addCheck(fatherId, data[index].product_option_value_id, 4)
                    } else {
                        for ((k, v) in allMap!!) {
                            if (k == fatherId && v.contains(data[index].product_option_value_id)) {
                                v.remove(data[index].product_option_value_id)
                            }
                        }
//                        removeCheck(fatherId, data[index].product_option_value_id, 4)
                    }
                }
            }
            notifyDataSetChanged()
        }
    }


    //查询集合是否包含选中数据源
    private fun selectCheck(fatherId: String, sonId: String): Boolean {
        for ((k, v) in allMap!!) {
            if (k == fatherId) {
                if (v.contains(sonId)) {
                    return true
                } else {
                    LogTool.e("tag", "集合不包含该数据 Size:${v.size}")
                    return false
                }
            }
        }
        LogTool.e("tag", "不存在的map key参数")
        return false
    }

    //添加进返回集合
    private fun addCheck(fatherId: String, sonId: String, type: Int) {
        for ((k, v) in allMap!!) {
            if (k == fatherId) {
                if (type == 1 || type == 2) { //单选先清空集合
                    v.clear()
                }
                v.add(sonId)
                break
            }
        }
    }

    //从返回集合移除
    private fun removeCheck(fatherId: String, sonId: String, type: Int) {
        for ((k, v) in allMap!!) {
            if (k == fatherId) {
                if (type == 1 || type == 3) { //必选
                    if (v.size > 2) {
                        return
                    }
                } else {
                    if (v.contains(sonId)) {
                        v.remove(sonId)
                    }
                }
                break
            }
        }
    }

    //返回指定选中集合的长度
    private fun getCheckSize(fatherId: String): Int {
        for ((k, v) in allMap!!) {
            if (k == fatherId) {
                return v.size
            }
        }
        return 0
    }

    //返回选中数据
    fun getAllCheck(): Map<String, ArrayList<String>> {
        return allMap ?: ConcurrentHashMap<String, ArrayList<String>>()
    }

    fun setAllCheck(map: Map<String, ArrayList<String>>) {
        allMap = map as ConcurrentHashMap<String, ArrayList<String>>
    }

    //外部更新内部数据
    fun setTheData(data: List<ZyProduct.Option>) {
        this.theData = data

        setNewData(data)
    }

    //返回弹窗数据
    fun getTheData(): List<ZyProduct.Option>? {
        return theData ?: null
    }
}