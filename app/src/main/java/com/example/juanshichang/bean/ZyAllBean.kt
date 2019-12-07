package com.example.juanshichang.bean

class ZyAllBean {
    data class ZyAllBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var products: List<Product> = listOf()
    )

    data class Product(
        var minimum: String = "",
        var name: String = "",
        var price: String = "",
        var product_id: String = "",
        var rating: Int = 0,
        var thumb: String = ""
    )
}