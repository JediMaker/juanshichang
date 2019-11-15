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
        val SEARCH = "/search"
        //商品详情查询
        val SEARCHDETAIL = "/apiserver/goodsdetail"
        // 商品分享链接 必须登录  login
        val SHARE = "/user/generateurl"
        // 商品店铺地址 必须登录 login
        val MALLURl = "/user/generate_mall_url"
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
        //列表
        val CATEGORY = "/category"
        //消息
        val MESSAGELIST = "/message_list"
        //支付宝提现
        val WITHDRAW = "/user/withdraw"
        //提现列表
        val HISTORY = "/user/withdraw/history"
        //修改提现账户
        val UPDZFB = "/user/withdraw/update_alipay_account"
        //servicer 关键字
        val Pdd = "pdd"
        val TaoBao = "taobao"
        val Jd = "jd"
        // ------------------------------------------------ new  port ------------------------------------------
        const val NEWBASEURL = "http://s.0371.ml:8080/"  //内网环境
//        const val NEWBASEURL = "http://shop.0371.ml:88/"  //外网测试
        val CART = "/index.php?route=app/cart"
        val CARTADD ="/index.php?route=app/cart/add"

        //聚合数据Key集
        val IDKey = "8bc635e7345dd2e160df47c7ed386818" //身份证实名信息
        val PhoneKey = "5c0ee202ae8c61f918e1367c7a007348"//手机号归属地查询
        val TeleKey = "d651d007f97138c79288519d84c756d9" //手机充值
        val OilCardKey = "4cc4a1c34ef8a88f7d07b7e1e43e27e4" //油卡充值
        //聚合接口
        const val JUHEAPi = "http://apis.juhe.cn"
        val TELCHECK = "/mobile/get" //手机号归属地查询
    }
}