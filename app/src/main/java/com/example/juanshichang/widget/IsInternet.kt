package com.example.juanshichang.widget

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import android.provider.Settings
import android.widget.TextView

class IsInternet {
    companion object {
        /**
         * 判断网络情况
         * @param context 上下文
         * @return false 表示没有网络 true 表示有网络
         */
        fun isNetworkAvalible(context: Context): Boolean {


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


            /*val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if(connectivityManager == null){
                return  false
            }
            val networkinfo = connectivityManager.activeNetworkInfo
            if (networkinfo == null || !networkinfo.isAvailable) {
                return false
            }
            return true*/
            //参考 https://blog.csdn.net/weixin_34067049/article/details/86890998

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) { //26..
                if (context != null) {
                    try {
                        val connectivityManager =
                            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        if (connectivityManager != null) {
                            val netWorkInfo: NetworkInfo = connectivityManager.activeNetworkInfo
                            if (netWorkInfo != null) {
                                return netWorkInfo.isAvailable
                            }
                        }
                    } catch (e: Exception) {
                        return  false
                    }

                }
            } else {
                if (context != null) {
                    /*val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    var con = false
                    cm.requestNetwork(NetworkRequest.Builder().build(),object : ConnectivityManager.NetworkCallback(){
                        override fun onLost(network: Network) {
                            super.onLost(network)
                            //网络不可用的情况下的方法
                            con = false
                        }

                        override fun onAvailable(network: Network) {
                            super.onAvailable(network)
                            //网络可用的情况下的方法
                            con = true
                        }
                    })
                    return con*/

                    //参考 https://blog.csdn.net/weixin_34067049/article/details/86890998
                    var con = false
                    val cm =
                        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager//获得ConnectivityManager对象
                    val networks = cm.getAllNetworks()//获取所有网络连接的信息
//                    var sb = StringBuilder() //用于存放网络连接信息
                    for (index in 0 until networks.size) {//通过循环将网络信息逐个取出来
                        val networkInfo = cm.getNetworkInfo(networks[index])
//                        sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected())
                        if (networkInfo.isConnected) {
                            con = true
                        }
                    }
                    return con
                }
            }
            return false
        }

        // 如果没有网络，则弹出网络设置对话框
        fun checkNetwork(activity: Activity) {
            if (!IsInternet.isNetworkAvalible(activity)) {
                val msg = TextView(activity)
                msg.text = "    当前没有可以使用的网络，请设置网络！"
                AlertDialog.Builder(activity)
                    //.setIcon(R.drawable.login_mie)
                    .setTitle("网络状态提示")
                    .setView(msg)
                    .setPositiveButton("确定") { dialog, whichButton ->
                        // 跳转到设置界面
                        activity.startActivityForResult(
                            Intent(Settings.ACTION_WIRELESS_SETTINGS),
                            0
                        )
                    }.create().show()
            }
            return
        }
    }
}