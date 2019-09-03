package com.example.juanshichang.utils

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.example.juanshichang.R
import com.example.juanshichang.activity.MessageActivity
import com.yanzhenjie.permission.setting.Setting

/**
 * @作者：yzq
 * @创建时间：2019/9/2 13:49
 * @文件作用:  这是一个消息通知工具类
 */
class NotifyUtil {
    companion object{
        fun sendSimpleNotify(context:Context,title:String,message:String){
            //创建跳转意图
            val cancel: Intent = Intent(context,MessageActivity::class.java)
            //创建延迟意图
            val deleteIntent:PendingIntent = PendingIntent.getActivity(context,
                                                                    R.string.app_name,
                                                                    cancel,
                                                                    PendingIntent.FLAG_UPDATE_CURRENT)
            //创建通知消息构造器
            var builder:Notification.Builder? = null
            //Android 8.0 开始 必须给每个通知分配对应的渠道
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ //26
                builder = Notification.Builder(context,context.getString(R.string.app_name))
                createNotifyChannel(context,context.getString(R.string.app_name)) //创建渠道
            }else{
                builder = Notification.Builder(context)
            }
            builder.setContentIntent(deleteIntent)//设置内容的点击意图
//                setDeleteIntent(deleteIntent) //设置清除意图
                .setAutoCancel(true) //设置是否允许自动取消
//                .setUsesChronometer(false) //设置是否显示计数器
//                .setProgress(100,60,false) //设置进度条 与 当前进度
//                .setNumber(66) //设置通知栏 右下方数字
                .setSmallIcon(R.mipmap.ic_launcher_round) //设置状态栏小图标
                .setTicker("劵市场 更新啦！！！") //设置状态栏提示文本
                .setWhen(System.currentTimeMillis()) //设置推送时间，格式为 '小时：分钟 日期？'
                //设置通知栏大图标
                .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.mipmap.ic_launcher))
                .setContentTitle(title) //设置通知栏的标题文本
                .setContentText(message) //设置通知栏的内容文本
                //构建 一个通知对象
            val notify:Notification = builder.build()
            //从系统服务拿到 通知管理器
            val notifyMgr:NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //使用系统服务 - 通知管理器 推送通知
            notifyMgr.notify(R.string.app_name,notify)
        }
        @TargetApi(Build.VERSION_CODES.O)
        //创建通知渠道 8.0之后 必须给每个通知分配渠道
        public fun createNotifyChannel(ctx:Context,channeId:String){
            //创建一个 默认重要性 的通知渠道
            val channel:NotificationChannel = NotificationChannel(channeId,"Channel",NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(null,null) //设置推送 通知铃声 null 则跟随系统
            channel.enableLights(true)  //桌面图标 右上角 小红点展示
            channel.lightColor = R.color.indicatorRed //设置桌面小红点的颜色
            channel.setShowBadge(true)  //长按桌面图标 显示该渠道信息
            //从系统服务中拿到 通知管理器
            val notifyMgr:NotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //创建指定的通知渠道
            notifyMgr.createNotificationChannel(channel)
        }


        /**
         * todo 下面是判断通知权限 和 跳转通知设置的方法
         */
        fun isPermissonOpen(context: Context):Boolean{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                return NotificationManagerCompat.from(context).importance != NotificationManager.IMPORTANCE_NONE
            }
            // areNotificationsEnabled只对 Api 19 以上有效 低于 则一直返回为 true
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
        //打开 通知 设置页面
        fun openPermissionSetting(context: Context){
            try {
                val localIntent = Intent()
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //直接跳转到应用设置通知的
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    localIntent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    localIntent.putExtra(Settings.EXTRA_APP_PACKAGE,context.packageName)
                    context.startActivity(localIntent)
                    return
                }
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                    localIntent.putExtra("app_package",context.packageName)
                    localIntent.putExtra("app_uid",context.applicationInfo.uid)
                    context.startActivity(localIntent)
                    return
                }
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    localIntent.addCategory(Intent.CATEGORY_DEFAULT)
                    localIntent.setData(Uri.parse("package:${context.packageName}"))
                    context.startActivity(localIntent)
                    return
                }
                //4.4以下 没有从app跳转到应用设置的action 可以考虑跳转到应用详情页
                if(Build.VERSION.SDK_INT >= 9){
                    localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    localIntent.setData(Uri.fromParts("package",context.packageName,null))
                    context.startActivity(localIntent)
                    return
                }
                //以下 个人瞎写系列  永远不会执行的Code....
                localIntent.setAction(Intent.ACTION_VIEW)
                localIntent.setClassName("com.android.settings","com.android.setting.InstalledAppDetails")
                localIntent.putExtra("com.android.settings.ApplicationPkgName",context.packageName)
                context.startActivity(localIntent) //prv  add
            }catch (e:Exception){
                e.printStackTrace()
                println("Open  Notify Setting 问题！！！")
            }
        }
    }
}