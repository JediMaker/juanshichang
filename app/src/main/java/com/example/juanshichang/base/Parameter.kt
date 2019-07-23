package com.example.juanshichang.base

import android.provider.Settings
import android.util.Log
import com.example.juanshichang.MyApp
import com.example.juanshichang.MyApp.Companion.getMD5uuid
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.widget.MD5Utils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Parameter {
    companion object {
        var baseList = arrayListOf<String>()//存放签名集合
        var stringList = arrayListOf<String>()//存放签名集合

        /**
         * 签名
         */
        fun getSignString(signType: Int, str: String): String {
            var appkey = MyApp.sp?.getString("appkey", "0371.ml.appkey")   //获取AppKey
            var usertoken = SpUtil.getIstance().user.usertoken  //获取UserToken
            var sign: String = ""
            if (signType == 0) { //未登录
                sign = MD5Utils.getMD5Str("|D|$str|K|$appkey")   //MD5(|D|+参数字符+|K|+Key)    //Key：系统统一密钥  todo 待询问
            } else if (signType == 1) { //登录
                sign = MD5Utils.getMD5Str("|D|$str|K|$usertoken")
            } else { //其它情况
                sign = ""
            }
            return sign
        }

        /***
         * @param mmap 非公共参数
         * @param action 接口名
         * @param signType 0 未登录 1 已登录 2 特殊
         */
        fun initParameter(mmap: Map<String, String>, action: String, signType: Int): HashMap<String, String> {
            stringList.clear()
            if (mmap.size > 0 && mmap != null) {
                for ((letter, binary) in mmap) {  //迭代 map，把 key 和 value 赋值给变量
                    stringList.add("$letter=$binary")
                }
            }
            stringList.add("action=$action")
//            stringList.add("lienttype=2")
            stringList.add("timestamp=" + (System.currentTimeMillis() / 1000).toString()) //todo 时间戳一直差8小时...
            stringList.add("uuid=" + getMD5uuid())
            stringList.sort()
            var sbs = StringBuffer()
            for (i in 0 until stringList.size) {
                sbs.append(stringList[i] + "&")
            }
            var map = hashMapOf<String, String>()
//            map.put("action", action)
            map.put("uuid", getMD5uuid())
//            map.put("clienttype", "2")
            map.put("sign", getSignString(signType, sbs.toString()))
            map.put("timestamp", ((System.currentTimeMillis()) / 1000).toString())
            map.putAll(mmap)
            return map
        }

        /**
         * 签名
         */
        fun getSign(signType: String, fuji: String): String {
            var appkey = MyApp.sp?.getString("appkey", "0371.ml.appkey")   //获取AppKey
            var usertoken = SpUtil.getIstance().user.usertoken  //获取UserToken
            var sign: String = ""
            if (signType.equals("unlogin")) { //未登录
                sign = MD5Utils.getMD5Str("|D|$fuji|K|$appkey")   //MD5(|D|+参数字符+|K|+Key)    //Key：系统统一密钥  todo 待询问
                Log.e("sign", "|D|$fuji|K|$appkey")
                Log.e("sign", MD5Utils.getMD5Str("|D|$fuji|K|$appkey"))
            } else if (signType.equals("login")) { //登录
                sign = MD5Utils.getMD5Str("|D|$fuji|K|$usertoken")
            } else if (signType.equals("special")) { //其它情况
                sign = ""
            }
            return sign
        }

        /**
         * 公共参数
         */
        fun getPublicMap(signType: String, fuji: String): HashMap<String, String> { //action: String,
            var map = hashMapOf<String, String>()
//            map.put("action", action)
            map.put("sign", getSign(signType, fuji))
            map.put("uuid", getMD5uuid())
            map.put("timestamp", (((System.currentTimeMillis()) / 1000).toString()))
            return map
        }

        /***
         * 字符集封装
         */
        fun getFuji(list: ArrayList<String>): String {//, action: String
//            list.add("action=$action")
//            list.add("clienttype=2")
            list.add("timestamp=" + ((System.currentTimeMillis()) / 1000))
            list.add("uuid=${getMD5uuid()}")
//            list.sort()
            Collections.sort(list)
            var sbs = StringBuffer()
            for (i in 0 until list.size) {
                if (i == list.size - 1) {
                    sbs.append(list[i])
                } else {
                    sbs.append(list[i] + "&")
                }
            }
            return sbs.toString()
        }

        /**
         * 封装登陆类型和方法名
         *
         * @param typeLogin
         * @param methodName
         * @return
         */
        fun fengMap(typeLogin: String): HashMap<String, String> { // , methodName: String
            var fengMap = getPublicMap(typeLogin, getFuji(baseList)) // methodName,   , methodName
            return fengMap
        }
        //--------------------------------------分割线-----------------------------------------------------
        /**
         * 注册/登录
         */
        fun getRegisterMap(mobile: String, password: String): HashMap<String, String> {
            baseList.clear()
            baseList.add("mobile=$mobile")
            baseList.add("password=$password")
            var map = fengMap("unlogin")
            map.put("mobile", mobile)
            map.put("password", password)
            return map
        }

    }
}