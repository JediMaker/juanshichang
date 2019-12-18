package com.example.juanshichang.bean

class OrderDetailBean {
    data class OrderDetailBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var comment: String = "",
        var date_added: String = "",
        var error_warning: String = "",
        var histories: List<History> = listOf(),
        var invoice_no: String = "",
        var order_id: String = "",
        var payment_address: String = "",
        var payment_method: String = "",
        var products: List<Product> = listOf(),
        var shipping_address: ShippingAddress = ShippingAddress(),
        var shipping_method: String = "",
        var success: String = "",
        var totals: List<Total> = listOf(),
        var vouchers: List<Any> = listOf()
    )

    data class History(
        var comment: String = "",
        var date_added: String = "",
        var status: String = ""
    )

    data class Product(
        var image: String = "",
        var model: String = "",
        var name: String = "",
        var option: List<Option> = listOf(),
        var price: String = "",
        var product_id: String = "",
        var quantity: String = "",
        var reorder: String = "",
        var `return`: String = "",
        var total: String = ""
    )

    data class Option(
        var name: String = "",
        var value: String = ""
    )

    data class ShippingAddress(
        var address_1: String = "",
        var address_2: String = "",
        var city: String = "",
        var company: String = "",
        var country: String = "",
        var name: String = "",
        var postcode: String = "",
        var telephone: String = "",
        var zone: String = ""
    )

    data class Total(
        var text: String = "",
        var title: String = ""
    )
}