package com.example.juanshichang.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.core.text.trimmedLength
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.activity_reg2_log.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.login_item.*
import kotlinx.android.synthetic.main.regist_item.*
import org.jetbrains.anko.backgroundResource
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber
/**
 * @作者: yzq
 * @创建日期: 2019/8/10 17:32
 * @文件作用: 新的登录注册页面  通过传入参数 决定 显示 哪个 也可以填 默认显示注册页面
 */
class Reg2LogActivity : BaseActivity(), View.OnClickListener {
    var tag = 1
    override fun getContentView(): Int {
        return R.layout.activity_reg2_log;
    }

    override fun initView() {
        QMUIStatusBarHelper.translucent(this@Reg2LogActivity)
//        StatusBarUtil.addStatusViewWithColor(this@Reg2LogActivity, R.color.label_color)
        rRL.setPadding(0,QMUIDisplayHelper.getStatusBarHeight(this@Reg2LogActivity),0,0)
        setOnClick() //注册点击事件
        //传入非0 就显示登录界面
        val type = intent.getIntExtra("type",0)
        if(type!= 0){
            registerInclude.visibility = View.GONE
            loginInclude.visibility = View.VISIBLE
        }
    }

    override fun initData() {
        //设置用户协议字体  设置点击事件
        val spStr = SpannableString(userAgreements.text)
        spStr.setSpan(ForegroundColorSpan(Color.RED), 4, spStr.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        userAgreements.text = spStr
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.log_ret->{
                this@Reg2LogActivity.finish()
            }
            //注册页面
            R.id.goLog ->{//登录
                registerInclude.visibility = View.GONE
                loginInclude.visibility = View.VISIBLE
            }
            R.id.goVerifyCode ->{//获取验证码
                ToastUtil.showToast(this@Reg2LogActivity,"获取验证码")
            }
            R.id.registBut ->{//注册按钮
                if (goRegister()) {
                    val phone = mRegPhone.text.toString()
                    val ps = mRegpass.text.toString()
                    ToastUtil.showToast(this@Reg2LogActivity,"登录...")
                    regGo(phone,ps)
                }
            }
            R.id.mRCheckBoxX ->{//用户协议选中
                if (tag == 1) {
                    mRCheckBoxX.backgroundResource = R.drawable.noselect
                    tag = 0
                } else {
                    tag = 1
                    mRCheckBoxX.backgroundResource = R.drawable.is_sel
                }
            }
            R.id.userAgreements ->{//用户协议字体
                ToastUtil.showToast(this@Reg2LogActivity,"《用户协议》准备中")
            }
            //登录界面
            R.id.goReg ->{//注册
                loginInclude.visibility = View.GONE
                registerInclude.visibility = View.VISIBLE
            }
            R.id.loginBut ->{//登录按钮
                val phone = logPhone.text.toString()
                val ps = logPW.text.toString()
                if(goLogin(phone,ps)){
                    logGo(phone,ps)
                }
            }
            R.id.fastLogin ->{//快速登录
                ToastUtil.showToast(this@Reg2LogActivity,"暂未开放入口")
            }
            R.id.lookPW ->{//找回密码
                ToastUtil.showToast(this@Reg2LogActivity,"暂未开放入口")
            }
        }
    }
    private fun goLogin(phone:String,ps:String): Boolean {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(ps)) {
            ToastUtil.showToast(this@Reg2LogActivity, "请根据提示框输入正确的信息!")
            return false
        }
        if (!Util.validateMobile(phone)) {
            ToastUtil.showToast(this@Reg2LogActivity, "请输入正确的手机号!")
            return false
        }
        if (ps.length < 6) {
            ToastUtil.showToast(this@Reg2LogActivity, "请设置至少6位登录密码!")
            return false
        }
        return true
    }
    private fun setOnClick() {
        log_ret.setOnClickListener(this)//返回
        //注册页面
        goLog.setOnClickListener(this) //登录
        goVerifyCode.setOnClickListener(this) //获取验证码
        registBut.setOnClickListener(this) //注册按钮
        mRCheckBoxX.setOnClickListener(this) //用户协议选中
        userAgreements.setOnClickListener(this) //用户协议字体
        //登录页面
        goReg.setOnClickListener(this) //注册
        loginBut.setOnClickListener(this) //登录按钮
        fastLogin.setOnClickListener(this) //快速登录
        lookPW.setOnClickListener(this) //找回密码
    }
    private fun goRegister(): Boolean {
        val phone = regPhone.text.toString()
        val ps = regPassword.text.toString()
        val yzCode = yzCode.text.toString()
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(ps) || TextUtils.isEmpty(yzCode)) {
            ToastUtil.showToast(this@Reg2LogActivity, "请根据提示框输入正确的信息!")
            return false
        }
        if (!Util.validateMobile(phone)) {
            ToastUtil.showToast(this@Reg2LogActivity, "请输入正确的手机号!")
            return false
        }
        if (ps.length < 6) {
            ToastUtil.showToast(this@Reg2LogActivity, "请设置至少6位登录密码!")
            return false
        }
//        if (ps != ps2) {
//            ToastUtil.showToast(this@RegisterActivity, "密码不一致!")
//            return false
//        }
        if (tag == 0) {
            ToastUtil.showToast(this@Reg2LogActivity, "同意注册协议才能注册!")
            return false
        }
        //todo 验证码 判断 ....
        return true
    }
    /**
     * 注册
     */
    private fun regGo(phone: String, ps: String) {
        HttpManager.getInstance().post(Api.USER, Parameter.getRegisterMap(phone, ps), object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    try {
                        jsonObj = JSONObject(str)
                    } catch (e: JSONException) {
                        e.printStackTrace();
                    }
                    if (!jsonObj?.optString(JsonParser.JSON_CODE)!!.equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@Reg2LogActivity, jsonObj!!.optString(JsonParser.JSON_MSG))
                    } else {
                        val data = jsonObj!!.getJSONObject("data")
                        val token: String = data.getString("token")  //注册返回Token不做处理
                        if(token!=""){
                            ToastUtil.showToast(this@Reg2LogActivity, "注册成功,正在登录...")
                        }
                        logGo(phone, ps)  //注册完成 直接登录
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted","注册请求完成!")
            }

            override fun onError(e: Throwable?) {
                Log.e("onError", "注册请求错误!" + e)
                ToastUtil.showToast(this@Reg2LogActivity, "注册失败,请稍后重试!")
            }
        })
    }
    /**
     * 登录
     */
    private fun logGo(phone: String, ps: String) {
        HttpManager.getInstance().post(Api.LOGIN, Parameter.getRegisterMap(phone, ps), object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    try {
                        jsonObj = JSONObject(str)
                    } catch (e: JSONException) {
                        e.printStackTrace();
                    }
                    if (!jsonObj?.optString(JsonParser.JSON_CODE)!!.equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@Reg2LogActivity, jsonObj!!.optString(JsonParser.JSON_MSG))
                    } else {
                        val data = jsonObj!!.getJSONObject("data")
                        val token: String = data.getString("token")  //注册返回Token不做处理
                        Log.e("token",token)
                        var user = SpUtil.getIstance().user
                        user.usertoken = token
                        SpUtil.getIstance().user = user //写入
                        this@Reg2LogActivity.runOnUiThread(Runnable {
                            if (token != "" && !TextUtils.isEmpty(token)) {
//                            downUser("login")
                                goStartActivity(this@Reg2LogActivity, MainActivity())
                                this@Reg2LogActivity.finish()
                            }
                        })
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted","登录请求完成!")
            }

            override fun onError(e: Throwable?) {
                Log.e("onError","登录请求错误!"+e)
            }

        })
    }
}
