package com.example.juanshichang.base

import com.example.juanshichang.MyApp
import com.example.juanshichang.MyApp.Companion.getMD5uuid
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.widget.MD5Utils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @作者: yzq
 * @创建日期: 2019/11/14 13:56
 * @文件作用:  这是新的接口协议
 */
class NewParameter {
    companion object {
        private var baseList = ArrayList<String>()//存放签名集合
        private var stringList = ArrayList<String>()//存放签名集合
        /**
         * 签名
         * @param signType 0 未登录  1 登录
         */
        fun getSignString(signType: Int, str: String): String {
            val appkey = MyApp.sp.getString("newappkey", "shop.0371.ml")   //获取AppKey
            val usertoken = SpUtil.getIstance().user.usertoken  //获取UserToken
            var sign: String = ""
            if (signType == 0) { //未登录
                sign = MD5Utils.getMD5Str("|D|$str|K|$appkey")   //MD5(|D|+参数字符+|K|+Key)    //Key：系统统一密钥
            } else if (signType == 1) { //登录
                sign = MD5Utils.getMD5Str("|D|$str|K|$usertoken")
                LogTool.e("signLogin", sign)
            } else { //其它情况
                sign = ""
            }
            return sign
        }

        /**
         * 公共参数
         */
        fun getPublicMap(signType: Int, fuji: String): HashMap<String, String> { //action: String,
            val map = HashMap<String, String>()
//            map.put("action", action)
            map.put("sign", getSignString(signType, fuji))
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
            val sbs = StringBuffer()
            for (i in 0 until list.size) {
                if (i == list.size - 1) {
                    sbs.append(list[i])
                } else {
                    sbs.append(list[i] + "&")
                }
            }
            LogTool.e("sign","${sbs.toString()}")
            return sbs.toString()
        }

        /**
         * 封装登陆类型和方法名
         *
         * @param typeLogin
         * @param methodName
         * @return
         */
        fun fengMap(typeLogin: Int): HashMap<String, String> { // , methodName: String
            if (typeLogin == 1) {//0 未登录  1 登录
                val useruid = SpUtil.getIstance().user.useruid  //获取Useruid
                baseList.add("uid=$useruid")
            }
            val fengMap = getPublicMap(typeLogin, getFuji(baseList)) // methodName,   , methodName
            if (typeLogin == 1) {//0 未登录  1 登录
                val useruid = SpUtil.getIstance().user.useruid  //获取Useruid
                fengMap.put("uid", "$useruid")
            }
            return fengMap
        }

        // --------------------------------------------------------------
        fun getBaseMap(): HashMap<String, String> {
            baseList.clear()
            val map = fengMap(1)
            return map
        }
        //自营商品详情 免登陆
        fun getProductMap(productId:Long): HashMap<String, String> {
            baseList.clear()
            baseList.add("product_id=$productId")
            baseList.add("route=app/product")
            val map = fengMap(0)
            map.put("product_id","$productId")
            map.put("route","app/product")
            return map
        }
    }
}