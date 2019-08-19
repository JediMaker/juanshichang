package com.example.juanshichang.bean

object OrdersBean {
    data class OrdersBeans(
        var `data`: List<Data> = ArrayList(),
        var errmsg: String = "",
        var errno: Int = 0
)

data class Data(
    var create_time: Int = 0,
    var goods_id: Int = 0,
    var goods_name: String = "",
    var goods_price: Int = 0,
    var goods_quantity: Int = 0,
    var goods_thumbnail_url: String = "",
    var group_success_time: Int = 0,
    var modify_at: Int = 0,
    var order_amount: Int = 0,
    var order_sn: String = "",
    var order_status: Int = 0,
    var order_status_desc: String = "",
    var pay_time: Int = 0,
    var promotion_amount: Int = 0,
    var promotion_rate: Int = 0,
    var tag: String = "",
    var verify_time: Int = 0
)
}