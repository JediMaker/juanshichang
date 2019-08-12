package com.example.juanshichang.base

import com.example.juanshichang.MyApp
import com.example.juanshichang.MyApp.Companion.getMD5uuid
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.widget.MD5Utils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Parameter {
    companion object {
        var baseList = ArrayList<String>()//存放签名集合
        var stringList = ArrayList<String>()//存放签名集合

        /**
         * 签名
         */
        fun getSignString(signType: Int, str: String): String {
            var appkey = MyApp.sp.getString("appkey", "0371.ml.appkey")   //获取AppKey
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
            var map = HashMap<String, String>()
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
            var appkey = MyApp.sp.getString("appkey", "0371.ml.appkey")   //获取AppKey
            var usertoken = SpUtil.getIstance().user.usertoken  //获取UserToken
            var sign: String = ""
            if (signType.equals("unlogin")) { //未登录
                sign = MD5Utils.getMD5Str("|D|$fuji|K|$appkey")   //MD5(|D|+参数字符+|K|+Key)    //Key：系统统一密钥  todo 待询问
//                Log.e("sign", "|D|$fuji|K|$appkey")
//                Log.e("sign", MD5Utils.getMD5Str("|D|$fuji|K|$appkey"))
            } else if (signType.equals("login")) { //登录
                sign = MD5Utils.getMD5Str("|D|$fuji|K|$usertoken")
//                Log.e("sign2", "$usertoken")
//                Log.e("sign2", "|D|$fuji|K|$usertoken")
//                Log.e("sign2", MD5Utils.getMD5Str("|D|$fuji|K|$usertoken"))
            } else if (signType.equals("special")) { //其它情况
                sign = ""
            }
            return sign
        }

        /**
         * 公共参数
         */
        fun getPublicMap(signType: String, fuji: String): HashMap<String, String> { //action: String,
            var map = HashMap<String, String>()
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

        /**
         * 商品搜索 pdd
         * @param servicer  只能为以下之一:pdd,taobao,jd
         * @param keyword  商品搜素关键字
         * @param page  搜索结果第几页
         */
        fun getSearchMap(servicer: String, keyword: String,page: Int): HashMap<String, String>{
            baseList.clear()
            baseList.add("servicer=$servicer")
            baseList.add("keyword=$keyword")
            baseList.add("page=$page")
            var map = fengMap("unlogin")
            map.put("servicer", servicer)
            map.put("keyword", keyword)
            map.put("page", "$page")
            return map
        }
        /**
         * 商品搜索详情 pdd
         * @param servicer  只能为以下之一:pdd,taobao,jd
         * @param goods_id  商品ID
         *
         */
        fun getSearchDetailsMap(servicer: String, goods_id: Long): HashMap<String, String>{
            baseList.clear()
            baseList.add("servicer=$servicer")
            baseList.add("goods_id=$goods_id")
            var map = fengMap("unlogin")
            map.put("servicer", servicer)
            map.put("goods_id", "$goods_id")
            return map
        }

        /**
         * @param isLogin  登录与否？ 必须登录
         * @param servicer  只能为以下之一:pdd,taobao,jd
         * @param goods_id  商品ID
         */
        fun getShareMap(isLogin:String,goods_id:Long,servicer:String): HashMap<String, String>{
            baseList.clear()
            baseList.add("servicer=$servicer")
            baseList.add("goods_id=$goods_id")
            var map = fengMap(isLogin)
            map.put("servicer", servicer)
            map.put("goods_id", "$goods_id")
            return map
        }
        /**
         * 请求首页Banner轮播图 地址
         * todo 其它无参 亦可
         */
        fun getMainBannerMap(): HashMap<String, String>{
            baseList.clear()
            var map = fengMap("unlogin")
            return map
        }

        /**
         * todo 废弃的方法   已被getBaseSonMap代替
         * @param banner_id id值
         * @param offset  页面页码 optional:default 0
         * @param limit   请求数据条目数 optional;default 20
         */
        fun getBannerClickMap(banner_id:Long,offset:Int,limit:Int): HashMap<String, String>{
            baseList.clear()
            baseList.add("banner_id=$banner_id")
            var map = fengMap("unlogin")
            map.put("banner_id", "$banner_id")
            if(offset != 0){
                baseList.add("offset=$offset")
                map.put("offset", "$offset")
            }
            if(limit != 20){
                baseList.add("limit=$limit")
                map.put("limit", "$limit")
            }
            return map
        }
        /**
         * todo 废弃的方法 已被getBaseSonMap代替
         * @param channel_id id值
         * @param offset  页面页码 optional:default 0
         * @param limit   请求数据条目数 optional;default 20
         */
        fun getGridClickMap(channel_id:Long,offset:Int,limit:Int): HashMap<String, String>{
            baseList.clear()
            baseList.add("channel_id=$channel_id")
            var map = fengMap("unlogin")
            map.put("channel_id", "$channel_id")
            if(offset != 0){
                baseList.add("offset=$offset")
                map.put("offset", "$offset")
            }
            if(limit != 20){
                baseList.add("limit=$limit")
                map.put("limit", "$limit")
            }
            return map
        }
        /**
         * @param idName 传入id的名称进行操作
         * @param id id值
         * @param offset  页面页码 optional:default 0
         * @param limit   请求数据条目数 optional;default 20
         */
        fun getBaseSonMap(idName:String,id:Long,offset:Int,limit:Int): HashMap<String, String>{
            baseList.clear()
            baseList.add("$idName=$id")
            var map = fengMap("unlogin")
            map.put("$idName", "$id")
            if(offset != 0){
                baseList.add("offset=$offset")
                map.put("offset", "$offset")
            }
            if(limit != 20){
                baseList.add("limit=$limit")
                map.put("limit", "$limit")
            }
            return map
        }

        /**
         * @param theme_goods_count optional;default 20
         */
        fun getRecyclerMap(theme_goods_count:Int): HashMap<String, String>{
            baseList.clear()
            var map = fengMap("unlogin")
            if(theme_goods_count != 2){
                baseList.add("theme_goods_count=$theme_goods_count")
                map.put("theme_goods_count", "$theme_goods_count")
            }
            return map
        }

        /**
         * @param mobile 传入要发送短信的手机号
         */
        fun getVerifyCode(mobile:String):HashMap<String,String>{
            baseList.clear()
            var map = fengMap("unlogin")
            baseList.add("mobile=$mobile")
            map.put("mobile", mobile)
            return map
        }
    }
}