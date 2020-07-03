package com.example.juanshichang.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import androidx.core.text.isDigitsOnly
import com.example.juanshichang.activity.Reg2LogActivity
import com.example.juanshichang.base.BaseActivity
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import android.util.Base64

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
            val manager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val name = manager.getRunningTasks(1).get(0).topActivity?.getClassName()
//            return name.equals(activity.javaClass.name)
            return name!!.equals(activity::class.java.name)
        }

        /***
         * @param mobile 手机号
         *  验证手机号是否符合规范  这个校验是一个不太严谨的校验
         */
        fun validateMobile(mobile: String): Boolean {
            val p = Pattern.compile("^(1)\\d{10}$")
            val m: Matcher = p.matcher(mobile)
            return m.matches()
        }

        /**
         * 判断是否已经登陆  没有 直接跳转
         */
        fun hasLogin(context: Context): Boolean {
            if ("0".equals(SpUtil.getIstance().user.useruid?.toString())!!) {
                BaseActivity.goStartActivity(context, Reg2LogActivity())
                return false
            }
            return true
        }

        /**
         * 判断是否已经登陆
         */
        fun  hasLogin(): Boolean {
            if ("0".equals(SpUtil.getIstance().user.useruid.toString())||TextUtils.isEmpty(SpUtil.getIstance().user.useruid.toString())) {
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
            if (price is Float || price is Double) { //如果 传入的价格就是小数
                val ret = UtilsBigDecimal.add(price.toString().toDouble(), 0.0)
                return ret.toString()
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

        private var sf: SimpleDateFormat? = null
        /*时间戳转换成字符窜*/
        fun getDateToString(time: Long): String {
//            val d = Date(time)
//            sf = SimpleDateFormat("MM-dd") //yyyy年MM月dd日
//            return sf!!.format(d)
            sf = SimpleDateFormat("MM-dd")
            val str: String = sf!!.format(Date(time * 1000L))
            return str
        }

        /*时间戳转换成字符窜*/
        fun getTimedate(time: Long): String {
            sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//            @SuppressWarnings("unused")
//            long lcc = Long.valueOf(time);
            val str: String = sf!!.format(Date(time * 1000L))
            return str
        }

        /*时间戳转换成字符窜*/
        fun getTimedateTwo(time: Long): String {
            sf = SimpleDateFormat("yyyy-MM-dd")
//            @SuppressWarnings("unused")
//            long lcc = Long.valueOf(time);
            val str: String = sf!!.format(Date(time * 1000L))
            return str
        }

        /*时间戳转换成字符窜*/
        fun getTimedateThree(time: Long): String {
            sf = SimpleDateFormat("HH:mm:ss")
//            @SuppressWarnings("unused")
//            long lcc = Long.valueOf(time);
            val str: String = sf!!.format(Date(time * 1000L))
            return str
        }

        /** 手机 字符 加密**/
        fun getPhoneNTransition(mobile: String): String {
            val str = mobile
            var sb = StringBuilder()
            if (mobile.length > 0) {
                for (i in 0..str.length - 1) {
                    if (i < 3 || i > 6) { //保留 前三位 后四位
                        sb.append(str[i])
                    } else {
                        sb.append("*")
                    }
                }
            }
            return sb.toString()
        }

        /**
         * 生成图片保存地址工具类
         */
        fun createImageFile(context: Context): File? {
            try {
                val timeStamp: String =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val imageFileName = "IMG_" + timeStamp
                val dir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
                return File.createTempFile(imageFileName, ".png", dir)
            } catch (e: Exception) {

            }
            return null
        }
        //
        /**
         * 得到资源文件中图片的Uri地址
         * todo 废弃
         * @param id  资源id
         * @return Uri 地址
         */
        fun getUriFormDrawableRes(context: Context, id: Int): Uri {
            val resources: Resources = context.resources
            val path: String =
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(
                    id
                ) + "/" + resources.getResourceTypeName(id) + "/" + resources.getResourceEntryName(
                    id
                )
            return Uri.parse(path)
        }

        /**
         * @param min_group_price 最少拼团价
         * @param coupon_discount 优惠劵面额
         * @param promotion_rate  佣金比列
         * @param istype  类型
         */
        fun getProportion(
            min_group_price: Long,
            coupon_discount: Long,
            promotion_rate: Int,
            istype: Boolean
        ): String {
            var t: Long = 0
            if (istype) {//有券
                t = min_group_price - coupon_discount
            } else {
                t = min_group_price
            }
            val lastP = promotion_rate * t / 1000
            return getFloatPrice(lastP.toLong())
        }

        //这是去除单价中的人民币符号的方法
        fun removeRMB(str:String):Double{
            val ind = str.indexOf("¥")
            var newStr:String? = null
            if(ind != -1){
                newStr = str.substring(ind,str.length)
            }
            //判断是否只包含数字
            if(newStr?.isDigitsOnly()!!){
                return newStr.toDouble()
            }else{
                return 0.0
            }
        }

        //花样设置 合计金额  return 设置后的数据
       fun getGaudyStr(str: String): SpannableString {
            val spannableString = SpannableString(str)
            val textColor = ForegroundColorSpan(Color.parseColor("#F93736")) //文字颜色
//        StyleSpan : 字体样式：粗体、斜体等
            val dotInd = str.indexOf(".") //获取小数点的下标
            val rmbInd = str.indexOf("¥") //获取人民币符号的下标
            val frontSize = AbsoluteSizeSpan(56) //56px
            val behindSize = AbsoluteSizeSpan(72) //72px
            spannableString.setSpan(textColor, 0, str.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE) //设置颜色
            if (rmbInd != -1) {
                spannableString.setSpan(
                    frontSize,
                    rmbInd,
                    rmbInd + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                ) //设置人民币符号大小 前面包括，后面不包括
            }
            if (dotInd != -1) {
                spannableString.setSpan(
                    behindSize,
                    1,
                    dotInd,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                ) //设置元符号大小   前面包括，后面包括
                spannableString.setSpan(
                    frontSize,
                    dotInd,
                    str.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                ) //设置人民币符号颜色  前面不包括，后面不包括
            }
            //Spannable. SPAN_EXCLUSIVE_INCLUSIVE：前面不包括，后面包括
            //设置字体样式正常 NORMAL ，粗体 BOLD，斜体 ITALIC，粗斜体  BOLD_ITALIC
            spannableString.setSpan(StyleSpan(Typeface.BOLD),0,str.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spannableString
        }

        //读取本地json
        fun readLocalJson(mContext:Context,filename:String):String{
            val sb = StringBuilder()
            val am = mContext.assets
            try {
                val br = BufferedReader(InputStreamReader(am.open(filename)))
                var line:String
                while (true){
                    line = br.readLine()?:break
                    sb.append(line)
                }
                br.close()
            }catch (e:Exception){
                e.printStackTrace()
                sb.delete(0,sb.length)
            }
            return sb.toString().trim()
        }

        /**
         * 将图片转换成Base64编码的字符串
         *
         *
         * https://blog.csdn.net/qq_35372900/article/details/69950867
         */
        fun imageToBase64(path: File?): String? {
            var `is`: InputStream? = null
            val data: ByteArray
            var result: String? = null
            try {
                `is` = FileInputStream(path)
                //创建一个字符流大小的数组。
                data = ByteArray(`is`.available())
                //写入数组
                `is`.read(data)
                //用默认的编码格式进行编码
                result = Base64.encodeToString(
                    data,
                    Base64.NO_WRAP
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                if (null != `is`) {
                    try {
                        `is`.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return result
        }
    }
}