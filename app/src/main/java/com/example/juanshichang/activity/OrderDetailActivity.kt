package com.example.juanshichang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.OrderDetailAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.OrderDetailBean
import com.example.juanshichang.bean.ZySearchBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.order_detail_end.*
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/12/18 17:42
 * @文件作用: 订单详情
 */
class OrderDetailActivity : BaseActivity(), View.OnClickListener {
    private var orderId: String = ""
    private var data: OrderDetailBean.Data? = null
    private var oDAdapter: OrderDetailAdapter? = null
    override fun getContentView(): Int {
        return R.layout.activity_order_detail
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@OrderDetailActivity, R.color.colorPrimary)
        if (null != intent.getStringExtra("orderid")) {
            orderId = intent.getStringExtra("orderid")
            oDAdapter = OrderDetailAdapter()
            oDList.adapter = oDAdapter
            getOrderData(orderId)
        } else {
            ToastUtil.showToast(this@OrderDetailActivity, "暂无订单状态信息")
            finish()
        }
    }

    override fun initData() {
        oDRet.setOnClickListener(this) //返回
        applyTk.setOnClickListener(this) //申请退款
        alignPay.setOnClickListener(this) //再次购买
        ddCopy.setOnClickListener(this) //复制订单编号
        oDAdapter?.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                data?.let {
                    val id = it.products
                    val intent = Intent(this@OrderDetailActivity, ShangPinZyContains::class.java)
                    intent.putExtra("product_id", id[position].product_id)
                    startActivity(intent)
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.oDRet -> {
                finish()
            }
            R.id.applyTk -> { //申请退款

            }
            R.id.alignPay -> { //再次购买
                data?.let {
                    val id = it.products
                    val intent = Intent(this@OrderDetailActivity, ShangPinZyContains::class.java)
                    intent.putExtra("product_id", id[0].product_id)
                    startActivity(intent)
                }
            }
            R.id.ddCopy -> { // 复制订单号

            }
        }
    }

    //商品详情
    private fun getOrderData(order_id: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL)
            .post(
                Api.NEWORDER,
                NewParameter.getOrderDhMap(order_id),
                object : Subscriber<String>() {
                    override fun onNext(result: String?) {
                        //todo后台返回数据结构问题，暂时这样处理
                        val str = result?.substring(result?.indexOf("{"), result.length)
                        if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                            val jsonObj: JSONObject = JSONObject(str)
                            if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                                ToastUtil.showToast(
                                    this@OrderDetailActivity,
                                    jsonObj.optString(JsonParser.JSON_MSG)
                                )
                            } else {
                                val bean = Gson().fromJson(
                                    str,
                                    OrderDetailBean.OrderDetailBeans::class.java
                                )
                                data = bean.data
                                setUIData(data)
                            }
                        }
                    }

                    override fun onCompleted() {
                        LogTool.e("onCompleted", "商品详情 完成")
                    }

                    override fun onError(e: Throwable?) {
                        LogTool.e("onError", "商品详情 Error")
                    }
                })
    }

    private fun setUIData(data: OrderDetailBean.Data?) {
        data?.let {
            oDAdapter?.setNewData(it.products)
            ddBh.text = it.order_num
            xdDate.text = it.date_added
            //地址信息
            val site = it.shipping_address
            oDName.text = site.name
            oDPhone.text = site.iphone
            oDSite.text = site.address_detail
            if (it.products.size != 0) {
                val v: View =
                    View.inflate(this@OrderDetailActivity, R.layout.order_detail_end, null)
                var sum: Int = 0
                for (i in 0 until it.products.size) {
                    sum += it.products[i].quantity.toInt()
                }
                v.findViewById<TextView>(R.id.rigPrice).text = it.totals[0].text
                v.findViewById<TextView>(R.id.parentTit).text = "共$sum 件商品"
                v.findViewById<TextView>(R.id.leftTit).text = it.status

                oDAdapter?.addFooterView(v)
            }

        }
    }
}
