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
//    var userage: String? = null //用户年龄
    var avatar: String? = null   //用户头像
    var nick_name:String? = null //昵称
    var balance:Float = 0f //余额
    var current_day_benefit:Float = 0f//今日预估
    var current_month_benefit:Float = 0f //本月预估
    var last_day_benefit:Float = 0f //昨天收益
    var from_invite_userid:Long? = 0 //邀请用户id
    var invite_code:String? = null  //邀请码


}