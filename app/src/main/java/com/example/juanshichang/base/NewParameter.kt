package com.example.juanshichang.base

import com.example.juanshichang.MyApp
import com.example.juanshichang.MyApp.Companion.getMD5uuid
import com.example.juanshichang.bean.AKey
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
                LogTool.e("signLogin", "|D|$str|K|$usertoken")
                LogTool.e("signLoginMD5", sign)
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
            baseList.add("route=app/cart")
            val map = fengMap(1)
            map.put("route","app/cart")
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
        //添加购物车
        fun getAddSCMap(productId: Long,quantity:Int,checkList:ArrayList<HashMap<String,ArrayList<String>>>): HashMap<String, String>{
            baseList.clear()
            baseList.add("product_id=$productId")
            baseList.add("quantity=$quantity")
            baseList.add("option=${getBaseCheckList(checkList)}")
            baseList.add("route=app/cart/add")
            val map = fengMap(1)
            map.put("product_id","$productId")
            map.put("quantity","$quantity")
            getBaseCheckMap(checkList,map)
            map.put("route","app/cart/add")
            return map
        }


        //下面是解析购物车返回集合
        private fun getBaseCheckList(checkList:ArrayList<HashMap<String,ArrayList<String>>>):String{
            var state226:Boolean = false  //单选
            var state227:Boolean = false  //单选
            var state228:Boolean = false //多选
            val strList = arrayListOf<String>()
            for (i in 0 until  checkList.size){
                if(!state226 && checkList[i].containsKey("226")){
                    state226 = true
                    strList.add("226=${checkList[i]["226"]!![0]}")
                }
                if(!state227 && checkList[i].containsKey("227")){
                    state227 = true
                    strList.add("227=${checkList[i]["227"]!![0]}")
                }
                if(!state228 && checkList[i].containsKey("228")){
                    val list = checkList[i]["228"]
                    val buf:StringBuilder = StringBuilder()
                    for (y in 0 until list?.size!!){
                        if(y == 0){
                            buf.append("228=$y=${list[y]}")
                        }else{
                            buf.append("&$y=${list[y]}")
                        }
                    }
                    strList.add("${buf}")
                    state228 = true
                }
            }
            strList.sort()
            val bufs:StringBuilder = StringBuilder()
            for (k in 0 until strList.size){
                if(k == strList.size-1){
                    bufs.append(strList[k])
                }else{
                    bufs.append("${strList[k]}&")
                }
            }
            LogTool.e("signOption",bufs.toString())
            return bufs.toString()
            /*if(!state226){
                baseList.add("option[226]=")
            }
            if(!state227){
                baseList.add("option[227]=")
            }
            if(!state228){
                baseList.add("option[228][]=")
            }*/
        }
        private fun getBaseCheckMap(checkList:ArrayList<HashMap<String,ArrayList<String>>>,map:HashMap<String, String>):HashMap<String, String>{
            var state226:Boolean = false  //单选
            var state227:Boolean = false  //单选
            var state228:Boolean = false //多选
            for (i in 0 until  checkList.size){
                if(!state226 && checkList[i].containsKey("226")){
                    state226 = true
                    map.put("option[226]","${checkList[i]["226"]!![0]}")
                }
                if(!state227 && checkList[i].containsKey("227")){
                    state227 = true
                    map.put("option[227]","${checkList[i]["227"]!![0]}")
                }
                if(!state228 && checkList[i].containsKey("228")){ //关于这一块儿解决思路 就是.... 重写 相关方法...
                    val list = checkList[i]["228"]
                    for (y in 0 until list?.size!!){
                        //解决hashmap 不可以同键的问题...
                        val a = AKey()
                        if(y == 0){
                            a.head = "option[228][]"
                            map.put(a.toString(),"${list[y]}")
                        }else if(y == 1){
                            a.head = "option"
                            a.center = "[228][]"
                            map.put(a.toString(),"${list[y]}")
                        }else if(y == 2){
                            a.head = "option"
                            a.center = "[228]["
                            a.behind ="]"
                            map.put(a.toString(),"${list[y]}")
                        }else if(y == 3){
                            a.head = "option"
                            a.center = "["
                            a.behind ="228][]"
                            map.put(a.toString(),"${list[y]}")
                        }else if(y == 4){
                            a.head = "option"
                            a.center = "[2"
                            a.behind ="28][]"
                            map.put(a.toString(),"${list[y]}")
                        }else if(y == 5){
                            a.head = "option"
                            a.center = "[22"
                            a.behind ="8][]"
                            map.put(a.toString(),"${list[y]}")
                        }else{
                            a.head = "option"
                            a.center = "[228]"
                            a.behind ="[]"
                            map.put(a.toString(),"${list[y]}")
                        }
                        // .....
                    }
                    state228 = true
                }
            }
            /*if(!state226){
                map.put("option[226]","")
            }
            if(!state227){
                map.put("option[227]","")
            }
            if(!state228){
                map.put("option[228][]","")
            }*/
            return map
        }
    }
}