package com.example.juanshichang.activity

import android.content.Intent
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.BenefitBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
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
        EaRet.setOnClickListener(this)
        syDetail.setOnClickListener(this)
        putRecord.setOnClickListener(this)
        putForward.setOnClickListener(this)
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        getBenefit() //请求网络
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.EaRet->{
                finish()
            }
            R.id.putForward ->{ //提现
                val intent = Intent(this@EarningsActivity,TakeOutActivity::class.java)
                intent.putExtra("type",1)
                goStartActivity(this@EarningsActivity,intent)
            }
            R.id.syDetail ->{//收益结算明细
                ToastUtil.showToast(this@EarningsActivity,"暂未开放收益入口")
            }
            R.id.putRecord ->{//提现记录
                val intent = Intent(this@EarningsActivity,TakeOutActivity::class.java)
                intent.putExtra("type",2)
                goStartActivity(this@EarningsActivity,intent)
            }
        }
    }
    private fun getBenefit(){
        HttpManager.getInstance().post(Api.BENFIT, Parameter.getBenefitMap(),object : Subscriber<String>(){
            override fun onNext(result: String?) {
                //todo后台返回数据结构问题，暂时这样处理
                val str =result?.substring(result?.indexOf("{"),result.length)

                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
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
                LogTool.e("onCompleted", "收益加载完成!")
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onError", "收益加载失败!" + e)
            }

        })
    }
    private fun setUiData(){
        if(benefit!=null){
            balanceZh.text =  "¥"+Util.getFloatPrice(benefit!!.balance) //账户余额
            todFk.text = "付款笔数\n"+benefit!!.current_day_order_paid
            todSy.text = "预估收益\n¥"+Util.getFloatPrice(benefit!!.current_day_pre_benefit.toLong())
            todQt.text = "其它\n¥"+Util.getFloatPrice(benefit!!.current_day_other.toLong())
            ytodFk.text = "付款笔数\n"+benefit!!.last_day_order_paid
            ytodSy.text = "预估收益\n¥"+Util.getFloatPrice(benefit!!.last_day_pre_benefit.toLong())
            ytodQt.text = "其它\n¥"+Util.getFloatPrice(benefit!!.last_day_other.toLong())
            monFk.text = "上月收益\n¥"+Util.getFloatPrice(benefit!!.last_month_benefit.toLong())
            monSy.text = "预估收益\n¥"+Util.getFloatPrice(benefit!!.current_month_pre_benefit.toLong())
            monQt.text = "其它\n¥"+Util.getFloatPrice(benefit!!.current_month_other.toLong())
        }else{
            getBenefit()
        }
    }
}
