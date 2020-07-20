package com.example.juanshichang.activity

import android.content.Intent
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.OrdersAdapter
import com.example.juanshichang.base.*
import com.example.juanshichang.bean.OrdersBean
import com.example.juanshichang.bean.OrdersBeanT
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastTool
import com.example.juanshichang.utils.ToastUtil
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_order_form.*
import kotlinx.coroutines.Runnable
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者：yzq
 * @创建时间：2019/9/3 16:32
 * @文件作用: 订单
 */
class OrderFormActivity() : BaseActivity(), View.OnClickListener {

    private var ordersListData: ArrayList<OrdersBeanT.Data>? = null
    private var ordersAdpater: OrdersAdapter? = null
    private var type: Int = 0
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    ordersAdpater?.emptyView =
                        View.inflate(this@OrderFormActivity, R.layout.activity_not_null, null)
                    if (ordersListData != null && ordersListData?.size != 0) {
                        ordersAdpater?.setNewData(ordersListData)
                    } else {
                        ordersAdpater?.setNewData(null)
                        ToastTool.showToast(this@OrderFormActivity, "暂无数据")
                    }
//                    ToastTool.showToast(this@OrderFormActivity, "" + ordersListData?.size)
                }
            }
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_order_form
    }

    override fun initView() {
        oldTab=-1
        StatusBarUtil.addStatusViewWithColor(this@OrderFormActivity, R.color.colorPrimary)
//        orderTop.setPadding(0, QMUIStatusBarHelper.getStatusbarHeight(this), 0, 0)
        setTab()
    }

    override fun initData() {
        if (Int.MAX_VALUE != intent.getIntExtra("type", Int.MAX_VALUE)) {
            type = intent.getIntExtra("type", Int.MAX_VALUE)
        }
        ordersListData = ArrayList()
        detailTab.selectTab(detailTab.getTabAt(type))
        if(type==0){
            ordTast.postDelayed(object : Runnable {
                override fun run() {
//                getOrders(0,20)
                    showProgressDialog()
                    orderList()
                }
            }, 300)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.orRet -> {
                finish()
            }
            R.id.orSearch -> {
                //todo 调用搜索订单接口
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ordersAdpater?.setOnItemChildClickListener(object :
            BaseQuickAdapter.OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                when (view?.id) {
                    R.id.goPay -> { //去支付
                        ordersListData?.let {
                            if ("已失效".equals(ordersAdpater!!.data[position].status)) {//取消状态订单可删除
                                delOrder(ordersAdpater!!.data[position].order_id)
                            } else if ("已发货".equals(ordersAdpater!!.data[position].status)) {//待支付状态订单{
                                receiveOrder(ordersAdpater!!.data[position].order_id)
                            } else if ("已提交".equals(ordersAdpater!!.data[position].status)) {//待支付状态订单{
                                val intent =
                                    Intent(this@OrderFormActivity, SettleAccActivity::class.java)
                                intent.putExtra("orderid", ordersAdpater!!.data[position].order_id)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    R.id.orderGo -> { //查看订单详情
                        ordersListData?.let {
                            val intent =
                                Intent(this@OrderFormActivity, OrderDetailActivity::class.java)
                            intent.putExtra("orderid", ordersAdpater!!.data[position].order_id)
                            startActivity(intent)

                        }
                    }
                }
            }

        })
    }

    private fun setTab() {
        detailTab.addTab(detailTab.newTab().setText("全部"))
        detailTab.addTab(detailTab.newTab().setText("待付款"))
        detailTab.addTab(detailTab.newTab().setText("待发货"))
        detailTab.addTab(detailTab.newTab().setText("待收货"))
        detailTab.addOnTabSelectedListener(mTabLayoutBottom)
        detailList.layoutManager =
            LinearLayoutManager(this@OrderFormActivity, RecyclerView.VERTICAL, false)
        ordersAdpater = OrdersAdapter()
        detailList.adapter = ordersAdpater
        orRet.setOnClickListener(this)
        orSearch.setOnClickListener(this)
    }

    private var oldTab: Int = -1
    private val mTabLayoutBottom = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {

        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
        }

        override fun onTabSelected(t: TabLayout.Tab?) {
            if (ordersListData != null && ordersListData?.size != 0) {
                ordersListData?.clear()
            }
            when (t?.text) {
                "全部" -> {
                    type = 0
                }
                "待付款" -> {
                    type = 1
                }
                "待发货" -> {
                    type = 2
                }
                "待收货" -> {
                    type = 3
                }
            }
            if (t?.position != oldTab) {
                ordTast.postDelayed(object : Runnable {
                    override fun run() {
//                getOrders(0,20)
                        showProgressDialog()
                        orderList()
                    }
                }, 300)
                /* if (ordersListData != null && ordersListData?.size != 0) {
                     showProgressDialog()
                     ordersAdpater?.setNewData(null)
                     LogTool.e("text", "${t?.text}")
                     ordTast.postDelayed(object : Runnable {
                         override fun run() {
                             when (t?.text) {
                                 "全部" -> {
                                     ordersAdpater?.setNewData(ordersListData)
                                 }
                                 "待付款" -> {
                                     val d = getTabData("已提交")
                                     if (d != null) {
                                         ordersAdpater?.setNewData(d)
                                     } else {
                                         ordersAdpater?.setNewData(null)
                                         ToastTool.showToast(this@OrderFormActivity, "暂无数据")
                                     }
                                 }
                                 "待发货" -> {
                                     val d = getTabData("已付款")
                                     if (d != null) {
                                         ordersAdpater?.setNewData(d)
                                     } else {
                                         ordersAdpater?.setNewData(null)
                                         ToastTool.showToast(this@OrderFormActivity, "暂无数据")
                                     }
                                 }
                                 "待收货" -> {
                                     val d = getTabData("已发货")
                                     if (d != null) {
                                         ordersAdpater?.setNewData(d)
                                     } else {
                                         ordersAdpater?.setNewData(null)
                                         ToastTool.showToast(this@OrderFormActivity, "暂无数据")
                                     }
                                 }
                             }
                             dismissProgressDialog()
                         }
                     }, 500)
                 } else {
                     ToastUtil.showToast(this@OrderFormActivity, "您还没有任何订单")
                 }*/
                oldTab = t?.position!!
            } else {
                ToastTool.showToast(this@OrderFormActivity, "您已在该列表下")
            }
        }
    }
    var newData: ArrayList<OrdersBeanT.Data>? = null
    fun getTabData(tit: String): List<OrdersBeanT.Data>? {
        newData = ArrayList()
        for (i in 0 until ordersListData!!.size) {
            if (ordersListData!![i].status.equals(tit)) {
                newData?.add(ordersListData!![i])
            }
        }
        if (newData?.size != 0) {
            return newData
        }
        return null
    }

    // jsc 请求订单列表
    private fun getOrders(offset: Int, limit: Int) {
        HttpManager.getInstance()
            .post(Api.ORDERS, Parameter.getOrders(offset, limit), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str = result?.substring(result?.indexOf("{"), result.length)

                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        jsonObj = JSONObject(str)
                        if (!jsonObj.optBoolean(JsonParser.JSON_Status)
                        ) {
                            ToastUtil.showToast(
                                this@OrderFormActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val ordersList: OrdersBean.OrdersBeans =
                                Gson().fromJson(str, OrdersBean.OrdersBeans::class.java)
                            if (ordersListData != null && ordersListData?.size != 0) {
                                ordersListData?.clear()
                            }
//                        ordersListData?.addAll(ordersList.data)
//                        handler.sendEmptyMessage(0)
                        }
                    }
                }

                override fun onCompleted() {
                    this@OrderFormActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            dismissProgressDialog()
                        }
                    })
                    LogTool.e("onCompleted", "订单加载完成!")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "订单加载失败!" + e)
                }

            })
    }

    //mxsh 最新请求订单列表
    private fun orderList() {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.NEWHISORDER,
            NewParameter.getBaseTMap(type),
            object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val t = result?.substring(result?.indexOf("{"), result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            ToastUtil.showToast(
                                this@OrderFormActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            dismissProgressDialog()
                            if (ordersListData != null && ordersListData?.size != 0) {
                                ordersListData?.clear()
                            }
                            try {
                                val data = Gson().fromJson(t, OrdersBeanT.OrdersBeanTs::class.java)
                                ordersListData?.addAll(data.data)
                                handler.sendEmptyMessage(0)
                            } catch (e: Exception) {
                                handler.sendEmptyMessage(0)
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "订单列表请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    dismissProgressDialog()
                    LogTool.e("onCompleted", "订单列表请求失败: ${e.toString()}")
                }
            })
    }

    //mxsh 确认收货
    private fun receiveOrder(orderId: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.ORDERRECEIVE,
            NewParameter.getBaseReceiveOrderMap(orderId),
            object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val t = result?.substring(result?.indexOf("{"), result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            ToastUtil.showToast(
                                this@OrderFormActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            orderList()
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "订单列表请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "订单列表请求失败: ${e.toString()}")
                }
            })
    }

    //mxsh 删除订单
    private fun delOrder(orderId: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.ORDERDEL,
            NewParameter.getBaseReceiveOrderMap(orderId),
            object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val t = result?.substring(result?.indexOf("{"), result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            ToastUtil.showToast(
                                this@OrderFormActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            orderList()
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "订单列表请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "订单列表请求失败: ${e.toString()}")
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
