package com.example.juanshichang.utils

import android.util.Log
import com.example.juanshichang.MyApp

/**
 * @作者：yzq
 * @创建时间：2019/9/2 12:17
 * @文件作用:  这是一个控制Log的工具类
 */
class LogTool {
    companion object{
        private val isShow = MyApp().getIsDebug() //true 为 debug ；false 为 正式版本
        /**打印*余日志
         * @param tag 字段名称
         * @param msg  内容
         */
        public fun v(tag:String,msg:String){
            if(isShow){
                Log.v(tag,msg)
            }
        }
        /**打印调试日志
         * @param tag 字段名称
         * @param msg  内容
         */
        public fun d(tag:String,msg:String){
            if(isShow){
                Log.d(tag,msg)
            }
        }
        /**打印一般日志
         * @param tag 字段名称
         * @param msg  内容
         */
        public fun i(tag:String,msg:String){
            if(isShow){
                Log.i(tag,msg)
            }
        }
        /**打印警告日志
         * @param tag 字段名称
         * @param msg  内容
         */
        public fun w(tag:String,msg:String){
            if(isShow){
                Log.w(tag,msg)
            }
        }
        /**打印错误日志
         * @param tag 字段名称
         * @param msg  内容
         */
        public fun e(tag:String,msg:String){
            if(isShow){
                Log.e(tag,msg)
            }
        }
    }
}