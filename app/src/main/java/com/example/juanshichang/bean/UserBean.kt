package com.example.juanshichang.bean

class UserBean {
    data class UserBeans(
    var `data`: Data = Data(),
    var errmsg: String = "",
    var errno: Int = 0
    )

    data class Data(
        var avatar: String = "",
        var balance: Float = 0f,
        var current_day_benefit: Float = 0f,
        var current_month_benefit: Float = 0f,
        var from_invite_userid: Int = 0,
        var invite_code: String = "",
        var last_day_benefit: Float = 0f,
        var nick_name: String = "",
        var date_added: String = "",
        var email: String = "",
        var ali_pay_account: String = ""
    )
}