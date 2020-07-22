package com.example.juanshichang.bean

class HomeBean {
    data class HomeBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0,
        var status:Boolean =false
    )

    data class Data(
        var categroy: List<Categroy> = listOf(),
        var products: List<Product> = listOf(),
        var slideshow: List<Slideshow> = listOf(),
        var items: List<Items> = listOf()
    )

    data class Categroy(
        var category_id: String = "",
        var column: String = "",
        var date_added: String = "",
        var date_modified: String = "",
        var description: String = "",
        var image: String = "",
        var language_id: String = "",
        var meta_description: String = "",
        var meta_keyword: String = "",
        var meta_title: String = "",
        var name: String = "",
        var parent_id: String = "",
        var sort_order: String = "",
        var status: String = "",
        var store_id: String = "",
        var top: String = ""
    )

    data class Product(
        var date: List<Date> = ArrayList(),
        var name: String = ""
    )

    data class Date(
        var minimum: String = "",
        var description: String = "",
        var href: String = "",
        var name: String = "",
        var price: String = "",
        var product_id: String = "",
        var rating: Int = 0,
        var special: Boolean = false,
        var tax: Boolean = false,
        var thumb: String = ""
    )

    data class Slideshow(
        var image: String = "",
        var link: String = "",
        var title: String = "",
        var type: String = "",
        var value: String = ""
    )
    data class Items(
        var id: String = "",
        var icon: String = "",
        var name: String = "",
        var uri: String = ""
    )
}