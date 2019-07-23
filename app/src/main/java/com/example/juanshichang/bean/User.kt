package com.example.juanshichang.bean

import java.io.Serializable

class User : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }

    //    public var userid: String? = null
//    public var level: String? = null//用户等级
//    public var usertype: String? = null//是否开通 商户
//    public var realname: String? = null
    public var usertoken: String? = null  //token
    //    public var facepicurl: String? = null
//    public var paypassword: String? = null
//    private var userimaccid: String? = null // IM 用户id
//    private var userimtoken: String? = null // IM 用户token
    var userage: String? = null //用户年龄
    var useravatar: String? = null   //用户头像
    var username: String? = null   //用户昵称
}