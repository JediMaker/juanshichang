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
        val isDebug:Boolean = false
        lateinit var app: MyApp
        lateinit var sp: SharedPreferences
        lateinit var applicationContext: Context
        var mPermissionList = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_LOGS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SET_DEBUG_APP,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.WRITE_APN_SETTINGS
        )
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
//                ToastUtil.showToast(context, "劵市场,感谢您的支持!!!")
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
   /*     PlatformConfig.setWeixin("","")//微信APPID和AppSecret
        PlatformConfig.setQQZone("","")//QQAPPID和AppSecret
        PlatformConfig.setSinaWeibo("","","http://sns.whalecloud.com")//微博  微博APPID  微博APPSecret  微博的后台配置回调地址*/
    /*    PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        //豆瓣RENREN平台目前只能在服务器端配置
        PlatformConfig.setSinaWeibo("40777395", "43150b5d2aecb064be3f1c5de6be0dc7","http://sns.whalecloud.com");
        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
        PlatformConfig.setAlipay("2015111700822536");
        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
        PlatformConfig.setPinterest("1439206");
        PlatformConfig.setKakao("e4f60e065048eb031e235c806b31c70f");
        PlatformConfig.setDing("dingoalmlnohc0wggfedpk");
        PlatformConfig.setVKontakte("5764965","5My6SNliAaLxEm3Lyd9J");
        PlatformConfig.setDropbox("oz8v5apet3arcdy","h7p2pjbzkkxt02a");*/
        UMConfigure.setLogEnabled(BuildConfig.DEBUG) //是否开启日志
        UMConfigure.init(this,UMConfigure.DEVICE_TYPE_PHONE,null)
        UMConfigure.init(this,"5f1a9a96d62dd10bc71bdfc8"
            ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");//58edcfeb310c93091c000be2 5965ee00734be40b580001a0
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