package com.example.juanshichang.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaCodec
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @作者: yzq
 * @创建日期: 2019/7/16 15:31
 * @文件作用: 工具类
 */
class Util {
    companion object {
        //获取uuid
        fun getDeviceId(context: Context): String? {
            var deviceId: String? = ""
            if (deviceId != null && "" != deviceId) {
                return deviceId
            }
            if (deviceId == null || "" == deviceId) {
                try {
                    deviceId = getAndroidId(context)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            return deviceId
        }

        private fun getAndroidId(context: Context): String {
            return Settings.Secure.getString(
                context.contentResolver, Settings.Secure.ANDROID_ID
            )
        }

        /**
         * 加载圈
         * @param activity
         * @return
         */
        @SuppressLint("NewApi")
        fun ifCurrentActivityTopStack(activity: AutoLayoutActivity?): Boolean {
            if (activity == null || activity.isFinishing)
                return false
            val manager = activity.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val name = manager.getRunningTasks(1)[0].topActivity?.getClassName()
//            return name.equals(activity::class.java.name)
            return name!!.equals(activity.javaClass.name)
        }

        /***
         * @param mobile 手机号
         *  验证手机号是否符合规范  这个校验是一个不太严谨的校验
         */
        fun validateMobile(mobile: String): Boolean {
            val p = Pattern.compile("^(1)\\d{10}$")
            var m: Matcher = p.matcher(mobile)
            return m.matches()
        }

        /**
         * 判断是否已经登陆
         */
        fun hasLogin(): Boolean {
            if (TextUtils.isEmpty(SpUtil.getIstance().user.usertoken)) {
                return false
            }
            return true
        }

        /**
         * 根据拼多多接口特性
         * 自定义的 数据接口
         * @param price 传入单位为分的价格
         */
        fun getFloatPrice(price: Any): String {
            if (price is Long) {
                val penny = price % 10 //得到 分
                val dime = price % 100 //得到 角
                var retStr = ""
                if (penny == 0.toLong()) { //如果分位 为0 则
                    if (dime != 0.toLong()) {//如果角位 不为0 则
                        val d: Double = UtilsBigDecimal.div(price.toDouble(), 100.toDouble(), 1)
                        retStr = "$d"
                        return retStr
                    } else {//如果角位 为0 则
                        val d: Double = UtilsBigDecimal.div(price.toDouble(), 100.toDouble(), 0)
                        retStr = "$d"
                        return retStr
                    }
                } else { // 如果分位 不为0 则
                    val d: Double = UtilsBigDecimal.div(price.toDouble(), 100.toDouble(), 2)
                    retStr = "$d"
                    return retStr
                }
            }
            return ""
        }
        fun getIntPrice(price: Any): String {
            if (price is Long) {
                val penny = price % 10 //得到 分
                val dime = price % 100 //得到 角
                var retStr = ""
                if (penny == 0.toLong()) { //如果分位 为0 则
                    if (dime != 0.toLong()) {//如果角位 不为0 则
                        val d: Double = UtilsBigDecimal.div(price.toDouble(), 100.toDouble(), 1)
                        retStr = "${d.toInt()}"
                        return retStr
                    } else {//如果角位 为0 则
                        val d: Double = UtilsBigDecimal.div(price.toDouble(), 100.toDouble(), 0)
                        retStr = "${d.toInt()}"
                        return retStr
                    }
                } else { // 如果分位 不为0 则
                    val d: Double = UtilsBigDecimal.div(price.toDouble(), 100.toDouble(), 2)
                    retStr = "${d.toInt()}"
                    return retStr
                }
            }
            return ""
        }
        /**
         *清理Cookie
         */
        fun removeCookie(context: Context) {
            CookieSyncManager.createInstance(context)
            var cookieManager: CookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            CookieSyncManager.getInstance().sync()
        }

        private var  sf: SimpleDateFormat? = null
        /*时间戳转换成字符窜*/
        fun getDateToString(time:Long):String{
            val d = Date(time)
            sf = SimpleDateFormat("MM-dd") //yyyy年MM月dd日
            return sf!!.format(d)
        }

    }
}