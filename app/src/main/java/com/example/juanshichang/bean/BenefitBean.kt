package com.example.juanshichang.bean

class BenefitBean {
    data class BenefitBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var balance: Long = 0,
        var current_day_order_paid: Int = 0,
        var current_day_other: Int = 0,
        var current_day_pre_benefit: Int = 0,
        var current_month_pre_benefit: Int = 0,
        var current_month_other:Int = 0,
        var last_day_order_paid: Int = 0,
        var last_day_other: Int = 0,
        var last_day_pre_benefit: Int = 0,
        var last_month_benefit: Int = 0
    )
}