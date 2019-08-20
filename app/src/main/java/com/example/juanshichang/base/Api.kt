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
        val USERINFO = "/user/info"   //todo 有更改的接口
        //商品搜索 unlogin
        val SEARCH = "/apiserver/search"
        //商品详情查询
        val SEARCHDETAIL = "/apiserver/goodsdetail"
        // 商品分享链接 必须登录  login
        val SHARE = "/user/generateurl"
        //首页banner
        val MAINBANNER = "/index/bannerlist" //  /theme/list   delete
        //Banner 条目请求商品地址
        val BANNERITEM= "/index/banner" //   /theme/theme_goods  delete
        //请求GridView条目
        val CHANNELLIST = "/index/channellist"
        //GridView条目数据请求
        val CHANNEL = "/index/channel"
        //请求首页RecyclerView条目
        val THEMELIST = "/index/themelist"
        //RecyclerView条目数据请求
        val THEME = "/index/theme"
        //发送验证码
        val SMSSEND = "/smssend"
        //订单
        val ORDERS = "/user/orders"
        //粉丝
        val FANS = "/user/fans"
        //收益
        val BENFIT = "/user/benefit"
        //用户信息
        val SETTINGS = "/user/settings"
        //修改头像
        val SETAVATER = "/user/settings/upload_avatar"
        //修改昵称
        val SETINFO = "/user/settings/update_info"
        //servicer 关键字
        val Pdd = "pdd"
        val TaoBao = "taobao"
        val Jd = "jd"
    }
}