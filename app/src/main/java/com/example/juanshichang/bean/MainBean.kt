package com.example.juanshichang.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
/**
 * @作者: yzq
 * @创建日期: 2019/7/16 14:52
 * @文件作用:  基类
 */
@SuppressLint("ParcelCreator") //// 用于处理 Lint 的错误提示
@Parcelize
open class MainBean<T> : Parcelable{
    private var code: String? = null
    private var msg: String? = null
    private var data: T? = null

    fun getCode(): String? {
        return code
    }

    fun setCode(code: String) {
        this.code = code
    }

    fun getMsg(): String? {
        return msg
    }

    fun setMsg(msg: String) {
        this.msg = msg
    }

    fun getData(): T? {
        return data
    }

    fun setData(data: T) {
        this.data = data
    }

    /*override fun toString(): String {
        return "MainBean{" +
                "code :'" + code + '\''.toString() +
                ", msg :'" + msg + '\''.toString() +
                ", data :" + data +
                '}'.toString()
    }*/
}