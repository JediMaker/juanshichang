package com.example.juanshichang.base

import android.util.Log
import com.example.juanshichang.MyApp
import com.example.juanshichang.MyApp.Companion.getMD5uuid
import com.example.juanshichang.MyApp.Companion.getMD5uuidNew
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.widget.MD5Utils
import okhttp3.internal.toImmutableMap
import okio.utf8Size
import java.util.*
import java.util.concurrent.ConcurrentHashMap
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
        private fun getSignString(signType: Int, str: String): String {
            val appkey = MyApp.sp.getString("newappkey", "shop.0371.ml")   //获取AppKey
            val usertoken = SpUtil.getIstance().user.usertoken  //获取UserToken
            var sign: String = ""
            if (signType == 0) { //未登录
                sign =
                    MD5Utils.getMD5Str("|D|$str|K|$appkey")   //MD5(|D|+参数字符+|K|+Key)    //Key：系统统一密钥
                LogTool.e("signLogin", "|D|$str|K|$appkey")
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
        private var uuidNew: String? = null

        private fun getPublicMap(
            signType: Int,
            fuji: String
        ): HashMap<String, String> { //action: String,
            val map = HashMap<String, String>()
//            map.put("action", action)
            map.put("sign", getSignString(signType, fuji))
            map.put("uuid", uuidNew!!)
            map.put("timestamp", (((System.currentTimeMillis()) / 1000).toString()))
            return map
        }

        private fun getPublicMap2(
            signType: Int,
            fuji: String
        ): IdentityHashMap<String, String> { //action: String,
            val map = IdentityHashMap<String, String>()
//            map.put("action", action)
            map.put("sign", getSignString(signType, fuji))
            map.put("uuid", uuidNew)
            map.put("timestamp", (((System.currentTimeMillis()) / 1000).toString()))
            return map
        }

        /***
         * 字符集封装
         */
        private fun getFuji(list: ArrayList<String>): String {//, action: String
//            list.add("action=$action")
//            list.add("clienttype=2")
            uuidNew = getMD5uuidNew()
            list.add("timestamp=" + ((System.currentTimeMillis()) / 1000))
            list.add("uuid=${uuidNew}")
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
            LogTool.e("sign", "${sbs.toString()}")
            return sbs.toString()
        }

        /**
         * 封装登陆类型和方法名
         *
         * @param typeLogin
         * @param methodName
         * @return
         */
        private fun fengMap(typeLogin: Int): HashMap<String, String> { // , methodName: String
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

        fun fengMap2(typeLogin: Int): IdentityHashMap<String, String> { // , methodName: String
            if (typeLogin == 1) {//0 未登录  1 登录
                val useruid = SpUtil.getIstance().user.useruid  //获取Useruid
                baseList.add("uid=$useruid")
            }
            val fengMap = getPublicMap2(typeLogin, getFuji(baseList)) // methodName,   , methodName
            if (typeLogin == 1) {//0 未登录  1 登录
                val useruid = SpUtil.getIstance().user.useruid  //获取Useruid
                fengMap.put("uid", "$useruid")
            }
            return fengMap
        }

        // --------------------------------------------------------------
        fun getHomeMap(): HashMap<String, String> {
            baseList.clear()
            baseList.add("route=app/home")
            val map = fengMap(0)
            return map
        }
        fun getBaseMap(): HashMap<String, String> {
            baseList.clear()
            baseList.add("route=app/cart")
            val map = fengMap(1)
            map.put("route", "app/cart")
            return map
        }
        //获取订单列表
        fun getBaseTMap(): HashMap<String, String> {
            baseList.clear()
            baseList.add("route=app/order/history")
            val map = fengMap(1)
            map.put("route", "app/order/history")
            return map
        }
        //获取地址id列表
        fun getBaseZMap(): HashMap<String, String> {
            baseList.clear()
            baseList.add("route=app/address/zones")
            val map = fengMap(1)
            return map
        }
        //自营商品详情 免登陆
        fun getProductMap(productId: String): HashMap<String, String> {
            baseList.clear()
            baseList.add("product_id=$productId")
            baseList.add("route=app/product")
            val map = fengMap(0)
            map.put("product_id", productId)
            map.put("route", "app/product")
            return map
        }

        //添加购物车
        fun getAddSCMap(
            productId: String,
            quantity: Int,
            checkMap: ConcurrentHashMap<String, ArrayList<String>>
        ): Map<String, String> {
            baseList.clear()
            baseList.add("product_id=$productId")
            baseList.add("quantity=$quantity")
//            baseList.add("option=${getBaseCheckList(checkMap)}")
            baseList.add("route=app/cart/add")
            val map = fengMap2(1)
            map.put("product_id", productId)
            map.put("quantity", "$quantity")
            map.put("route", "app/cart/add")
            LogTool.e("map1", map.toString())
            //map 排序  https://www.jianshu.com/p/605dbb5e1712
            LogTool.e("map2", map.toString())
            getBaseCheckMap(checkMap, map)
            LogTool.e("map3", map.toString())  //todo 在此处自定义重写一个按value排序的方法...
            val maps = sortByValueDescending(map)
            LogTool.e("map4", maps.toString())  //todo 在此处自定义重写一个按value排序的方法...
            return maps
        }

        //更改、删除购物车
        fun getEditSCMap(cart_id: String, count: String, type: Int): HashMap<String, String> {
            baseList.clear()
            baseList.add("quantity=$count")
            baseList.add("cart_id=$cart_id")
            if (type == 1) {
                baseList.add("route=app/cart/edit")
            } else if (type == 2) {
                baseList.add("route=app/cart/remove")
            }
            val map = fengMap(1)
            map.put("quantity", "$count")
            map.put("cart_id", "$cart_id")
            if (type == 1) {
                map.put("route", "app/cart/edit")
            } else if (type == 2) {
                map.put("route", "app/cart/remove")
            }
            return map
        }

        //添加 地址
        fun getNewAdMap(
            name: String,
            phone: String,
            address_detail: String,
            city: String,
            zone_id: String
        ): HashMap<String, String> {
            baseList.clear()
            baseList.add("name=$name")
            baseList.add("address_detail=$address_detail")
            baseList.add("city=$city")
            baseList.add("zone_id=$zone_id")
            baseList.add("iphone=$phone")
            baseList.add("route=app/address/add")
            val map = fengMap(1)
            map.put("name", "$name")
            map.put("address_detail", "$address_detail")
            map.put("city", "$city")
            map.put("zone_id", "$zone_id")
            map.put("iphone",phone)
            map.put("route", "app/address/add")
            return map
        }
        //修改地址
        fun getEditAdMap(
            name: String,
            phone: String,
            address_detail: String,
            city: String,
            zone_id: String,
            address_id:String,
            default: Boolean
        ): HashMap<String, String> {
            baseList.clear()
            baseList.add("name=$name")
            baseList.add("address_detail=$address_detail")
            baseList.add("city=$city")
            baseList.add("zone_id=$zone_id")
            baseList.add("address_id=$address_id")
            baseList.add("default=$default")
            baseList.add("iphone=$phone")
            baseList.add("route=app/address/edit")
            val map = fengMap(1)
            map.put("name", name)
            map.put("address_detail", address_detail)
            map.put("city", city)
            map.put("zone_id", zone_id)
            map.put("address_id",address_id)
            map.put("default","$default")
            map.put("iphone",phone)
            map.put("route", "app/address/edit")
            return map
        }
        // 删除地址
        fun getDeleAdMap(
            address_id:String
        ): HashMap<String, String> {
            baseList.clear()
            baseList.add("address_id=$address_id")
            baseList.add("route=app/address/delete")
            val map = fengMap(1)
            map.put("address_id",address_id)
            map.put("route", "app/address/delete")
            return map
        }
        //提交订单
        fun getCoMap(list:List<String>):Map<String, String>{
            baseList.clear()
            baseList.add("route=app/checkout")
            val map = fengMap2(1)
            val maps = fillMap(list,map)
            return maps
        }
        //完成订单
        fun getSucMap(list:List<String>,address_id:String):Map<String, String>{
            baseList.clear()
            baseList.add("address_id=$address_id")
            baseList.add("route=app/checkout/confirm")
            val map = fengMap2(1)
            val maps = fillMap(list,map)
            maps.put("address_id",address_id)
            return maps
        }
        //商品列表分类请求
        fun getNewClassMap(parent_category_id:String):Map<String, String>{
            baseList.clear()
            baseList.add("route=app/category")
            baseList.add("parent_category_id=$parent_category_id")
            val map = fengMap(0)
            map.put("parent_category_id",parent_category_id)
            return map
        }
        //商品列表详情
        fun getNewCGoodMap(category_id:String):Map<String, String>{
            baseList.clear()
            baseList.add("route=app/category/goods")
            baseList.add("category_id=$category_id")
            val map = fengMap(0)
            map.put("category_id",category_id)
            return map
        }
        //商品搜索
        fun getSearchMap(search:String):Map<String, String>{
            baseList.clear()
            baseList.add("route=app/search")
            baseList.add("search=$search")
            val map = fengMap(0)
            map.put("search",search)
            return map
        }
        //商品详情
        fun getOrderDhMap(order_id:String):Map<String, String>{
            baseList.clear()
            baseList.add("route=app/order")
            baseList.add("order_id=$order_id")
            val map = fengMap(1)
            map.put("order_id",order_id)
            return map
        }
        //--------------------------------------------------------------------------------------------------------------
        //下面是解析购物车返回集合
        private fun getBaseCheckList(checkMap: ConcurrentHashMap<String, ArrayList<String>>): String {
            LogTool.e("signO", "checkList: ${checkMap.toString()}")
            val strList = arrayListOf<String>()
            for ((k, v) in checkMap) {
                LogTool.e("signOp", "k: $k  v: $v")
                if (v.size > 1) {
                    val buf: StringBuilder = StringBuilder()
                    for (y in 0 until v.size) {
                        if (y == 0) {
                            buf.append("$k=$y=${v[y]}")
                        } else {
                            buf.append("&$y=${v[y]}")
                        }
                    }
                    strList.add("${buf}")
                    continue
                } else if (v.size <= 1) {
                    strList.add("$k=${v[0]}")
                    continue
                }
            }
            strList.sort()
            val bufs: StringBuilder = StringBuilder()
            for (k in 0 until strList.size) {
                if (k == strList.size - 1) {
                    bufs.append(strList[k])
                } else {
                    bufs.append("${strList[k]}&")
                }
            }
            LogTool.e("signOption", bufs.toString())
            return bufs.toString()
        }

        private fun getBaseCheckMap(
            checkMap: ConcurrentHashMap<String, ArrayList<String>>,
            map: IdentityHashMap<String, String>
        ): Map<String, String> {
            //准换为 可以键值重复的map  //https://blog.csdn.net/static_coder/article/details/54894660
            for ((k, v) in checkMap) {
                if (v.size == 1) {
                    map.put("option[$k]", "${v!![0]}")
                    continue
                } else if (v.size > 1) {
                    //解决hashmap 不可以同键值重复的问题...
                    for (y in 0 until v.size) {
                        val str: String = String("option[$k][]".toByteArray())
                        map.put(str, v[y])
                    }
                    continue
                }

            }
            return map
        }

        //传入map排序算法
        private fun sortByValueDescending(map: Map<String, String>): IdentityHashMap<String, String> {
            val list = java.util.ArrayList<Map.Entry<String, String>>(map.entries)
            LogTool.e("mapList1", list.toString())
            Collections.sort(list, object : Comparator<Map.Entry<String, String>> {
                override fun compare(
                    o1: Map.Entry<String, String>?,
                    o2: Map.Entry<String, String>?
                ): Int {
                    val compare: Int = (o1?.value)!!.compareTo(o2?.value!!)
                    return compare
                }
            })
            val maps = IdentityHashMap<String, String>()
            list.forEach {
                LogTool.e("mapListItem", "key: ${it.key}   value:${it.value}")
                maps.put(String((it.key.toByteArray())), it.value)
            }
            return maps
        }
        //这是为 订单打造的map
        private fun fillMap(list:List<String>,checkMap: IdentityHashMap<String, String>): IdentityHashMap<String, String>{
            for(i in 0 until list.size){
                val str = String("cart_id[]".toByteArray())
                checkMap.put(str,list[i])
            }
            return checkMap
        }
    }
}