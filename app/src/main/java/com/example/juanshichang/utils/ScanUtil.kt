package com.example.juanshichang.utils

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * author:翊-yzq
 * type: ScanUtil.kt
 * details: 这是一个扫一扫工具类   https://blog.csdn.net/zhaodecang/article/details/53402038
 * create-date:2019/8/29 16:16
 */
class ScanUtil {
    companion object{
        @SuppressLint("WrongConstant")
        //跳转 到 微信扫一扫
        public fun toWeChatScanDirect(context: Context){
            try {
                val intent = Intent()
                intent.setComponent(ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"))
                intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
                intent.setFlags(335544320)
                intent.setAction("android.intent.action.VIEW")
                context.startActivity(intent)
            }catch (e:Exception){
                toAliPayScan(context)
            }
        }
        //跳转 到 微信扫一扫
        fun openWeixinToQE_Code(context: Context){
            try {
                val intent = context.packageManager.getLaunchIntentForPackage("com.tencent.mm")
                intent?.putExtra("LauncherUI.From.Scaner.Shortcut", true)
                context.startActivity(intent)
            }catch (e:Exception){
                ToastUtil.showToast(context,"请安装微信 or 支付宝")
            }
        }
        //跳转到支付宝
        fun toAliPayScan(context: Context){
            try {
                //利用Intent打开支付宝
                //支付宝跳过开启动画打开扫码和付款码的urlscheme分别是：
                //alipayqr://platformapi/startapp?saId=10000007
                //alipayqr://platformapi/startapp?saId=2000005
                val uri:Uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007")
                val intent = Intent(Intent.ACTION_VIEW,uri)
                context.startActivity(intent)
            }catch (e:Exception){
                openWeixinToQE_Code(context)
            }
        }
    }
}