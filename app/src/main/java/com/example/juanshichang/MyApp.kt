package com.example.juanshichang

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.multidex.MultiDex
import com.example.juanshichang.utils.Util
import com.example.juanshichang.widget.MD5Utils
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager

open class MyApp : Application() {
    companion object{
        lateinit var app: MyApp
        lateinit var sp: SharedPreferences
        lateinit var applicationContext: Context

        fun getInstance(): MyApp {
            return app
        }

        /**
         * 获取手机uuid
         */
        fun md5Uuid(): String {
            var smi = Util.getDeviceId(applicationContext)
            if (TextUtils.isEmpty(smi)) {
                smi = "iuapp"
            }
            return MD5Utils.getMD5Str(smi)
        }
    }

    override fun onCreate() {
        super.onCreate()
        QMUISwipeBackActivityManager.init(this)  //初始化 腾讯QMUI_Android
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}