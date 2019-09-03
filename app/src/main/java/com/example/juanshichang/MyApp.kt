package com.example.juanshichang

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.multidex.MultiDex
import com.example.juanshichang.utils.JumpPermissionManagement
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import com.example.juanshichang.widget.MD5Utils
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.yanzhenjie.permission.AndPermission
import java.util.*

open class MyApp : Application() {

    companion object {
        //todo 正式包 isDebug 一定要修改为 false ！！！
        val isDebug:Boolean = true
        lateinit var app: MyApp
        lateinit var sp: SharedPreferences
        lateinit var applicationContext: Context
        private val PERMISSION_CAM = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        Manifest.permission.RECORD_AUDIO
        ) //新添 Manifest.permission.RECORD_AUDIO  音频
        fun getInstance(): MyApp {
            return app
        }

        /**
         * 获取手机uuid
         */
       private fun md5Uuid(): String {
//            var smi = Util.getDeviceId(applicationContext)
//            if (TextUtils.isEmpty(smi)) {
//                smi = "jscApp"
//            }
//            LogTool.e("smi",smi)
            val uuid:UUID = UUID.randomUUID() //更改为随机生成uuid
            val str = uuid.toString()
            LogTool.e("uuidStr",str)
            val uuidStr = str.replace("-", "")
            LogTool.e("uuidStrRep",uuidStr)
            return MD5Utils.getMD5Str(uuidStr)
        }
        /**
         * 根据需求每次登录重新生成uuid
         */
        fun getMD5uuid():String{
            var uuid:String = sp.getString("uu","")!!
            LogTool.e("uuidc",uuid)
            if(TextUtils.isEmpty(uuid) && uuid == ""){
                uuid = md5Uuid()
                sp.edit().putString("uu",uuid).apply()
                LogTool.e("uuidc2",uuid)
            }
            return uuid
        }

        /**
         *  权限请求
         */
        fun requestPermission(context: Context){
            AndPermission.with(context).runtime().permission(PERMISSION_CAM).onGranted({
                //使用权限
                ToastUtil.showToast(context, "劵市场,感谢您的支持!!!")
            }).onDenied({
                //拒绝使用权限
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //如果sdk大于23则跳转
                    ToastUtil.showToast(context, "请前往设置中心开启相关权限")
                    JumpPermissionManagement.GoToSetting(context as Activity)
                }
            }).start()
        }
    }
    fun getIsDebug():Boolean{
        return isDebug
    }
    override fun onCreate() {
        super.onCreate()
        sp = getSharedPreferences("GXm", Activity.MODE_PRIVATE)
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