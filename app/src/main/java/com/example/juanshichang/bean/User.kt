package com.example.juanshichang.bean

import com.raizlabs.android.dbflow.sql.language.Condition
import java.io.Serializable

class User : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }

    //    public var userid: String? = null
//    public var level: String? = null//用户等级
//    public var usertype: String? = null//是否开通 商户
//    public var realname: String? = null
    var usertoken: String? = null  //token
    var useruid: Long? = 0 //uid

    //    public var facepicurl: String? = null
//    public var paypassword: String? = null
//    private var userimaccid: String? = null // IM 用户id
//    private var userimtoken: String? = null // IM 用户token
//    var userage: String? = null //用户年龄
    var avatar: String? = null   //用户头像
    var nick_name: String? = null //昵称
    var phone_num: String? = null //电话
    var password: String? = null //密码
    var date_added: String? = null//注册时间
    var balance: Float = 0f //余额
    var points: Int = 0//积分
    var current_day_benefit: Float = 0f//今日预估
    var current_month_benefit: Float = 0f //本月预估
    var last_day_benefit: Float = 0f //昨天收益
    var from_invite_userid: Long? = 0 //邀请用户id
    var invite_code: String? = null  //邀请码
    var ali_pay_account: String? = null  //用户提现账户

    //新增授权模式
    var access_token: String? = null  //access_token
    var expires_in: Int? = 604800  //access_token 有效期
    var token_type: String? = "Bearer"  //token_type
    var refresh_token: String = ""  //refresh_token

}