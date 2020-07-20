package com.example.juanshichang.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.juanshichang.MainActivity
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.TokenBean
import com.example.juanshichang.http.*
import com.example.juanshichang.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 11:31
 * @文件作用: 首页面
 */
class SplashActivity : FragmentActivity(), View.OnClickListener {
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        StatusBarUtil.addStatusViewWithColor(this, R.color.white)
        val GxmQ: Boolean = MyApp.sp.getBoolean("FIRST", true)
        val edit = MyApp.sp.edit()
//        edit.putString("appkey", "0371.ml.appkey")
        edit.apply()
        setContentView(R.layout.activity_splash)
        if (GxmQ) { //第一次登录
//            setContentView(R.layout.activity_splash)
            getCode()
            BaseActivity.goStartActivity(this@SplashActivity, GuideActivity())
            finish()
            //首次登录 拿取appKey
        } else {
            splash_img.visibility = View.VISIBLE
            tv.visibility = View.VISIBLE
            //各个第三方的初始化 以及获取版本信息
            Handler().postDelayed(Runnable {
                goActivity(this@SplashActivity)
            }, 3000)
        }

        tv.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv -> {
                goActivity(this@SplashActivity)
                finish()
            }
        }
    }

    private fun goActivity(context: Context) {
        if (Util.hasLogin()) {
            BaseActivity.goStartActivity(context, MainActivity())
        } else {
//            BaseActivity.goStartActivity(context,LoginActivity())
            BaseActivity.goStartActivity(context, MainActivity())
//            ToastTool.showToast(this@SplashActivity, "大侠 尚未登录 如何畅游江湖")
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Handler().removeCallbacksAndMessages(0)
    }

    var code: String? = null;
    private fun getCode() {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).get(
            Api.AUTHORIZE,
            NewParameter.getAuthorizeMap(
                CLIENT_ID,
                CODE,
                REDIRECT_URI
            ),
            object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val t = result?.substring(result?.indexOf("{"), result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)

                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        val data = jsonObj?.optJSONObject("data")
                        code = data?.optString("code")
                        getAccessToken()

                    }
                }

                override fun onCompleted() {
                }

                override fun onError(e: Throwable?) {
                }
            })
    }

    /**
     * 获取token
     */
    private fun getAccessToken() {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.ACCESS_TOKEN,
            NewParameter.getAuthorizeTokenMap(
                CLIENT_ID,
                code.toString(),
                CLIENT_SECRET,
                REDIRECT_URI
            ),
            object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val t = result?.substring(result?.indexOf("{"), result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            //获取到令牌信息写入缓存
                            val data = Gson().fromJson(t, TokenBean.TokenBean::class.java)
                            val user = SpUtil.getIstance().user
                            user.apply {
                                access_token = data?.data?.access_token!!
                                expires_in = data?.data?.expires_in!!
                                token_type = data?.data?.token_type!!
                                refresh_token = data?.data?.refresh_token!!
                            }.let {
                                SpUtil.getIstance().user = it //写入
                            }
                        } else { //令牌信息获取失败

                        }

                    }
                }

                override fun onCompleted() {
                }

                override fun onError(e: Throwable?) {
                }
            })
    }


}
