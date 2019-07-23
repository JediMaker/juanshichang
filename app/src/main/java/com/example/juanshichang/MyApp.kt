package com.example.juanshichang

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import androidx.multidex.MultiDex
import com.example.juanshichang.utils.Util
import com.example.juanshichang.widget.MD5Utils
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import java.util.*

open class MyApp : Application() {
    companion object {
        lateinit var app: MyApp
        lateinit var sp: SharedPreferences
        lateinit var applicationContext: Context

        fun getInstance(): MyApp {
            return app!!
        }

        /**
         * 获取手机uuid
         */
       private fun md5Uuid(): String {
//            var smi = Util.getDeviceId(applicationContext)
//            if (TextUtils.isEmpty(smi)) {
//                smi = "jscApp"
//            }
//            Log.e("smi",smi)
            val uuid:UUID = UUID.randomUUID() //更改为随机生成uuid
            val str = uuid.toString()
            Log.e("uuidStr",str)
            val uuidStr = str.replace("-", "")
            Log.e("uuidStrRep",uuidStr)
            return MD5Utils.getMD5Str(uuidStr)
        }
        /**
         * 根据需求每次登录重新生成uuid
         */
        fun getMD5uuid():String{
            var uuid:String = sp.getString("uu","")!!
            Log.e("uuidc",uuid)
            if(TextUtils.isEmpty(uuid) && uuid == ""){
                uuid = md5Uuid()
                sp.edit().putString("uu",uuid).apply()
                Log.e("uuidc2",uuid)
            }
            return uuid
        }
    }

    override fun onCreate() {
        super.onCreate()
        sp = getSharedPreferences("GXm", Activity.MODE_PRIVATE)
        Log.e("uuid","执行清理程序")
        sp.edit().remove("uu").commit()
        app = this
        Companion.applicationContext = this
        QMUISwipeBackActivityManager.init(this)  //初始化 腾讯QMUI_Android
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onTerminate() { //程序终止的时候执行
        super.onTerminate()
    }
}