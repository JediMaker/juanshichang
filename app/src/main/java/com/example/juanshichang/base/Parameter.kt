package com.example.juanshichang.base

import android.text.TextUtils
import com.example.juanshichang.MyApp
import com.example.juanshichang.MyApp.Companion.getMD5uuid
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.widget.MD5Utils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Parameter {
    companion object {
        private var baseList = ArrayList<String>()//存放签名集合
        private var stringList = ArrayList<String>()//存放签名集合

        /**
         * 签名
         */
        fun getSignString(signType: Int, str: String): String {
            val appkey = MyApp.sp.getString("appkey", "0371.ml.appkey")   //获取AppKey
            val usertoken = SpUtil.getIstance().user.usertoken  //获取UserToken
            var sign: String = ""
            if (signType == 0) { //未登录
                sign =
                    MD5Utils.getMD5Str("|D|$str|K|$appkey")   //MD5(|D|+参数字符+|K|+Key)    //Key：系统统一密钥  todo 待询问
            } else if (signType == 1) { //登录
                sign = MD5Utils.getMD5Str("|D|$str|K|$usertoken")
                LogTool.e("signLogin", sign)
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
        fun initParameter(
            mmap: Map<String, String>,
            action: String,
            signType: Int
        ): HashMap<String, String> {
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
            val sbs = StringBuffer()
            for (i in 0 until stringList.size) {
                sbs.append(stringList[i] + "&")
            }
            val map = HashMap<String, String>()
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
//            val appkey = MyApp.sp.getString("appkey", "0371.ml.appkey")   //获取AppKey
            val appkey = "shop.0371.ml";   //获取AppKey
            val usertoken = SpUtil.getIstance().user.usertoken  //获取UserToken
            var sign: String = ""
            if (signType.equals("unlogin")) { //未登录
                sign =
                    MD5Utils.getMD5Str("|D|$fuji|K|$appkey")   //MD5(|D|+参数字符+|K|+Key)    //Key：系统统一密钥  todo 待询问
                LogTool.e("sign", "|D|$fuji|K|$appkey")
                LogTool.e("sign", MD5Utils.getMD5Str("|D|$fuji|K|$appkey"))
            } else if (signType.equals("login")) { //登录
                sign = MD5Utils.getMD5Str("|D|$fuji|K|$usertoken")
//                LogTool.e("sign2", "$usertoken")
//                LogTool.e("sign2", "|D|$fuji|K|$usertoken")
//                LogTool.e("sign2", MD5Utils.getMD5Str("|D|$fuji|K|$usertoken"))
            } else if (signType.equals("special")) { //其它情况
                sign = ""
            }
            return sign
        }

        /**
         * 公共参数
         */
        fun getPublicMap(
            signType: String,
            fuji: String
        ): HashMap<String, String> { //action: String,
            val map = HashMap<String, String>()
//            map.put("action", action)
            // 2020-07-01 更换新的授权模式 以下签名废弃
//            map.put("sign", getSign(signType, fuji))
//            map.put("uuid", getMD5uuid())
//            map.put("timestamp", (((System.currentTimeMillis()) / 1000).toString()))
            return map
        }

        /***
         * 字符集封装
         */
        fun getFuji(list: ArrayList<String>): String {//, action: String
//            list.add("action=$action")
//            list.add("clienttype=2")
//            list.add("timestamp=" + ((System.currentTimeMillis()) / 1000))
//            list.add("uuid=${getMD5uuid()}")
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
            val fengMap = getPublicMap(typeLogin, getFuji(baseList)) // methodName,   , methodName
            return fengMap
        }
        //--------------------------------------分割线-----------------------------------------------------
        /**
         * 注册/登录
         */
        fun getLoginMap(mobile: String, password: String): HashMap<String, String> {
            baseList.clear()
            baseList.add("mobile=$mobile")
            baseList.add("password=$password")
            baseList.add("route=pre/login")
            val map = fengMap("unlogin")
            map.put("mobile", mobile)
            map.put("password", password)
            return map
        }
        /**
         * 快速登录
         */
        fun getFastLoginMap(mobile: String, sms_code: String): HashMap<String, String> {
            baseList.clear()
            baseList.add("mobile=$mobile")
            baseList.add("sms_code=$sms_code")
            val map = fengMap("unlogin")
            map.put("mobile", mobile)
            map.put("sms_code", sms_code)
            return map
        }
        /**
         * 重置密码
         */
        fun resetPasswordMap(mobile: String, sms_code: String,password: String): HashMap<String, String> {
            baseList.clear()
            baseList.add("mobile=$mobile")
            baseList.add("sms_code=$sms_code")
            val map = fengMap("unlogin")
            map.put("mobile", mobile)
            map.put("sms_code", sms_code)
            map.put("password", password)
            return map
        }
        fun getRegisterMap(
            mobile: String,
            password: String,
            invite_code: String,
            sms_code: String
        ): HashMap<String, String> {
            baseList.clear()
            baseList.add("mobile=$mobile")
            baseList.add("password=$password")
            baseList.add("sms_code=$sms_code")
//            baseList.add("username="+"test666")
            baseList.add("route=pre/register")
            if(TextUtils.isEmpty(invite_code)){
                if(!MyApp.isDebug){
                    baseList.add("invite_code=D7FCA2")
                }
            }else{
                baseList.add("invite_code=$invite_code")
            }
            val map = fengMap("unlogin")
            map.put("mobile", mobile)
            map.put("password", password)
            map.put("sms_code", sms_code)
//            map.put("username", "test666")
            if (TextUtils.isEmpty(invite_code)) {
                if (!MyApp.isDebug) {
                    map.put("invite_code", "D7FCA2")
                }
            } else {
                map.put("invite_code", invite_code)
            }
            return map
        }

        /**
         * 商品搜索 pdd
         * @param servicer  只能为以下之一:pdd,taobao,jd
         * @param keyword  商品搜素关键字
         * @param page  搜索结果第几页
         * @param
         * @param sort_type  0-综合排序;1-按佣金比率升序;2-按佣金比例降序;3-按价格升序;4-按价格降序;5-按销量升序;6-按销量降序;7-优惠券金额排序升序;8-优惠券金额排序降序;9-券后价升序排序;10-券后价降序排序;11-按照加入多多进宝时间升序;12-按照加入多多进宝时间降序;13-按佣金金额升序排序;14-按佣金金额降序排序;15-店铺描述评分升序;16-店铺描述评分降序;17-店铺物流评分升序;18-店铺物流评分降序;19-店铺服务评分升序;20-店铺服务评分降序;27-描述评分击败同类店铺百分比升序，28-描述评分击败同类店铺百分比降序，29-物流评分击败同类店铺百分比升序，30-物流评分击败同类店铺百分比降序，31-服务评分击败同类店铺百分比升序，32-服务评分击败同类店铺百分比降序
         */
        fun getSearchMap(
            keyword: String,
            page: Int,
            page_size: Int,
            sort_type: Int,
            category_id: Int
        ): HashMap<String, String> { //servicer: String,
            baseList.clear()
//            baseList.add("servicer=$servicer")
            baseList.add("keyword=$keyword")
            baseList.add("page=$page")
            if (page_size != 20) {
                baseList.add("page_size=$page_size")
            }
            baseList.add("category_id=$category_id")
            baseList.add("sort_type=$sort_type")
            val map = fengMap("unlogin")
//            map.put("servicer", servicer)
            map.put("keyword", keyword)
            map.put("page", "$page")
            if (page_size != 20) {
                map.put("page_size", "$page_size")
            }
            map.put("category_id", "$category_id")
            map.put("sort_type", "$sort_type")
            return map
        }

        /**
         * 商品搜索详情 pdd
         * @param servicer  只能为以下之一:pdd,taobao,jd
         * @param goods_id  商品ID
         *
         */
        fun getSearchDetailsMap(servicer: String, goods_id: Long): HashMap<String, String> {
            baseList.clear()
            baseList.add("servicer=$servicer")
            baseList.add("goods_id=$goods_id")
            val map = fengMap("unlogin")
            map.put("servicer", servicer)
            map.put("goods_id", "$goods_id")
            return map
        }

        /**
         * @param isLogin  登录与否？ 必须登录
         * @param servicer  只能为以下之一:pdd,taobao,jd
         * @param goods_id  商品ID
         */
        fun getShareMap(
            isLogin: String,
            goods_id: Long,
            servicer: String
        ): HashMap<String, String> {
            baseList.clear()
            baseList.add("servicer=$servicer")
            baseList.add("goods_id=$goods_id")
            val map = fengMap(isLogin)
            map.put("servicer", servicer)
            map.put("goods_id", "$goods_id")
            return map
        }

        /**
         * 请求首页Banner轮播图 地址
         * todo 其它无参 免登陆 亦可
         */
        fun getMainBannerMap(): HashMap<String, String> {
            baseList.clear()
            val map = fengMap("unlogin")
            return map
        }

        /**
         * 请求首页Banner轮播图 地址
         * todo 其它无参需登录 亦可
         */
        fun getBenefitMap(): HashMap<String, String> {
            baseList.clear()
            val map = fengMap("login")
            return map
        }

        /**
         * todo 废弃的方法   已被getBaseSonMap代替
         * @param banner_id id值
         * @param offset  页面页码 optional:default 0
         * @param limit   请求数据条目数 optional;default 20
         */
        fun getBannerClickMap(banner_id: Long, offset: Int, limit: Int): HashMap<String, String> {
            baseList.clear()
            baseList.add("banner_id=$banner_id")
            val map = fengMap("unlogin")
            map.put("banner_id", "$banner_id")
            if (offset != 0) {
                baseList.add("offset=$offset")
                map.put("offset", "$offset")
            }
            if (limit != 20) {
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
        fun getGridClickMap(channel_id: Long, offset: Int, limit: Int): HashMap<String, String> {
            baseList.clear()
            baseList.add("channel_id=$channel_id")
            val map = fengMap("unlogin")
            map.put("channel_id", "$channel_id")
            if (offset != 0) {
                baseList.add("offset=$offset")
                map.put("offset", "$offset")
            }
            if (limit != 20) {
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
        fun getBaseSonMap(
            idName: String,
            id: Long,
            offset: Int,
            limit: Int
        ): HashMap<String, String> {
            baseList.clear()
            baseList.add("$idName=$id")
            if (offset != 0) {
                baseList.add("offset=$offset")
            }
            if (limit != 20) {
                baseList.add("limit=$limit")
            }
            val map = fengMap("unlogin")
            map.put(idName, "$id")
            if (offset != 0) {
                map.put("offset", "$offset")
            }
            if (limit != 20) {
                map.put("limit", "$limit")
            }
            return map
        }

        /**
         * @param theme_goods_count optional;default 20
         */
        fun getRecyclerMap(theme_goods_count: Int): HashMap<String, String> {
            baseList.clear()
            if (theme_goods_count != 2) {
                baseList.add("theme_goods_count=$theme_goods_count")
            }
            val map = fengMap("unlogin")
            if (theme_goods_count != 2) {
                map.put("theme_goods_count", "$theme_goods_count")
            }
            return map
        }

        /**
         * @param mobile 传入要发送短信的手机号
         */
        fun getVerifyCode(mobile: String,type: String): HashMap<String, String> {
            baseList.clear()
            baseList.add("mobile=$mobile")
            val map = fengMap("unlogin")
            map.put("mobile", mobile)
            map.put("type", type)
            return map
        }

        /***
         * 订单请求
         * @param offset
         * @param limit
         */
        fun getOrders(offset: Int, limit: Int): HashMap<String, String> {
            baseList.clear()
            if (offset != 0) {
                baseList.add("offset=$offset")
            }
            if (limit != 20) {
                baseList.add("limit=$limit")
            }
            val map = fengMap("login")
            if (offset != 0) {
                map.put("offset", "$offset")
            }
            if (limit != 20) {
                map.put("limit", "$limit")
            }
            return map
        }

        /***
         * 修改用户信息
         * @param nickname
         */
        fun getUpdInfo(nickname: String): HashMap<String, String> {
            baseList.clear()
            baseList.add("nickname=$nickname")
            val map = fengMap("login")
            map.put("nickname", nickname)
            return map
        }

        /***
         * 修改用户支付宝
         * @param ali_pay_account
         */
        fun getUpdZfb(ali_pay_account: String): HashMap<String, String> {
            baseList.clear()
            baseList.add("ali_pay_account=$ali_pay_account")
            val map = fengMap("login")
            map.put("ali_pay_account", ali_pay_account)
            return map
        }

        /***
         * 申请提现
         * @param amount
         */
        fun getTxZfb(amount: String): HashMap<String, String> {
            baseList.clear()
            baseList.add("amount=$amount")
            val map = fengMap("login")
            map.put("amount", amount)
            return map
        }

        /**
         * 获取首页Tab等数据
         * @param parent_id def 0
         * @param with_image def 0 决定是否带图 带图 为 1
         */
        fun getTabData(parent_id: Int, with_image: Int): HashMap<String, String> {
            baseList.clear()
            baseList.add("parent_id=$parent_id")
            baseList.add("with_image=$with_image")
            val map = fengMap("unlogin")
            map.put("parent_id", "$parent_id")
            map.put("with_image", "$with_image")
            return map
        }

        /**
         * 获取商品详情页链接地址
         * @param
         * @param
         */
        fun getStoreMap(mall_id: String, servicer: String): HashMap<String, String> {
            baseList.clear()
            baseList.add("mall_id=$mall_id")
            baseList.add("servicer=$servicer")
            val map = fengMap("login")
            map.put("mall_id", mall_id)
            map.put("servicer", servicer)
            return map
        }
    }
}
