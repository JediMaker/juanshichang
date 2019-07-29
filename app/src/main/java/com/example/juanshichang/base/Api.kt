package com.example.juanshichang.base

/**
 * @作者: yzq
 * @创建日期: 2019/7/16 14:31
 * @文件作用: Url存放
 */
interface Api {
    companion object {
        //        const val BASEURL = "http://dev.0371.ml:8080/" // 内网环境
        const val BASEURL = "http://in.0371.ml:88/" // 阿里环境
        //        const val BASEURL = "http://192.168.5.67:8080/" // 本地环境
        //注册
        val USER = "/register"
        //登录
        val LOGIN = "/login"
        //用户信息 login
        val USERINFO = "/user/info"
        //商品搜索 unlogin
        val SEARCH = "/apiserver/search"
        //商品详情查询
        val SEARCHDETAIL = "/apiserver/goodsdetail"
        // 商品分享链接 必须登录  login
        val SHARE = "/user/generateurl"


        //servicer 关键字
        val Pdd = "pdd"
        val TaoBao = "taobao"
        val Jd = "jd"
    }
}