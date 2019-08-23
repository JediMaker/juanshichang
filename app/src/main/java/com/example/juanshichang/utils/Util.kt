package com.example.juanshichang.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory




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
//            val d = Date(time)
//            sf = SimpleDateFormat("MM-dd") //yyyy年MM月dd日
//            return sf!!.format(d)
            sf = SimpleDateFormat("MM-dd")
            val str:String = sf!!.format(Date(time*1000L))
            return str
        }
        /*时间戳转换成字符窜*/
        fun getTimedate(time:Long):String{
            sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//            @SuppressWarnings("unused")
//            long lcc = Long.valueOf(time);
            val str:String = sf!!.format(Date(time*1000L))
            return str
        }
        /*时间戳转换成字符窜*/
        fun getTimedateTwo(time:Long):String{
            sf = SimpleDateFormat("yyyy-MM-dd")
//            @SuppressWarnings("unused")
//            long lcc = Long.valueOf(time);
            val str:String = sf!!.format(Date(time*1000L))
            return str
        }
        /** 手机 字符 加密**/
        fun getPhoneNTransition(mobile: String):String{
            val str = mobile
            var sb = StringBuilder()
            if(mobile.length>0){
                for (i in 0 .. str.length-1){
                    if(i<3 || i>7){
                        sb.append(str[i])
                    }
                    sb.append("*")
                }
            }
            return sb.toString()
        }

        /**
         * 生成图片保存地址工具类
         */
        fun createImageFile(context:Context): File?{
            try {
                val timeStamp:String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val imageFileName = "IMG_" + timeStamp
                val dir:File =  context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
                return File.createTempFile(imageFileName,".png",dir)
            }catch (e:Exception){

            }
            return null
        }

        /**
         * @param min_group_price 最少拼团价
         * @param coupon_discount 优惠劵面额
         * @param promotion_rate  佣金比列
         * @param istype  类型
         */
        fun getProportion(min_group_price:Long,coupon_discount:Long,promotion_rate:Int,istype:Boolean):String{
            var t:Long= 0
            if(istype){//有券
                t = min_group_price - coupon_discount
            }else{
                t = min_group_price
            }
            val lastP = promotion_rate*t/1000
            return getFloatPrice(lastP.toLong())
        }
    }
}