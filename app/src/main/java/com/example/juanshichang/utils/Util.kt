package com.example.juanshichang.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
/**
 * @作者: yzq
 * @创建日期: 2019/7/16 15:31
 * @文件作用: 工具类
 */
class Util {
    companion object{
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
            return  name.equals(activity.javaClass.name)
        }
    }
}