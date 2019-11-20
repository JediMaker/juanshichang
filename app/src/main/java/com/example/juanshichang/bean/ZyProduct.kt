package com.example.juanshichang.bean
/**
 * @作者: yzq
 * @创建日期: 2019/11/19 15:16
 * @文件作用:  自营产品 集合类
 */
class ZyProduct {
    data class ZyProducts(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var attribute_groups: List<Any> = listOf(),
        var captcha: String = "",
        var customer_name: String = "",
        var description: String = "",
        var discounts: List<Any> = listOf(),
        var images: List<Image> = listOf(),
        var manufacturer: String = "",
        var manufacturers: String = "",
        var minimum: String = "",
        var model: String = "",
        var options: List<Option> = listOf(),
        var points: String = "",
        var popup: String = "",
        var price: String = "",
        var product_id: Long = 0,
        var products: List<Any> = listOf(),
        var rating: Int = 0,
        var recurrings: List<Any> = listOf(),
        var review_guest: Boolean = false,
        var review_status: String = "",
        var reviews: String = "",
        var reward: String = "",
        var share: String = "",
        var special: String = "",
        var stock: String = "",
        var tab_review: String = "",
        var tags: List<Any> = listOf(),
        var tax: Boolean = false,
        var thumb: String = ""
    )

    data class Option(
        var name: String = "",
        var option_id: String = "",
        var product_option_id: String = "",
        var product_option_value: List<ProductOptionValue> = listOf(),
        var required: String = "",
        var type: String = "",
        var value: String = ""
    )

    data class ProductOptionValue(
        var image: Any? = null,
        var name: String = "",
        var option_value_id: String = "",
        var price: Boolean = false,
        var price_prefix: String = "",
        var product_option_value_id: String = ""
    )

    data class Image(
        var popup: String = "",
        var thumb: String = ""
    )
}