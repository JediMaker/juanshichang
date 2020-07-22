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
        var thumb: String = "",
        var description: String = "",
        var href: String = "",
        var special: Boolean = false,
        var tax: Boolean = false
    )
    data class ZyAllBeans2(
        var `data`: Data2 = Data2(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data2(
        var products: List<HomeBean.Date> = listOf()
    )


}