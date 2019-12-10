package com.example.juanshichang.bean

/**
 * @作者: yzq
 * @创建日期: 2019/12/5 12:53
 * @文件作用: 订单提交成功bean
 */
class SettleAccBean {
    data class SettleAccBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var alipay_order_info: String = "",
        var order_id: Int = 0,
        var products: List<Product> = listOf(),
        var total: Double = 0.0,
        var vouchers: List<Any> = listOf()
    )

    data class Product(
        var cart_id: String = "",
        var href: String = "",
        var model: String = "",
        var name: String = "",
        var option: List<Option> = listOf(),
        var price: String = "",
        var product_id: String = "",
        var quantity: String = "",
        var recurring: String = "",
        var subtract: String = "",
        var total: String = ""
    )

    data class Option(
        var name: String = "",
        var value: String = ""
    )
}