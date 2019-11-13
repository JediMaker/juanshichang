package com.example.juanshichang.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.example.juanshichang.MyApp
import com.example.juanshichang.utils.ToastUtil.Companion.sToast

/**
 * author:翊-yzq
 * type: ToastTool.kt
 * details:  这是一个 用于控制 debug 与否的Toast工具类 如果 不考虑debug与否 建议移步 ToastUtil.kt
 * create-date:2019/9/2 12:02
 */
class ToastTool {
    companion object{
        private val isShown = MyApp().getIsDebug() //true 为 debug ；false 为 正式版本  MyApp().getIsDebug()
        private var sToast:Toast? = null
        //显示提示 默认 短...
        public fun showToast(context: Context,msg:String){
            if (isShown){
                showToastInner(context,msg,Toast.LENGTH_SHORT)
            }
        }
        //显示短提示
        public fun showShort(context: Context,msg:String){
            if (isShown){
                showToastInner(context,msg,Toast.LENGTH_SHORT)
            }
        }
        //显示长提示
        public fun showLong(context: Context,msg:String){
            if (isShown){
                showToastInner(context,msg,Toast.LENGTH_LONG)
            }
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
                sToast = Toast.makeText(context.applicationContext, "", Toast.LENGTH_SHORT)
            }
        }
    }
}