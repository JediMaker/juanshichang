package com.example.juanshichang.http

const val BEARER = "Bearer"
const val HTTP_HEADER_AUTH = "Authorization"
const val RESP_SUCCESS = 0
const val CODE = "code"
//const val CLIENT_SECRET = "mxss_test"
//const val REDIRECT_URI = "mxss.local.com"
//const val CLIENT_ID = "mxss_test"//测试环境
//生产环境
const val CLIENT_ID = "mxsh_master"
const val CLIENT_SECRET = "mxsh_master"
const val REDIRECT_URI = "mxss.bainianmao.com"

object HttpCode {
    //token过期
    const val TOKEN_ERROR = 203.0
    //refreshtoken过期
    const val REFRESH_TOKEN_ERROR = 401
}
