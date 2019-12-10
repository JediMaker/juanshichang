package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import com.alipay.sdk.app.PayTask
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.ConOrderBean
import com.example.juanshichang.bean.SettleAccBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.UtilsBigDecimal
import com.example.juanshichang.utils.pay.PayResult
import com.google.gson.Gson
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import kotlinx.android.synthetic.main.activity_settle_acc.*
import kotlinx.coroutines.Runnable
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/12/4 17:59
 * @文件作用: 结算台
 */
class SettleAccActivity : BaseActivity(), View.OnClickListener {
    private var address_id: String? = null //地址id
    private var cartList: ArrayList<String>? = null
    private var iData: SettleAccBean.SettleAccBeans? = null
    private val ALiPAY_FLAG: Int = 1
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
//            super.handleMessage(msg)
            when (msg.what) {
                ALiPAY_FLAG -> { //支付宝
                    val payResult = PayResult(msg.obj as Map<String, String>)
                    val resultInfo = payResult.result
                    val resultStatus = payResult.resultStatus
                    // 判断resultStatus 为9000则代表支付成功
                    if(TextUtils.equals(resultStatus,"9000")){
                        ToastUtil.showToast(this@SettleAccActivity,payResult.toString())
                        showMyLoadD(QMUITipDialog.Builder.ICON_TYPE_SUCCESS,"支付成功",true)
                        sendEmptyMessageDelayed(0,1200)
                    }else{
                        showMyLoadD(QMUITipDialog.Builder.ICON_TYPE_FAIL,"支付失败",true)
                        sendEmptyMessageDelayed(0,1200)
                        ToastUtil.showToast(this@SettleAccActivity,"失败？：${payResult.toString()}")
                    }
                }
                0 ->{
                    myLoading?.dismiss()
                    finish()
                }
            }
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_settle_acc
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@SettleAccActivity, R.color.white)
        if (null != intent.getBundleExtra("bundle")) {
            val bundle = intent.getBundleExtra("bundle")
            address_id = bundle.getString("address_id")
            cartList = bundle.getStringArrayList("checkAll")
            showProgressDialog()
            if (cartList != null && address_id != null) {
                sucOrderFrom(cartList!!, address_id!!)
            } else {
                ToastUtil.showToast(this@SettleAccActivity, "网络异常,请稍后重新提交订单")
                finish()
            }
        } else {
            ToastUtil.showToast(this@SettleAccActivity, "数据异常,请稍后重试。")
            finish()
        }
    }

    override fun initData() {
        setARet.setOnClickListener(this) //返回
        goZfb.setOnClickListener(this) //支付宝
        goVx.setOnClickListener(this) //微信
    }

    override fun onClick(v: View?) {
        when (v) {
            setARet -> {
                finish()
            }
            goZfb -> {
                if (iData != null) {
                    val alipayInfo = iData?.data?.alipay_order_info
                    val runnable: Runnable = object : Runnable {
                        override fun run() {
                            val alipay = PayTask(this@SettleAccActivity)
                            val result = alipay.payV2(alipayInfo, true)
                            LogTool.e("aliPay", result.toString())
                            val msg = Message()
                            msg.what = ALiPAY_FLAG
                            msg.obj = result
                            mHandler.sendMessage(msg)
                        }
                    }
                    //异步调用
                    val payThread = Thread(runnable)
                    payThread.start()
                }
            }
            goVx -> {
                ToastUtil.showToast(this@SettleAccActivity, "去微信...")
            }
        }
    }

    //完成订单
    private fun sucOrderFrom(list: ArrayList<String>, address_id: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.CHECKOUTCONFIRM,
            NewParameter.getSucMap(list, address_id),
            object : Subscriber<String>() {
                override fun onNext(t: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optString(JsonParser.JSON_CODE)!!.equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(
                                this@SettleAccActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val data = Gson().fromJson(t, SettleAccBean.SettleAccBeans::class.java)
                            iData = data
                            val price = data.data.total
                            val priceStr = "￥${UtilsBigDecimal.mul(price, 1.toDouble(), 2)}"
                            endPrice.text = priceStr
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "订单完成请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "订单完成请求失败: ${e.toString()}")
                    finish()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
}
