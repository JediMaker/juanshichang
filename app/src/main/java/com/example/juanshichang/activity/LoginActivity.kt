package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber
/**
 * @作者: yzq
 * @创建日期: 2019/7/23 18:08
 * @文件作用: 登录页面
 */
class LoginActivity : BaseActivity(), View.OnClickListener{
    override fun getContentView(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        //进入页面 上传服务器版本号
        //可添加相关输入监听
        //设置点击监听
        mLogoinBt.setOnClickListener(this)  //登录
        mRegisterTV.setOnClickListener(this) //新用户注册
        mForgetPwdTV.setOnClickListener(this)   //忘记密码
    }

    override fun initData() {

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.mLogoinBt ->{
                val phone = mLoginName.text.toString()
                val ps = mLoginPass.text.toString()
                if(goLogin(phone,ps)){
                    logGo(phone,ps)
                }
            }
            R.id.mRegisterTV ->{
                goStartActivity(this@LoginActivity, RegisterActivity())
            }
            R.id.mForgetPwdTV ->{
                ToastUtil.showToast(this@LoginActivity,"暂未开放入口")
            }
        }
    }
    private fun goLogin(phone:String,ps:String): Boolean {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(ps)) {
            ToastUtil.showToast(this@LoginActivity, "请根据提示框输入正确的信息!")
            return false
        }
        if (!Util.validateMobile(phone)) {
            ToastUtil.showToast(this@LoginActivity, "请输入正确的手机号!")
            return false
        }
        if (ps.length < 6) {
            ToastUtil.showToast(this@LoginActivity, "请设置至少6位登录密码!")
            return false
        }
        return true
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
                        ToastUtil.showToast(this@LoginActivity, jsonObj!!.optString(JsonParser.JSON_MSG))
                    } else {
                        val data = jsonObj!!.getJSONObject("data")
                        val token: String = data.getString("token")  //注册返回Token不做处理
                        Log.e("token",token)
                        var user = SpUtil.getIstance().user
                        user.usertoken = token
                        SpUtil.getIstance().user = user //写入
                        if (token != "" && !TextUtils.isEmpty(token)) {
//                            downUser("login")
                            goStartActivity(this@LoginActivity, MainActivity())
                            this@LoginActivity.finish()
                        }
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
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//    }
}
