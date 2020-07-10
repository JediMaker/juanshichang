package com.example.juanshichang.activity

import android.content.Context
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.*
import kotlinx.android.synthetic.main.activity_fast_login.*
import kotlinx.android.synthetic.main.activity_fast_login.goVerifyCode
import kotlinx.android.synthetic.main.activity_fast_login.loginBut
import kotlinx.android.synthetic.main.login_item.*
import kotlinx.android.synthetic.main.regist_item.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * 快速登陆找回密码公共页面
 */
class FastLoginActivity : BaseActivity(), View.OnClickListener {


    private var type: Int = LOGINCODE
    var tag = 1
    var mSmsCode: String? = ""  //返回的 验证码

    companion object {
        val RESETPASSWORDCODE: Int = 0//忘记密码，重新设置密码
        val LOGINCODE: Int = 1//快速登陆
    }

    override fun getContentView(): Int {
        return R.layout.activity_fast_login;
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@FastLoginActivity, R.color.colorPrimary)
        SoftHideKeyBoardUtil.assistActivity(this@FastLoginActivity)
        //传入非0 就显示快速登录界面
        type = intent.getIntExtra("type", 0)
        if (type != FastLoginActivity.RESETPASSWORDCODE) {
            ll_pwd.visibility = View.GONE
        } else {
            ll_pwd.visibility = View.VISIBLE
        }
        loginBut.setOnClickListener(this) //登录按钮
        goVerifyCode.setOnClickListener(this) //获取验证码
        fastLoginRet.setOnClickListener(this) //获取验证码
    }

    override fun initData() {
        if (type != FastLoginActivity.RESETPASSWORDCODE) {
            loginBut.text = "登录"
            fastLoginTitle.text = "快速登录"
        } else {
            loginBut.text = "设置"
            fastLoginTitle.text = "重置密码"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fastLoginRet -> {
                this@FastLoginActivity.finish()
            }
            R.id.goVerifyCode -> {//获取验证码
                val phone = rPhone.text.toString()
                mSmsCode = null
                if (Util.validateMobile(phone)) {
                    //启动注册轮询
                    goVerifyCode.isEnabled = false
                    if (type != FastLoginActivity.RESETPASSWORDCODE) {
                        getSendSMS(phone, "3", this@FastLoginActivity)

                    } else {
                        getSendSMS(phone, "2", this@FastLoginActivity)
                    }
                    timer.start()
                } else {
                    showRegisterDialog("温馨提示", "请输入正确的手机号", "确定", "", object : DialogCallback {
                        override fun cancle() { //无取消
                        }

                        override fun sure() {
                            rPhone.text.clear()
                        }
                    }, false)
                }
            }
            R.id.loginBut -> {//登录按钮
                val phone = rPhone.text.toString()
                val smscode = et_checkCode.text.toString()
                val password = et_pwd.text.toString()
                if (type != FastLoginActivity.RESETPASSWORDCODE) {//快速登陆
                    if (goLogin(phone, smscode)) {
                        logGo(phone, smscode)
                    }
                } else {//重置密码
                    resetPassword(phone, smscode, password)
                }

            }
        }
    }

    private fun goLogin(phone: String, smscode: String): Boolean {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(smscode)) {
            ToastUtil.showToast(this@FastLoginActivity, "请根据提示框输入正确的信息!")
            return false
        }
        if (!Util.validateMobile(phone)) {
            ToastUtil.showToast(this@FastLoginActivity, "请输入正确的手机号!")
            return false
        }
        return true
    }

    private fun goLogin2(phone: String, smscode: String, ps: String): Boolean {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(smscode) || TextUtils.isEmpty(ps)) {
            ToastUtil.showToast(this@FastLoginActivity, "请根据提示框输入正确的信息!")
            return false
        }
        if (!Util.validateMobile(phone)) {
            ToastUtil.showToast(this@FastLoginActivity, "请输入正确的手机号!")
            return false
        }
        if (ps.length < 6) {
            ToastUtil.showToast(this@FastLoginActivity, "请设置至少6位登录密码!")
            return false
        }
        return true
    }

    fun getSendSMS(mobile: String, type: String, context: Context) {
//        var smsCodes: String? = null
        HttpManager.getInstance()
            .post(
                Api.SMSSEND,
                Parameter.getVerifyCode(mobile, type),
                object : Subscriber<String>() {
                    override fun onNext(result: String?) {
                        //todo后台返回数据结构问题，暂时这样处理
                        val str = result?.substring(result?.indexOf("{"), result.length)

                        if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                            var jsonObj: JSONObject? = null
                            try {
                                jsonObj = JSONObject(str)
                            } catch (e: JSONException) {
                                e.printStackTrace();
                            }
                            if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                            ) {
                                if ("201".equals(jsonObj!!.getString(JsonParser.JSON_CODE))) {//发送验证码频率过快
                                    ToastUtil.showToast(
                                        context,
                                        "您发送验证码的频率过快，请稍后再试！"
                                    )
                                    return
                                }
                                ToastUtil.showToast(
                                    context,
                                    jsonObj!!.optString(JsonParser.JSON_MSG)
                                )
                            } else {
                                val data = jsonObj!!.getJSONObject("data")
                                LogTool.e("zxcv", "Date: " + data.toString())
                                mSmsCode = data.getString("code")  //注册返回Token不做处理
//                        mSmsCode = data.getString("sms_code")  //注册返回Token不做处理
                                LogTool.e("zxcv", "sms_code: " + mSmsCode.toString())
                                if (mSmsCode != "") {
                                    timer.start()
                                    ToastUtil.showToast(context, "验证码已发送,请注意查收消息")
                                    //todo 此处可添加 请求读取通知类消息权限
                                }
                            }
                        }
                    }

                    override fun onCompleted() {
                        LogTool.e("onCompleted", "验证码请求完成!")
                    }

                    override fun onError(e: Throwable?) {
                        mSmsCode = ""
                        LogTool.e("onError", "验证码请求错误!" + e)
                    }
                })
    }

    /**
     * 登录
     */
    private fun logGo(phone: String, smscode: String) {
        HttpManager.getInstance()
            .post(
                Api.FASTLOGIN,
                Parameter.getFastLoginMap(phone, smscode),
                object : Subscriber<String>() {
                    override fun onNext(result: String?) {
                        //todo后台返回数据结构问题，暂时这样处理
                        val str = result?.substring(result?.indexOf("{"), result.length)

                        if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                            var jsonObj: JSONObject? = null
                            try {
                                jsonObj = JSONObject(str)
                            } catch (e: JSONException) {
                                e.printStackTrace();
                            }
                            /*  if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                                  ToastUtil.showToast(this@Reg2LogActivity, jsonObj.optString(JsonParser.JSON_MSG))
                              } */
                            if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                            ) {
                                ToastUtil.showToast(
                                    this@FastLoginActivity,
                                    jsonObj.optString(JsonParser.JSON_MSG)
                                )
                            } else {
                                val data = jsonObj.getJSONObject("data")
//                        val token: String = data.getString("token")  //注册返回Token不做处理
                                val uid: Long = data.getLong("uid") //这是用于校验新接口的uid
//                        LogTool.e("LogToken", token)
                                val user = SpUtil.getIstance().user
                                user.apply {
                                    useruid = uid
//                            usertoken = token
//                            phone_num = phone
//                            password = ps
                                }.let {
                                    SpUtil.getIstance().user = it //写入
                                }
                                this@FastLoginActivity.runOnUiThread(Runnable {
//                            if (token != "" && !TextUtils.isEmpty(token)) {
//                            downUser("login")
                                    goStartActivity(this@FastLoginActivity, MainActivity())
                                    this@FastLoginActivity.finish()
//                            }
                                })

                            }
                        }
                    }

                    override fun onCompleted() {
                        LogTool.e("onCompleted", "登录请求完成!")
                    }

                    override fun onError(e: Throwable?) {
                        LogTool.e("onError", "登录请求错误!" + e)
                    }

                })
    }

    /**
     * 登录
     */
    private fun resetPassword(phone: String, smscode: String, password: String) {
        HttpManager.getInstance()
            .post(
                Api.RESETPASSWORD,
                Parameter.resetPasswordMap(phone, smscode, password),
                object : Subscriber<String>() {
                    override fun onNext(result: String?) {
                        //todo后台返回数据结构问题，暂时这样处理
                        val str = result?.substring(result?.indexOf("{"), result.length)

                        if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                            var jsonObj: JSONObject? = null
                            try {
                                jsonObj = JSONObject(str)
                            } catch (e: JSONException) {
                                e.printStackTrace();
                            }
                            /*  if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                                  ToastUtil.showToast(this@Reg2LogActivity, jsonObj.optString(JsonParser.JSON_MSG))
                              } */
                            if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                            ) {
                                ToastUtil.showToast(
                                    this@FastLoginActivity,
                                    jsonObj.optString(JsonParser.JSON_MSG)
                                )
                            } else {
                                val data = jsonObj.getJSONObject("data")
//                        val token: String = data.getString("token")  //注册返回Token不做处理
                                val uid: Long = data.getLong("uid") //这是用于校验新接口的uid
//                        LogTool.e("LogToken", token)
                                val user = SpUtil.getIstance().user
                                user.apply {
                                    useruid = uid
//                            usertoken = token
//                            phone_num = phone
//                            password = ps
                                }.let {
                                    SpUtil.getIstance().user = it //写入
                                }
                                this@FastLoginActivity.runOnUiThread(Runnable {
//                            if (token != "" && !TextUtils.isEmpty(token)) {
//                            downUser("login")
                                    goStartActivity(this@FastLoginActivity, MainActivity())
                                    this@FastLoginActivity.finish()
//                            }
                                })

                            }
                        }
                    }

                    override fun onCompleted() {
                        LogTool.e("onCompleted", "登录请求完成!")
                    }

                    override fun onError(e: Throwable?) {
                        LogTool.e("onError", "登录请求错误!" + e)
                    }

                })
    }

    //验证码计时器
//    var curTime = 60
    var timer: CountDownTimer = object : CountDownTimer(60000, 1000) {
        override fun onFinish() {
            goVerifyCode.isEnabled = true
            goVerifyCode.text = "获取验证码"
        }

        override fun onTick(millisUntilFinished: Long) {
            //todo millisUntilFinished为剩余时间，也就是30000 - n*1000
            goVerifyCode.isEnabled = false //设置按钮不可点击
            val s = millisUntilFinished / 1000
            goVerifyCode.text = "$s s"
        }
    }

}