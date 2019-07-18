package com.example.juanshichang.widget

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.Settings
import android.widget.TextView

class IsInternet {
    companion object{
        /**
         * 判断网络情况
         * @param context 上下文
         * @return false 表示没有网络 true 表示有网络
         */
        fun isNetworkAvalible(context: Context):Boolean {



            // 获得网络状态管理器
            //        ConnectivityManager connectivityManager = (ConnectivityManager) context
            //                .getSystemService(Context.CONNECTIVITY_SERVICE);
            //
            //        if (connectivityManager == null) {
            //            return false;
            //        } else {
            //            // 建立网络数组
            //            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();
            //
            //            if (net_info != null) {
            //                for (int i = 0; i < net_info.length; i++) {
            //                    // 判断获得的网络状态是否是处于连接状态
            //                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
            //                        return true;
            //                    }
            //                }
            //            }
            //        }
            //        return false;


            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if(connectivityManager == null){
                return  false
            }
            val networkinfo = connectivityManager.activeNetworkInfo

            if (networkinfo == null || !networkinfo.isAvailable) {
                return false
            }
            return true
        }

        // 如果没有网络，则弹出网络设置对话框
        fun checkNetwork(activity: Activity) {
            if (!IsInternet.isNetworkAvalible(activity))
            {
                val msg = TextView(activity)
                msg.text = "    当前没有可以使用的网络，请设置网络！"
                AlertDialog.Builder(activity)
                    //.setIcon(R.drawable.login_mie)
                    .setTitle("网络状态提示")
                    .setView(msg)
                    .setPositiveButton("确定") { dialog, whichButton ->
                        // 跳转到设置界面
                        activity.startActivityForResult(Intent(Settings.ACTION_WIRELESS_SETTINGS),0)
                    }.create().show()
            }
            return
        }
    }
}