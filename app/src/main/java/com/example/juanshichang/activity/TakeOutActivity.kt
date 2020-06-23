package com.example.juanshichang.activity

import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.TakeOutAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.TakeOutBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.*
import com.example.juanshichang.widget.EditInputFilter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_take_out.*
import kotlinx.android.synthetic.main.activity_take_outt.*
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者：yzq
 * @创建时间：2019/9/17 18:15
 * @文件作用: 提现页面 该页面中：把提现列表和提现申请页面组合在了一起....
 */
class TakeOutActivity : BaseActivity(), View.OnClickListener{
    private var vType:Int = 0
    private var userYe:Double? = 0.0
    private var txAdpater:TakeOutAdapter? = null
    private var txList:ArrayList<TakeOutBean.Withdraw>? = null
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.takeFin->{
                finish()
            }
            R.id.goTx->{
                if(txBut()){
                   val amount = txMon.text.toString()
                   showProgressDialog()
                   startTxZfb(amount)
                }
            }
            R.id.txClose ->{
                txMon.setText("")
            }
        }
    }
    override fun getContentView(): Int {
        return R.layout.activity_take_out
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@TakeOutActivity, R.color.white)
        if(Int.MAX_VALUE != intent.getIntExtra("type", Int.MAX_VALUE)){
            vType = intent.getIntExtra("type", Int.MAX_VALUE)
        }
        if (vType == 1){ //提现按钮进入
            layoutOne.visibility = View.VISIBLE
            setOneUi()
        }
        if (vType == 2){
            txRecycler.visibility = View.VISIBLE
            showProgressDialog()
            pullTxList(1) //page 为页码 拓展备用字段....
            setReycler()
        }
        takeFin.setOnClickListener(this)
    }


    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        if(vType == 1){
            val aliPay = SpUtil.getIstance().user.ali_pay_account
            if (aliPay=="" || TextUtils.isEmpty(aliPay)){
                txPhone.text = "无账户"
            }else{
                txPhone.text = Util.getPhoneNTransition(aliPay.toString())
            }
        }
        if (vType == 2){
            //添加条目点击事件
            txAdpater?.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener{
                override fun onItemClick(
                    adapter: BaseQuickAdapter<*, *>?,
                    view: View?,
                    position: Int
                ) {

                }
            })
        }
    }
    private fun setReycler() { //这个是对 列表页面的操作
        val lm = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        txAdpater = TakeOutAdapter()
        txRecycler.layoutManager = lm
        txRecycler.adapter = txAdpater
        txList = ArrayList()
    }
    private fun setOneUi() {
        goTx.setOnClickListener(this)
        txClose.setOnClickListener(this)
        userYe = SpUtil.getIstance().user.balance.toDouble()
        baHint.text = "可提现余额${Util.getFloatPrice(SpUtil.getIstance().user.balance)}"
        //给Edit添加监听事件
        val filters: InputFilter = (EditInputFilter())
        txMon.filters = arrayOf(filters)
        txMon.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(edit: Editable?) {
                //edit  输入结束呈现在输入框中的信息

            }

            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
                //text  输入框中改变前的字符串信息
                //start 输入框中改变前的字符串的起始位置
                //count 输入框中改变前后的字符串改变数量一般为0
                // after 输入框中改变后的字符串与起始位置的偏移量
            }

            override fun onTextChanged(text : CharSequence?, start : Int, before : Int, count : Int) {
                //text  输入框中改变后的字符串信息
                //start 输入框中改变后的字符串的起始位置
                //before 输入框中改变前的字符串的位置 默认为0
                //count 输入框中改变后的一共输入字符串的数量
                if (text?.length!! > 0){
                    txClose.visibility = View.VISIBLE
                }else{
                    txClose.visibility = View.INVISIBLE
                }
            }

        })
    }
    //点击提现按钮后的操作
    private fun txBut():Boolean{
        val aliPay = SpUtil.getIstance().user.ali_pay_account
        val edit = txMon.text.toString()
        if(edit=="" || TextUtils.isEmpty(edit)){
            ToastTool.showToast(this@TakeOutActivity,"请输入提现金额")
            return false
        }
        if(aliPay=="" || TextUtils.isEmpty(aliPay)){
            ToastTool.showToast(this@TakeOutActivity,"请设置提现账户")
            goStartActivity(this@TakeOutActivity,SettingActivity())
            return false
        }
        val editFlo = edit.toDouble()
        if(editFlo < 0.1){
            ToastTool.showToast(this@TakeOutActivity,"提现金额不能少于0.1元")
            return false
        }
        if(editFlo > userYe!!){
           ToastTool.showToast(this@TakeOutActivity,"可用余额不足")
           txMon.setText("")
           return false
        }
        return true
    }

    private fun startTxZfb(amount:String){
        // ¥
        HttpManager.getInstance().post(Api.WITHDRAW, Parameter.getTxZfb(amount), object : Subscriber<String>() {
            override fun onNext(result: String?) {
                //todo后台返回数据结构问题，暂时这样处理
                val str =result?.substring(result?.indexOf("{"),result.length)

                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@TakeOutActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val data = jsonObj.getJSONObject("data")
                        val namount = data.getDouble("amount") //提现金额
                        val balance = data.getDouble("balance") //余额
                        val account = data.getString("account") //账户
                        val user = SpUtil.getIstance().user
                        user.balance = UtilsBigDecimal.add(balance,0.0).toFloat()
                        SpUtil.getIstance().user = user
                        this@TakeOutActivity.runOnUiThread(object : Runnable {
                            override fun run() {
                                txJe.text = "¥$namount"
                                txZh.text = Util.getPhoneNTransition(account)
                                dqYe.text = "¥${UtilsBigDecimal.add(balance,0.0).toFloat()}"
                                layoutOne.visibility = View.GONE
                                layoutTwo.visibility = View.VISIBLE
                                dismissProgressDialog()
                            }
                        })
                    }
                }
            }

            override fun onCompleted() {
                LogTool.e("onCompleted", "提现完成!")
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onError", "提现失败!" + e)
                this@TakeOutActivity.runOnUiThread(object : Runnable {
                    override fun run() {
                        ToastUtil.showToast(this@TakeOutActivity, "提现失败,请稍后重试")
                        dismissProgressDialog()
                    }
                })
            }

        })
    }

    private fun pullTxList(page:Int){ //page 请求的页码
        HttpManager.getInstance().post(Api.HISTORY, Parameter.getBenefitMap(), object : Subscriber<String>() {
            override fun onNext(result: String?) {
                //todo后台返回数据结构问题，暂时这样处理
                val str =result?.substring(result?.indexOf("{"),result.length)

                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@TakeOutActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val data = Gson().fromJson(str,TakeOutBean.TakeOutBeans::class.java)
                        val newList = data.data.withdraw_list
                        if(page == 1){
                            if(txList == null){
                                txList = ArrayList()
                            }else{
                                txList?.clear()
                            }
                        }
                        txList?.addAll(newList)
                        this@TakeOutActivity.runOnUiThread(object : Runnable {
                            override fun run() {
                                txAdpater?.setNewData(txList)
                                dismissProgressDialog()
                            }
                        })
                    }
                }
            }

            override fun onCompleted() {
                LogTool.e("onCompleted", "提现列表请求完成!")
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onError", "提现列表请求失败!" + e)
                this@TakeOutActivity.runOnUiThread(object : Runnable {
                    override fun run() {
                        ToastUtil.showToast(this@TakeOutActivity, "提现记录拉取失败,请稍后重试")
                        dismissProgressDialog()
                    }
                })
            }

        })
    }
}
