package com.example.juanshichang

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.text.TextUtils
import androidx.multidex.MultiDex
import com.alipay.sdk.app.EnvUtils
import com.bumptech.glide.Glide
import com.example.juanshichang.utils.*
import com.example.juanshichang.widget.MD5Utils
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
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
            Manifest.permission.READ_PHONE_STATE,
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
        //直接生成一个uuid 并返回
        fun getMD5uuidNew():String{
            return md5Uuid()
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
        //友盟相关平台配置，如果不配置会调不起来相关界面
        PlatformConfig.setWeixin("","")//微信APPID和AppSecret
        PlatformConfig.setQQZone("","")//QQAPPID和AppSecret
        PlatformConfig.setSinaWeibo("","","http://sns.whalecloud.com")//微博  微博APPID  微博APPSecret  微博的后台配置回调地址
        UMConfigure.setLogEnabled(BuildConfig.DEBUG) //是否开启日志
        UMConfigure.init(this,UMConfigure.DEVICE_TYPE_PHONE,null)
        initTencentBugly() //初始化腾讯bugly
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX) //支付宝 沙盒环境...
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun initTencentBugly() {
        //bugly 文档:https://bugly.qq.com/docs/user-guide/advance-features-android/?v=20180709165613#bugly_1
        val strategy = CrashReport.UserStrategy(applicationContext)
        strategy.setAppReportDelay(8000) //程序启动8s后初始化腾讯bugly
        CrashReport.initCrashReport(getApplicationContext(), "60bd157ca3",isDebug,strategy)
    }
    override fun onTerminate() { //程序终止的时候执行
        super.onTerminate()
    }

    /**
     * 在 lowMemory 的时候，调用 Glide.cleanMemroy() 清理掉所有的内存缓存。
    在 App 被置换到后台的时候，调用 Glide.cleanMemroy() 清理掉所有的内存缓存。
    在其它情况的 onTrimMemroy() 回调中，直接调用 Glide.trimMemory() 方法来交给 Glide 处理内存情况。
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN){
            Glide.get(this).clearMemory()
        }
        Glide.get(this).trimMemory(level)
    }
    override fun onLowMemory() {
        super.onLowMemory()
        //内存低的时候清理Glide缓存
        Glide.get(this).clearMemory()
    }

}