package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.ZySearchBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_order_detail.*
import org.json.JSONObject
import rx.Subscriber

class OrderDetailActivity : BaseActivity(), View.OnClickListener {
    private var orderId:String = ""
    override fun getContentView(): Int {
        return R.layout.activity_order_detail
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@OrderDetailActivity, R.color.white)
        if(null != intent.getStringExtra("orderid")){
            orderId = intent.getStringExtra("orderid")
            getOrderData(orderId)
        }else{
            ToastUtil.showToast(this@OrderDetailActivity,"暂无订单状态信息")
            finish()
        }
    }

    override fun initData() {
        oDRet.setOnClickListener(this) //返回
        applyTk.setOnClickListener(this) //申请退款
        alignPay.setOnClickListener(this) //再次购买
        ddCopy.setOnClickListener(this) //复制订单编号
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.oDRet ->{
                finish()
            }
            R.id.applyTk ->{

            }
            R.id.alignPay ->{

            }
            R.id.ddCopy ->{

            }
        }
    }
    //商品详情
    private fun getOrderData(order_id: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL)
            .post(Api.NEWORDER, NewParameter.getOrderDhMap(order_id), object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        val jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(
                                this@OrderDetailActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {

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
}
