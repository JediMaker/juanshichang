package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.BenefitBean
import com.example.juanshichang.bean.OrdersBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.activity_earnings.*
import kotlinx.coroutines.Runnable
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/8/16 15:03
 * @文件作用: 我的收益页面
 */
class EarningsActivity : BaseActivity(), View.OnClickListener{
    private var benefit:BenefitBean.Data? = null
    override fun getContentView(): Int {
        return R.layout.activity_earnings;
    }

    override fun initView() {
//        StatusBarUtil.addStatusViewWithColor(this, R.color.colorPrimary)
        isOne.setPadding(0,QMUIStatusBarHelper.getStatusbarHeight(this),0,0)
        getBenefit()
        EaRet.setOnClickListener(this)
        syDetail.setOnClickListener(this)
        putRecord.setOnClickListener(this)
    }

    override fun initData() {

    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.EaRet->{
                finish()
            }
            R.id.syDetail ->{//收益结算明细
                ToastUtil.showToast(this@EarningsActivity,"暂未开放收益入口")
            }
            R.id.putRecord ->{//提现记录
                ToastUtil.showToast(this@EarningsActivity,"暂未开放提现入口")
            }
        }
    }
    private fun getBenefit(){
        HttpManager.getInstance().post(Api.BENFIT, Parameter.getBenefitMap(),object : Subscriber<String>(){
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@EarningsActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else{
                        val data:BenefitBean.BenefitBeans =  Gson().fromJson(str,BenefitBean.BenefitBeans::class.java)
                        benefit = data.data
                        this@EarningsActivity.runOnUiThread(object : Runnable{
                            override fun run() {
                                setUiData()
                            }
                        })
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted", "收益加载完成!")
            }

            override fun onError(e: Throwable?) {
                Log.e("onError", "收益加载失败!" + e)
            }

        })
    }
    private fun setUiData(){
        if(benefit!=null){
            balanceZh.text =  "¥"+benefit!!.balance //账户余额
            todFk.text = "付款笔数\n"+benefit!!.current_day_order_paid
            todSy.text = "预估收益\n¥"+benefit!!.current_day_pre_benefit
            todQt.text = "其它\n¥"+benefit!!.current_day_other
            ytodFk.text = "付款笔数\n"+benefit!!.last_day_order_paid
            ytodSy.text = "预估收益\n¥"+benefit!!.last_day_pre_benefit
            ytodQt.text = "其它\n¥"+benefit!!.last_day_other
            monFk.text = "上月收益\n¥"+benefit!!.last_month_benefit
            monSy.text = "预估收益\n¥"+benefit!!.current_month_benefit
            monQt.text = "其它\n¥"+benefit!!.current_month_pre_benefit
        }else{
            getBenefit()
        }
    }
}
