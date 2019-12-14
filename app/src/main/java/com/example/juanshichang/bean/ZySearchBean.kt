package com.example.juanshichang.bean

class ZySearchBean {
    data class ZySearchBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var categories: List<Category> = listOf(),
        var products: List<Product> = listOf()
    )

    data class Category(
        var category_id: String = "",
        var children: List<Children> = listOf(),
        var name: String = ""
    )

    data class Children(
        var category_id: String = "",
        var children: List<Any> = listOf(),
        var name: String = ""
    )

    data class Product(
        var description: String = "",
        var href: String = "",
        var minimum: String = "",
        var name: String = "",
        var price: String = "",
        var product_id: String = "",
        var rating: Int = 0,
        var special: Boolean = false,
        var tax: Boolean = false,
        var thumb: String = ""
    )
}