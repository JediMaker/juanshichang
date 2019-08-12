package com.example.juanshichang.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration

/**
 * @作者: yzq
 * @创建日期: 2019/8/12 15:38
 * @文件作用: 这是一个键盘 工具类
 */
class NavigationBarUtil {
    companion object{
        /**
         * @param activity 上下文对象
         * 这是一个判断设备是否是虚拟键盘的方法
         */
        @SuppressLint("NewApi")
        fun checkDeviceHasNavigationBar(activity:Context):Boolean{
            //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
            val hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey()
            val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            if(!hasMenuKey && !hasBackKey){
                // 做任何你需要做的,这个设备有一个导航栏
                return true
            }
            return false
        }

        /**
         * @param activity 上下文对象
         * 获取虚拟键盘高度
         */
        fun getNavigationBarHeight(activity:Context):Int{
            val resources = activity.resources
            val resourceId:Int = resources.getIdentifier("navigation_bar_height","dimen","android")
            //获取NavigationBar的高度
            val height:Int =  resources.getDimensionPixelSize(resourceId)
            return height
        }
    }
}