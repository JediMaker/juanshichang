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
    private var allList = ArrayList<HashMap<String, ArrayList<String>>>() //设置一个存储类
    private var theData: List<ZyProduct.Option>? = null
    override fun convert(helper: BaseViewHolder?, item: ZyProduct.Option?) {
        helper?.setText(R.id.itemTit, item?.name) //设置标题
        //设置列表
//        val ind = data.indexOf(item) //获取item下标
        val fatData = item
        val data = item?.product_option_value
        val floatLayout = helper?.getView<QMUIFloatLayout>(R.id.itemFloat)
        if (item?.type?.contentEquals("radio")!! || item?.type?.contentEquals("select")) { //单选
            val radioList = arrayListOf<String>() //初始化单选集合
            if (item.required.contentEquals("1")) {//为1 必选 0 可选
                radioList.add(data!![0]?.product_option_value_id) //todo 暂时先这么写
                val map = HashMap<String, ArrayList<String>>()
                map.put("${item.product_option_id}", radioList)
                allList.add(map)
                setBackground(floatLayout, fatData, 1)
            } else { //非必选
                val map = HashMap<String, ArrayList<String>>()
                map.put("${item.product_option_id}", radioList)
                allList.add(map)
                setBackground(floatLayout, fatData, 2)
            }
        } else if (item.type.contentEquals("checkbox")) { //多选
            val checkboxList = arrayListOf<String>() //初始化多选集合
            if (item.required.contentEquals("1")) {//为1 必选 0 可选
                checkboxList.add(data!![0]?.product_option_value_id) //todo 暂时先这么写
                val map = HashMap<String, ArrayList<String>>()
                map.put("${item.product_option_id}", checkboxList)
                allList.add(map)
                setBackground(floatLayout, item, 3)
            } else {
                val map = HashMap<String, ArrayList<String>>()
                map.put("${item.product_option_id}", checkboxList)
                allList.add(map)
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
//            if (currentChildCount != it.size) { //todo 这个判断...
                floatLayout?.removeAllViews()
                for (i in 0 until it.size) {
                    addItemToFloatLayout(floatLayout, it, i, id, type)
                }
//            }
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
        val sData = data
        val isSelect = sData[index].is_select //是否选中  todo 1
        val content = data[index].name  //内容
        val currentChildCount = floatLayout?.childCount //获取floatLayout item 个数
        val tv = TextView(mContext)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f) //设置字体大小
        tv.setText(content)

        if (selectCheck(fatherId,data[index].product_option_value_id)) {//选中
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
        /*tv.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                floatLayout?.removeAllViews()
                when (type) {
                    1 -> {//必选单选
                        LogTool.e(
                            "tag1",
                            "index:$index   product_option_id：$fatherId  isSelect:$isSelect"
                        )
                        if (sData[index].is_select) { //选中态 就 不做操作
                            return
                        } else {
                            LogTool.e("ee1","${sData[index].is_select}")
                            sData = retState(sData, false)
                            sData[index].is_select = true
                            LogTool.e("ee12","${sData[index].is_select}")
                            setMyData(sData, fatherId)
                        }
                    }
                    2 -> {//非必选单选
                        val state = sData[index].is_select
                        sData = retState(sData, false)
                        sData[index].is_select = !state
                        setMyData(sData, fatherId)
                        LogTool.e(
                            "tag2",
                            "index:$index   product_option_id：$fatherId   isSelect:$isSelect"
                        )
                    }
                    3 -> {//必选多选
                        //先获取选中个数
                        var isSelectNum: Int = 0
                        for (i in 0 until sData.size) {
                            if (sData[i].is_select) {
                                isSelectNum++
                            }
                        }
                        //确定 ->取消
                        if (sData[index].is_select && isSelectNum < 2) { //如果只剩一个选中
                            return
                        } else if (sData[index].is_select && isSelectNum >= 2) { //如果还有多个
                            sData[index].is_select = false
                            setMyData(sData, fatherId)
                        }
                        //取消 -> 确定
                        if (!sData[index].is_select) {
                            sData[index].is_select = true
                            setMyData(sData, fatherId)
                        }
                        LogTool.e(
                            "tag3",
                            "index:$index   product_option_id：$fatherId   isSelect:$isSelect"
                        )
                    }
                    4 -> {//非必选多选
                        val state = sData[index].is_select
                        sData[index].is_select = !state
                        setMyData(sData, fatherId)
                        LogTool.e(
                            "tag4",
                            "index:$index   product_option_id：$fatherId   isSelect:$isSelect"
                        )
                    }
                }
                notifyDataSetChanged()
            }
        })*/
        tv.setOnClickListener {
            when (type) {
                1 -> {//必选单选
                    val isCheck = selectCheck(fatherId,data[index].product_option_value_id)
                    if (isCheck) { //选中态 就 不做操作
                        return@setOnClickListener
                    } else {
                        floatLayout?.removeAllViews()
//                        addCheck(fatherId,data[index].product_option_value_id,1)
                        for (i in 0 until allList.size){
                            if(allList[i].containsKey(fatherId)){
                                allList[i][fatherId]?.clear()
                                allList[i][fatherId]?.add(data[index].product_option_value_id)
                                break
                            }
                        }
                    }
                    LogTool.e("tag2","isCheck：$isCheck  fatherId:$fatherId  index:$index")
                }
                2 -> {//非必选单选
                    floatLayout?.removeAllViews()
                    val isCheck = selectCheck(fatherId,data[index].product_option_value_id)
                    if(!isCheck){//如果开始未选中
                        for (i in 0 until allList.size){
                            if(allList[i].containsKey(fatherId)){
                                allList[i][fatherId]?.clear()
                                allList[i][fatherId]?.add(data[index].product_option_value_id)
                                break
                            }
                        }
//                        addCheck(fatherId,data[index].product_option_value_id,2)
                    }else{
                        for (i in 0 until allList.size){
                            if(allList[i].containsKey(fatherId)){
                                allList[i][fatherId]?.clear()
                                allList[i][fatherId]?.remove(data[index].product_option_value_id)
                                break
                            }
                        }
//                        removeCheck(fatherId,data[index].product_option_value_id,2)
                    }
                }
                3 -> {//必选多选
                    //先获取选中个数
                    val isSelectNum: Int = getCheckSize(fatherId)
                    val isCheck : Boolean= selectCheck(fatherId,data[index].product_option_value_id)
                    //取消 -> 确定
                    if (!isCheck) {
                        floatLayout?.removeAllViews()
//                        addCheck(fatherId,data[index].product_option_value_id,3)
                        for (i in 0 until allList.size){
                            if(allList[i].containsKey(fatherId)){
                                allList[i][fatherId]?.add(data[index].product_option_value_id)
                                break
                            }
                        }
                    }
                    //确定 ->取消
                    if (isCheck && isSelectNum < 2) { //如果只剩一个选中
                        return@setOnClickListener
                    } else if (isCheck && isSelectNum >= 2) { //如果还有多个
                        floatLayout?.removeAllViews()
//                        removeCheck(fatherId,data[index].product_option_value_id,3)
                        for (i in 0 until allList.size){
                            if(allList[i].containsKey(fatherId)){
                                allList[i][fatherId]?.remove(data[index].product_option_value_id)
                                break
                            }
                        }
                    }
                    LogTool.e("tag3","isSelectNum:$isSelectNum  isCheck：$isCheck  fatherId:$fatherId  index:$index")
                }
                4 -> {//非必选多选
                    floatLayout?.removeAllViews()
                    val isCheck : Boolean= selectCheck(fatherId,data[index].product_option_value_id)
                    if(!isCheck){ //未选中
                        for (i in 0 until allList.size){
                            if(allList[i].containsKey(fatherId)){
                                allList[i][fatherId]?.clear()
                                allList[i][fatherId]?.add(data[index].product_option_value_id)
                                break
                            }
                        }
//                        addCheck(fatherId,data[index].product_option_value_id,4)
                    }else{
                        for (i in 0 until allList.size){
                            if(allList[i].containsKey(fatherId)){
                                allList[i][fatherId]?.clear()
                                allList[i][fatherId]?.remove(data[index].product_option_value_id)
                                break
                            }
                        }
//                        removeCheck(fatherId,data[index].product_option_value_id,4)
                    }
                }
            }
            notifyDataSetChanged()
        }
        floatLayout?.addView(tv, lp)
        //添加点击事件
        /*floatLayout?.getChildAt(index)?.setOnClickListener {
            when (type) {
                1 -> {//必选单选
                    val isCheck = selectCheck(fatherId,data[index].product_option_value_id)
                    if (isCheck) { //选中态 就 不做操作
                        return@setOnClickListener
                    } else {
                        floatLayout.removeAllViews()
                        addCheck(fatherId,data[index].product_option_value_id,1)
                    }
                }
                2 -> {//非必选单选
                    floatLayout.removeAllViews()
                    val isCheck = selectCheck(fatherId,data[index].product_option_value_id)
                    if(!isCheck){//如果开始未选中
                        addCheck(fatherId,data[index].product_option_value_id,2)
                    }else{
                        removeCheck(fatherId,data[index].product_option_value_id,2)
                    }
                }
                3 -> {//必选多选
                    //先获取选中个数
                    val isSelectNum: Int = getCheckSize(fatherId)
                    val isCheck : Boolean= selectCheck(fatherId,data[index].product_option_value_id)
                    //确定 ->取消
                    if (isCheck && isSelectNum < 2) { //如果只剩一个选中
                        return@setOnClickListener
                    } else if (isCheck && isSelectNum >= 2) { //如果还有多个
                        floatLayout.removeAllViews()
                        removeCheck(fatherId,data[index].product_option_value_id,3)
                    }
                    //取消 -> 确定
                    if (!isCheck) {
                        floatLayout.removeAllViews()
                        LogTool.e("tag3","添加哎哎哎aaaaa")
                        addCheck(fatherId,data[index].product_option_value_id,3)
                    }
                    LogTool.e("tag3","isSelectNum:$isSelectNum  isCheck：$isCheck")
                }
                4 -> {//非必选多选
                    floatLayout.removeAllViews()
                    val isCheck : Boolean= selectCheck(fatherId,data[index].product_option_value_id)
                    if(!isCheck){ //未选中
                        addCheck(fatherId,data[index].product_option_value_id,4)
                    }else{
                        removeCheck(fatherId,data[index].product_option_value_id,4)
                    }
                }
            }
            notifyDataSetChanged()
        }*/
    }


    //查询集合是否包含选中数据源
    private  fun selectCheck(fatherId: String,sonId:String):Boolean{
        for (i in 0 until allList.size){
            if(allList[i].containsKey(fatherId)){
                if(allList[i].size == 0){ //这个是hashmap长度
                    LogTool.e("tag","hash 为空")
                    return false
                }else{
                    val selectData = allList[i][fatherId]
                    if(selectData?.size == 0){
                        LogTool.e("tag","存储集合 为空")
                        return false
                    }else{
                        if(selectData?.contains(sonId)!!){
                            return true
                        }else{
                            LogTool.e("tag","集合不包含该数据")
                            return false
                        }
                    }
                }
                break
            }
        }
        return false
    }
    //添加进返回集合
    private fun addCheck(fatherId: String,sonId:String,type: Int){
        if(selectCheck(fatherId,sonId)){//发现已存在
            LogTool.e("tagAAAAAAAAAAAAA","发现已存在的添加...")
            return
        }else{
            for (i in 0 until allList.size){
                if(allList[i].containsKey(fatherId)){
                    val selectData = allList[i][fatherId]
                    if(type<=2){ //单选
                        selectData?.clear()
                    }
                    selectData?.add(sonId)
                    allList[i].remove(fatherId)
                    allList[i].put(fatherId,selectData!!)
                }
                break
            }
        }
    }
    //从返回集合移除
    private fun removeCheck(fatherId: String,sonId:String,type: Int){
        if(selectCheck(fatherId,sonId)){//发现已存在
            return
        }else{
            for (i in 0 until allList.size){
                if(allList[i].containsKey(fatherId)){
                    val selectData = allList[i][fatherId]
                    if(type==1) { //必选单选
                        return
                    } else if(type == 3 && selectData?.size == 1){
                        return
                    }else{
                        if(selectData?.contains(sonId)!!){
                            selectData.remove(sonId)
                            allList[i].remove(fatherId)
                            allList[i].put(fatherId,selectData)
                        }
                    }
                }
                break
            }
        }
    }
    //返回指定选中集合的长度
    private fun getCheckSize(fatherId: String):Int{
        for (i in 0 until allList.size){
            if(allList[i].containsKey(fatherId)) {
                val checkList = allList[i].get(fatherId)
                if(checkList == null){
                    return  0
                }else{
                    return checkList.size
                }
            }
        }
        return 0
    }
    //返回选中数据
    fun getAllCheck(): ArrayList<HashMap<String, ArrayList<String>>> {
        return allList ?: ArrayList<HashMap<String, ArrayList<String>>>()
    }
    fun setAllCheck(checkList:ArrayList<HashMap<String, ArrayList<String>>>){
        this.allList = checkList
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


    //更新返回的数据源 todo 废弃
    private fun updRetData(data: List<ZyProduct.ProductOptionValue>, fatherId: String,type: Int) {
        allList.let {
            var isExist = false
            for (i in 0 until it.size) {
                if (it[i].containsKey(fatherId)) {//如果有这个key
                    isExist = true //数据源存在
                    val value = it[i].get(fatherId)
                    if(type == 1 || type == 2){
                        value?.clear()
                    }
                    for (y in 0 until data.size) { //把选中的id 添加到返回列表
                        if (data[y].is_select) {
                            value?.add(data[y].product_option_value_id) //todo 暂时先这么写
                        }
                    }
                }
                break
            }
            if (!isExist) {
                val list = ArrayList<String>()
                for (y in 0 until data.size) {
                    if (data[y].is_select) {
                        list.add(data[y].product_option_value_id) //todo 暂时先这么写
                    }
                }
                val map = HashMap<String, ArrayList<String>>()
                map.put(fatherId, list)
                allList.add(map)
            }
        }
    }
    //更新数据源并刷新  todo 废弃
    private fun setMyData(data: List<ZyProduct.ProductOptionValue>, fatherId: String) {
        theData?.let {
            //更新数据源
            for (i in 0 until it.size) {
                if (it[i].product_option_id.contentEquals(fatherId)) {
//                    val upd = it[i]
//                    upd.product_option_value = data
                    it[i].product_option_value = data
                    break
                }
            }
            //更新列表数据
//            updRetData(data, fatherId,type)
            //刷新列表
//            replaceData(it)
            setTheData(it)
        }
    }

    //单选状态全部变更 todo 废弃
    private fun retState(
        data: List<ZyProduct.ProductOptionValue>?,
        valueState: Boolean = false
    ): List<ZyProduct.ProductOptionValue> {
        for (i in 0 until data?.size!!) {
            data[i].is_select = valueState
        }
        return data
    }
}