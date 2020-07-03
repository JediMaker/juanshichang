package com.example.juanshichang.base

/**
 * @作者: yzq
 * @创建日期: 2019/7/16 14:31
 * @文件作用: Url存放
 */
interface Api {
    companion object {
        //        const val BASEURL = "http://dev.0371.ml:8080/" // 内网环境
        const val BASEURL = "http://mxss.bainianmao.com:88/" // 阿里环境
        //        const val BASEURL = "http://192.168.5.67:8080/" // 本地环境
        //注册
        val USER = "/api/index.php?route=api/user/register"
        //登录
        val LOGIN = "/api/index.php?route=api/user/login"
        //快速登录
        val FASTLOGIN = "/api/index.php?route=api/user/tel_login"
        //登录
        val GETTOKEN = "/api/index.php?route=api/token"
        //用户信息 login
//        val USERINFO = "/user/info"   //todo 有更改的接口
        val USERINFO = "/api/index.php?route=api/user/info"
        val INVITECODE = "/api/index.php?route=api/invitecode"
        //修改头像
        val SETAVATER = "/api/index.php?route=api/user/eface"
        //修改提现账户
        val UPDZFB = "/api/index.php?route=api/user/epay"
        //修改昵称
        val SETINFO = "/api/index.php?route=api/user/euname"
        //发送验证码
        val SMSSEND = "/api/index.php?route=api/sms/send"

        //        val USERINFO = "/index.php?route=app/user"
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
        val BANNERITEM = "/index/banner" //   /theme/theme_goods  delete

        //请求GridView条目
        val CHANNELLIST = "/index/channellist"

        //GridView条目数据请求
        val CHANNEL = "/index/channel"

        //请求首页RecyclerView条目
        val THEMELIST = "/index/themelist"

        //RecyclerView条目数据请求
        val THEME = "/index/theme"
        //订单
        val ORDERS = "/user/orders"

        //粉丝
        val FANS = "/user/fans"

        //收益
        val BENFIT = "/user/benefit"

        //用户信息
        val SETTINGS = "/user/settings"

/*
        //修改头像
        val SETAVATER = "/user/settings/upload_avatar"
*/

        /*
                //修改头像
                val SETAVATER = "/user/settings/upload_avatar"
                //修改昵称
                val SETINFO = "/user/settings/update_info"
        */
        //列表
        val CATEGORY = "/category"

        //消息
        val MESSAGELIST = "/message_list"

        //支付宝提现
        val WITHDRAW = "/user/withdraw"

        //提现列表
        val HISTORY = "/user/withdraw/history"

        /*
                //修改提现账户
                val UPDZFB = "/user/withdraw/update_alipay_account"
        */
        //servicer 关键字
        val Pdd = "pdd"
        val TaoBao = "taobao"
        val Jd = "jd"

        // ------------------------------------------------ new  port ------------------------------------------
//        const val NEWBASEURL2 = "http://s.0371.ml/"  //内网环境
//        const val NEWBASEURL = "http://mxss.0371.ml:88/"  //外网测试
        const val NEWBASEURL = "http://mxss.bainianmao.com:88/"//外网测试
//        const val NEWBASEURL = "http://mxss.local.com/" // 测试环境

        val HOME = "/api/index.php?route=api/home"     //首页
        val CART = "/api/index.php?route=api/cart"         //购物车
        val CARTADD = "/api/index.php?route=api/cart/add"  //添加商品
        val CARTDELE = "/api/index.php?route=api/cart/remove"  //删除商品
        val STOCKCHECK = "/api/index.php?route=api/product/product_options"  //校验购物车库存
        val CARTEDIT = "/api/index.php?route=api/cart/edit"  //修改商品数量
        val PRODUCT = "/api/index.php?route=api/product"  //商品详情
        val ADDRESS = "/api/index.php?route=api/address"  //地址详情
        val ADDADDRESS = "/api/index.php?route=api/address/add"  //添加地址
        val EDITADDRESS = "/api/index.php?route=api/address/edit" //修改地址
        val DELEADDRESS = "/api/index.php?route=api/address/delete" //删除地址
        val CHECKOUT = "/api/index.php?route=api/checkout" //提交订单
        val CHECKOUTCONFIRM = "/api/index.php?route=api/checkout/confirm" //确定提交
        val NEWORDER = "/api/index.php?route=api/order" //订单详情
        val NEWHISORDER = "/api/index.php?route=api/order/history" //历史订单
        val NEWCATEGORY = "/api/index.php?route=api/category" //列表信息
        val NEWCATEGORYCON = "/api/index.php?route=api/category/goods" //商品列表
        val ADDRESSZONES = "/api/index.php?route=api/address/zones" //地址列表参数
        val GET_REGIONS = "/api/index.php?route=api/checkout/get_regions" //地址列表参数
        val NEWAEARCH = "/api/index.php?route=api/search" //商品搜索接口
        val CHECKOUTXX = "/api/index.php?route=api/checkout/checkout" //订单列表页面去支付调用

        val AUTHORIZE = "/api/index.php?route=oauth/oauth2/authorize" //获取授权码
        val ACCESS_TOKEN = "/api/index.php?route=oauth/oauth2/token" //获取令牌access_token
        val REFRESH_TOKEN = "/api/index.php?route=oauth/oauth2/refresh_token" //刷新令牌
        val REFRESH_TOKEN2 = "api/index.php?route=oauth/oauth2/refresh_token" //刷新令牌

        //------------------------------------ --------------- end ------------------------------------------------
        //聚合数据Key集
        val IDKey = "8bc635e7345dd2e160df47c7ed386818" //身份证实名信息
        val PhoneKey = "5c0ee202ae8c61f918e1367c7a007348"//手机号归属地查询
        val TeleKey = "d651d007f97138c79288519d84c756d9" //手机话费充值
        val PHONELLKey = "b926b1de052f1cb84db50d62b9c1e1b5" //流量充值 key
        val OilCardKey = "4cc4a1c34ef8a88f7d07b7e1e43e27e4" //油卡充值

        //聚合接口
        const val JUHEAPi = "http://apis.juhe.cn"
        val TELCHECK = "/mobile/get" //手机号归属地查询
        const val JUHEAPi2 = "http://op.juhe.cn"
        val REALNAME = "/idcard/query"//实名验证
        val PNHFSELECT = "/ofpay/mobile/telcheck" //检测手机是否能充值话费
        val MZSELECT = "/ofpay/mobile/telquery" //查询面值
        const val JUHEAPi3 = "http://v.juhe.cn"
        val JCLLTC = "/flow/telcheck"//检测手机号支持的流量套餐

    }
}