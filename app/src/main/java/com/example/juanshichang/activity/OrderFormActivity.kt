package com.example.juanshichang.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juanshichang.R
import com.example.juanshichang.adapter.OrdersAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.OrdersBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastTool
import com.example.juanshichang.utils.ToastUtil
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.activity_order_form.*
import kotlinx.android.synthetic.main.activity_order_form.view.*
import org.json.JSONObject
import rx.Subscriber
/**
 * @作者：yzq
 * @创建时间：2019/9/3 16:32
 * @文件作用: 订单
 */
class OrderFormActivity : BaseActivity(), View.OnClickListener {
    private var ordersListData:ArrayList<OrdersBean.Data>? = null
    private var ordersAdpater:OrdersAdapter ? = null
    private val handler:Handler = object : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                0 ->{
                    ordersAdpater?.setNewData(ordersListData)
                    ToastUtil.showToast(this@OrderFormActivity,""+ordersListData?.size)
                }
            }
        }
    }
    override fun getContentView(): Int {
        return R.layout.activity_order_form
    }

    override fun initView() {
        orderTop.setPadding(0, QMUIStatusBarHelper.getStatusbarHeight(this),0,0)
        setTab()
    }

    override fun initData() {
        ordersListData = ArrayList()
        getOrders(0,20)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.orRet->{
                finish()
            }
            R.id.orSearch->{

            }
        }
    }
    private fun setTab() {
        detailTab.addTab(detailTab.newTab().setText("全部"))
        detailTab.addTab(detailTab.newTab().setText("已付款"))
        detailTab.addTab(detailTab.newTab().setText("已结算"))
        detailTab.addTab(detailTab.newTab().setText("已失效"))
        detailTab.addOnTabSelectedListener(mTabLayoutBottom)
        detailList.layoutManager = LinearLayoutManager(this@OrderFormActivity,RecyclerView.VERTICAL,false)
        ordersAdpater = OrdersAdapter(R.layout.item_orders)
        ordersAdpater?.emptyView = View.inflate(this, R.layout.activity_not_null, null)
        detailList.adapter = ordersAdpater
        orRet.setOnClickListener(this)
        orSearch.setOnClickListener(this)
    }
    private val mTabLayoutBottom = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {

        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
        }

        override fun onTabSelected(t: TabLayout.Tab?) {
            ToastTool.showToast(this@OrderFormActivity,"点击到了"+t?.position)
        }
    }
    private fun getOrders(offset:Int,limit:Int){
        HttpManager.getInstance().post(Api.ORDERS,Parameter.getOrders(offset,limit),object : Subscriber<String>(){
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@OrderFormActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else{
                        val ordersList:OrdersBean.OrdersBeans = Gson().fromJson(str,OrdersBean.OrdersBeans::class.java)
                        if (ordersListData != null && ordersListData?.size != 0){
                            ordersListData?.clear()
                        }
                        ordersListData?.addAll(ordersList.data)
                        handler.sendEmptyMessage(0)
                    }
                }
            }

            override fun onCompleted() {
                LogTool.e("onCompleted", "订单加载完成!")
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onError", "订单加载失败!" + e)
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
