package com.example.juanshichang.bean

/**
 * @作者: yzq
 * @创建日期: 2019/12/4 19:32
 * @文件作用: 提交订单bean
 */
class ConOrderBean {
    data class ConOrderBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var addresses: List<Addresse> = listOf(),
        var products: List<Product> = listOf(),
        var default_address_id: String = "0",
        var total: Double = 0.0
    )

    data class Addresse(
        var address_detail: String = "",
        var address_id: String = "",
        var iphone: String = "",
        var province: String = "",
        var city: String = "",
        var county: String = "",
        var province_id: String = "",
        var city_id: String = "",
        var county_id: String = "",
        var firstname: String = "",
        var lastname: String = "",
        var zone: String = ""
    )

    data class Product(
        var cart_id: String = "",
        var href: String = "",
        var model: String = "",
        var name: String = "",
        var option: List<Option> = ArrayList(),
        var price: String = "",
        var quantity: String = "",
        var recurring: String = "",
        var reward: String = "",
        var stock: Boolean = false,
        var thumb: String = "",
        var total: String = ""
    )
    data class Option(
        var name: String = "",
        var value: String = ""
    )

}