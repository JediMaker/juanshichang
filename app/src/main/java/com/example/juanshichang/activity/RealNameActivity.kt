package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastTool
import com.example.juanshichang.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_real_name.*
import kotlinx.android.synthetic.main.activity_topup.*
import okhttp3.internal.waitMillis
import org.json.JSONObject
import rx.Subscriber
import java.net.URLEncoder

/**
 * @作者：yzq
 * @创建时间：2019/11/7 18:58
 * @文件作用: 实名认证
 */
class RealNameActivity : BaseActivity(), View.OnClickListener {
    override fun getContentView(): Int {
        return R.layout.activity_real_name
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this, R.color.colorPrimary)
    }

    override fun initData() {
        realRet.setOnClickListener(this)
        goReal.setOnClickListener(this)
        cancelReal.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        //姓名输入监听
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val monicker = s.toString().trim() //名字
                val idNum = idNumber.text.toString().trim() //身份证号码
                if (!monicker.isEmpty() && monicker.length >= 2 && !idNum.isEmpty() && idNum.length == 18) {
                    goReal.isEnabled = true
                    goApprove = true
                } else {
                    goReal.isEnabled = false
                }
            }
        })
        //添加身份证号输入 监听
        idNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val monicker = name.text.toString().trim() //名字
                val idNum = s.toString().trim() //身份证号码
                if (!monicker.isEmpty() && monicker.length >= 2 && !idNum.isEmpty() && idNum.length == 18) {
                    goReal.isEnabled = true
                    goApprove = true
                } else {
                    goReal.isEnabled = false
                }
            }
        })
    }
    private var goApprove:Boolean = false //是否开始认证   避免重复点击 多次调用接口
    override fun onClick(v: View?) {
        when (v) {
            goReal -> { //立即认证
                if(goApprove){
                    goRealName(name.text.toString(),idNumber.text.toString())
                    showProgressDialog()
                    goApprove = false
                }else{
                    ToastUtil.showToast(this@RealNameActivity,"请勿重复认证！！！")
                }
            }
            realRet,
            cancelReal -> {// 取消/返回
                finish()

            }
        }
    }

    //以下为网络请求
    private fun goRealName(name: String,idNum:String) {
        var map = hashMapOf<String, String>()
        map.put("idcard", idNum)
        map.put("realname", URLEncoder.encode(name,"utf-8"))
        map.put("key", Api.IDKey)
        JhApiHttpManager.getInstance(Api.JUHEAPi2).post(Api.REALNAME, map, object : Subscriber<String>() {
            override fun onNext(result: String?) {
                //todo后台返回数据结构问题，暂时这样处理
                val str =result?.substring(result?.indexOf("{"),result.length)
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    val jsonObj: JSONObject = JSONObject(str)
                    if (jsonObj.optInt("error_code") != 0) {//.equals("0")
                        ToastUtil.showToast(
                            this@RealNameActivity,
                            jsonObj.optString("reason")
                        )
                    } else {
                        val result = jsonObj.getJSONObject("result")
                        val res = result.getInt("res") //属result,匹配详情,1匹配,2不匹配
                        if(res == 1){//匹配成功
                            ToastUtil.showToast(this@RealNameActivity,"匹配成功啦...")
                        }else{//失败
                            ToastTool.showToast(this@RealNameActivity,"不匹配的姓名与身份证号码！！！")
                        }
                        this@RealNameActivity.runOnUiThread {

                        }
                    }
                }
            }

            override fun onCompleted() {
                LogTool.e("onCompleted","实名认证查询完成")
                dismissProgressDialog()
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onError","实名认证Error")
            }
        })
    }
}
