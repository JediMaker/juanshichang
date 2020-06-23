package com.example.juanshichang.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.http.HttpManager
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/7/19 15:00
 * @文件作用: Toast 弹窗工具类
 */
class ToastUtil {
    companion object {
        internal var sToast: Toast? = null

        fun showToast(context: Context, text: String) {
            showToastInner(context, getError(text, context), Toast.LENGTH_SHORT)
        }

        fun showToast(context: Context, stringId: Int) {
            showToastInner(context, context.getString(stringId), Toast.LENGTH_SHORT)
        }


        fun showToastLong(context: Context, text: String) {
            showToastInner(context, text, Toast.LENGTH_LONG)
        }

        fun showToastLong(context: Context, stringId: Int) {
            showToastInner(context, context.getString(stringId), Toast.LENGTH_LONG)
        }

        private fun showToastInner(context: Context, text: String, duration: Int) {
            ensureToast(context)
            sToast?.setText(text)
            sToast?.setDuration(duration)
            if (!TextUtils.isEmpty(text)){
                sToast?.show()
            }
        }

        @SuppressLint("ShowToast")
        private fun ensureToast(context: Context) {
            if (sToast != null) {
                return
            }
            synchronized(ToastUtil::class.java) {
                if (sToast != null) {
                    return
                }
                sToast = Toast.makeText(context.applicationContext, " ", Toast.LENGTH_SHORT)
            }
        }

        //返回信息
        private fun getError(str: String, context: Context): String {
            var retStr: String = str
            if (isError(str)) {
                when (str) {
                    "NEED SIGN",
                    "NEED TIMESTAMP",
                    "NEED UUID",
                    "NEED CART_ID & QUANTITY",
                    "NEED ADDRESS_ID" -> {
                        retStr = "用户信息缺失"
                    }
                    "NOT FOUND PRODUCT",
                    "NOT FOUND GOODS IN CART" -> {
                        retStr = "敬请期待"
                    }
                    "INVALID INPUT PARAMS",
                    "INVALID TIMESTAMP",
                    "INVALID SIGN" -> {
                        retStr = "网络异常，请重试"
                        getNewToken(context)
                    }
                    "LOGIN UID ERROR" -> {
//                        retStr = "尚未登陆，请登录"
                        retStr = "登录错误"
                    }
                    "CAN NOT FOUND USER" -> {
                        retStr = "不存在的用户"
                    }
                    "USERNAME OR PASSWORD IS NOT CORRECT" -> {
                        retStr = "用户名或密码错误"
                    }
                    "The phone number already exists",
                    "FAILED TO ADD USER" -> {
                        retStr = "用户已存在"
                    }
                }
            }
            return retStr
        }


        //无效信息重新请求登录
        private fun getNewToken(context: Context) {
            val phone = SpUtil.getIstance().user.phone_num
            val ps = SpUtil.getIstance().user.password
            HttpManager.getInstance()
                .post(
                    Api.GETTOKEN,
                    NewParameter.getUserTokenMap(),
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
                                if (!jsonObj?.optString(JsonParser.JSON_CODE)!!.equals(
                                        JsonParser.JSON_SUCCESS
                                    )
                                ) {
                                    showToast(
                                        context, jsonObj.optString(
                                            JsonParser.JSON_MSG
                                        )
                                    )
                                } else {
                                    val data = jsonObj.getJSONObject("data")
                                    val token: String = data.getString("token")  //注册返回Token不做处理
                                    val uid: Long = data.getLong("uid") //这是用于校验新接口的uid
                                    LogTool.e("LogToken", token)
                                    val user = SpUtil.getIstance().user
                                    user.apply {
                                        useruid = uid
                                        usertoken = token
                                    }.let {
                                        SpUtil.getIstance().user = it //写入
                                    }

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

        //用于判定是否为已知异常
        private fun isError(str: String): Boolean {
            if (TextUtils.equals(str, "NEED SIGN") || TextUtils.equals(
                    str,
                    "NEED TIMESTAMP"
                ) || TextUtils.equals(str, "INVALID TIMESTAMP")
                || TextUtils.equals(str, "INVALID SIGN") || TextUtils.equals(
                    str,
                    "NEED UUID"
                ) || TextUtils.equals(str, "LOGIN UID ERROR")
                || TextUtils.equals(str, "NEED CART_ID & QUANTITY") || TextUtils.equals(
                    str,
                    "NOT FOUND PRODUCT"
                ) || TextUtils.equals(str, "CAN NOT FOUND USER")
                || TextUtils.equals(str, "INVALID INPUT PARAMS") || TextUtils.equals(
                    str,
                    "NEED ADDRESS_ID"
                ) || TextUtils.equals(str, "NOT FOUND GOODS IN CART")
                || TextUtils.equals(str, "USERNAME OR PASSWORD IS NOT CORRECT")
                || TextUtils.equals(str, "FAILED TO ADD USER")
                || TextUtils.equals(str, "The phone number already exists")
            ) {
                return true
            }
            return false
        }
    }

}