package com.example.juanshichang.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
/**
 * @作者: yzq
 * @创建日期: 2019/7/19 15:00
 * @文件作用: Toast 弹窗工具类
 */
class ToastUtil {
    companion object{
        private var sToast:Toast? = null

        fun showToast(context: Context, text: String) {
            showToastInner(context, text, Toast.LENGTH_SHORT)
        }

        fun showToast(context: Context, stringId: Int) {
            showToastInner(context, context.getString(stringId), Toast.LENGTH_SHORT)
        }


        fun showToastLong(context: Context, text: String) {
            showToastInner(context, text, Toast.LENGTH_LONG)
        }

        fun showToastLong(context: Context, stringId: Int) {
            showToastInner(context, context.getString(stringId), Toast.LENGTH_LONG)
        }
        private fun showToastInner(context:Context,text:String,duration:Int){
            ensureToast(context)
            sToast?.setText(text)
            sToast?.setDuration(duration)
            sToast?.show()
        }
        @SuppressLint("ShowToast")
        private fun ensureToast(context:Context){
            if (sToast != null) {
                return
            }
            synchronized(ToastUtil::class.java) {
                if (sToast != null) {
                    return
                }
                sToast = Toast.makeText(context.applicationContext, " ", Toast.LENGTH_SHORT)
            }
        }
    }

}