package com.example.juanshichang.bean

class CartBean {
    data class CartBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var products: List<Product> = listOf(),
        var totals: List<Total> = listOf()
    )

    data class Total(
        var text: String = "",
        var title: String = ""
    )

    data class Product(
        var cart_id: String = "",
        var href: String = "",
        var model: String = "",
        var name: String = "",
        var option: List<Any> = listOf(),
        var price: String = "",
        var quantity: String = "",
        var recurring: String = "",
        var reward: String = "",
        var stock: Boolean = false,
        var thumb: String = "",
        var total: String = ""
    )
}