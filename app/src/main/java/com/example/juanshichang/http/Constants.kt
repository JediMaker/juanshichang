package com.example.juanshichang.http

const val BEARER = "Bearer"
const val HTTP_HEADER_AUTH = "Authorization"
const val RESP_SUCCESS = 0
const val CODE = "code"
const val LIMIT_SIZE = "10"//搜索商品分页单页展示数据数量
const val OrderASC = "ASC"//搜索商品分页单页展示数据数量
const val OrderDESC = "DESC"//搜索商品分页单页展示数据数量
/*const val CLIENT_SECRET = "mxss_test"
const val REDIRECT_URI = "mxss.local.com"
const val CLIENT_ID = "mxss_test"//测试环境*/
//生产环境
const val CLIENT_ID = "mxsh_master"
const val CLIENT_SECRET = "mxsh_master"
const val REDIRECT_URI = "mxss.bainianmao.com"

//协议信息地址
const val REGISTRATION_AGREEMENT_URL =  "http://mxss.bainianmao.com/doc/zhuce.html"//注册协议
const val SERVICES_AGREEMENT_URL =      "http://mxss.bainianmao.com/doc/tiaokuan.html"//用户服务协议
const val PRIVACY_AGREEMENT_URL =       "http://mxss.bainianmao.com/doc/yinsi.html"//用户隐私协议
object HttpCode {
    //token过期
    const val TOKEN_ERROR = 203.0
    //refreshtoken过期
    const val REFRESH_TOKEN_ERROR = 401
}
