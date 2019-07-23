package com.example.juanshichang.base

/**
 * @作者: yzq
 * @创建日期: 2019/7/16 14:31
 * @文件作用: Url存放
 */
interface Api {
    companion object {
//        const val BASEURL = "http://dev.0371.ml:8080/" // 内网环境
//        const val BASEURL = "http://in.0371.ml:8080/" // 阿里环境
        const val BASEURL = "http://192.168.5.67:8080/" // 本地环境
        //注册
        val USER = "/register"
        //登录
        val LOGIN = "/login"
        //用户信息
        val USERINFO = "/user/info"
    }
}