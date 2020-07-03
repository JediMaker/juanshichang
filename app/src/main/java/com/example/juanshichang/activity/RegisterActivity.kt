package com.example.juanshichang.activity

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.backgroundResource
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber
/**
 * @作者: yzq
 * @创建日期: 2019/7/22 18:16
 * @文件作用: 注册页面
 */
class RegisterActivity : BaseActivity(), View.OnClickListener {
    var tag = 1
    override fun getContentView(): Int {
        return R.layout.activity_register
    }

    override fun initView() {
        registerBut.setOnClickListener(this)
        userAgreement.setOnClickListener(this)
        mRCheckBoxT.setOnClickListener(this)
    }

    override fun initData() {
        //设置用户协议字体  设置点击事件
        val spStr = SpannableString(userAgreement.text)
        spStr.setSpan(ForegroundColorSpan(Color.RED), 6, spStr.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        userAgreement.text = spStr

    }

    override fun onClick(v: View?) {
        when (v) {
            registerBut -> { //注册
                if (goRegister()) {
                    val phone = mRegPhone.text.toString()
                    val ps = mRegpass.text.toString()
                    ToastUtil.showToast(this@RegisterActivity,"一键登录")
                    regGo(phone,ps)
                }
            }
            userAgreement -> { //用户协议字体
                ToastUtil.showToast(this@RegisterActivity, "用户协议准备中...敬请期待!!!")
            }
            mRCheckBoxT -> { //用户协议选项
                if (tag == 1) {
                    mRCheckBoxT.backgroundResource = R.drawable.noselect
                    tag = 0
                } else {
                    tag = 1
                    mRCheckBoxT.backgroundResource = R.drawable.i_reg_hassele
                }
            }
            else -> {
            }
        }
    }

    private fun goRegister(): Boolean {
        val phone = mRegPhone.text.toString()
        val ps = mRegpass.text.toString()
        val ps2 = mRegpass2.text.toString()
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(ps) || TextUtils.isEmpty(ps2)) {
            ToastUtil.showToast(this@RegisterActivity, "请根据提示框输入正确的信息!")
            return false
        }
        if (!Util.validateMobile(phone)) {
            ToastUtil.showToast(this@RegisterActivity, "请输入正确的手机号!")
            return false
        }
        if (ps.length < 6) {
            ToastUtil.showToast(this@RegisterActivity, "请设置至少6位登录密码!")
            return false
        }
        if (ps != ps2) {
            ToastUtil.showToast(this@RegisterActivity, "密码不一致!")
            return false
        }
        if (tag == 0) {
            ToastUtil.showToast(this@RegisterActivity, "同意注册协议才能注册!")
            return false
        }
        return true
    }
    companion object{

    }
    /**
     * 注册
     */
    private fun regGo(phone: String, ps: String) {
        HttpManager.getInstance().post(Api.USER, Parameter.getLoginMap(phone, ps), object : Subscriber<String>() {
            override fun onNext(result: String?) {
                //todo后台返回数据结构问题，暂时这样处理
                val str =result?.substring(result?.indexOf("{"),result.length)

                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    try {
                        jsonObj = JSONObject(str)
                    } catch (e: JSONException) {
                        e.printStackTrace();
                    }
                    if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                        ToastUtil.showToast(this@RegisterActivity, jsonObj!!.optString(JsonParser.JSON_MSG))
                    } else {
                        val data = jsonObj!!.getJSONObject("data")
                        val token: String = data.getString("token")  //注册返回Token不做处理
                        if(token!=""){
                            ToastUtil.showToast(this@RegisterActivity, "注册成功,正在登录...")
                        }
                        logGo(phone, ps)  //注册完成 直接登录
                    }
                }
            }

            override fun onCompleted() {
                LogTool.e("onCompleted","注册请求完成!")
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onError", "注册请求错误!" + e)
            }
        })
    }

    /**
     * 登录
     */
    private fun logGo(phone: String, ps: String) {
        HttpManager.getInstance().post(Api.LOGIN, Parameter.getLoginMap(phone, ps), object : Subscriber<String>() {
            override fun onNext(result: String?) {
                //todo后台返回数据结构问题，暂时这样处理
                val str =result?.substring(result?.indexOf("{"),result.length)

                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    try {
                        jsonObj = JSONObject(str)
                    } catch (e: JSONException) {
                        e.printStackTrace();
                    }
                    if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                        ToastUtil.showToast(this@RegisterActivity, jsonObj!!.optString(JsonParser.JSON_MSG))
                    } else {
                        val data = jsonObj!!.getJSONObject("data")
                        val token: String = data.getString("token")  //注册返回Token不做处理
                        var user = SpUtil.getIstance().user
                        user.usertoken = token
                        SpUtil.getIstance().user = user //写入
                        if (SpUtil.getIstance().user.usertoken != "" && !TextUtils.isEmpty(SpUtil.getIstance().user.usertoken)) {
//                            downUser("login")
                            goStartActivity(this@RegisterActivity,MainActivity())
                            this@RegisterActivity.finish()
                        }
                    }
                }
            }

            override fun onCompleted() {
                LogTool.e("onCompleted","登录请求完成!")
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onCompleted","登录请求错误!"+e)
            }

        })
    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
//    }
}
