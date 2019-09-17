package com.example.juanshichang.activity

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.Util
import kotlinx.android.synthetic.main.activity_take_out.*

/**
 * @作者：yzq
 * @创建时间：2019/9/17 18:15
 * @文件作用: 提现页面
 */
class TakeOutActivity : BaseActivity(), View.OnClickListener{
    private var vType:Int = 0
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.takeFin->{
                finish()
            }
            R.id.goTx->{

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
            setOneUi()
        }
        takeFin.setOnClickListener(this)
    }
    override fun initData() {

    }
    private fun setOneUi() {
        goTx.setOnClickListener(this)
        txClose.setOnClickListener(this)
        //给Edit添加监听事件
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
        val aliPay = SpUtil.getIstance().user.ali_pay_account
        if (aliPay=="" || TextUtils.isEmpty(aliPay)){
            txPhone.text = "无账户"
        }else{
            txPhone.text = Util.getPhoneNTransition(aliPay.toString())
        }
    }
}
