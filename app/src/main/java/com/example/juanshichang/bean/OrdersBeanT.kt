package com.example.juanshichang.bean

/**
 * @作者: yzq
 * @创建日期: 2019/12/5 17:22
 * @文件作用: 更新后的订单详情
 */
class OrdersBeanT {
    data class OrdersBeanTs(
        var `data`: List<Data> = listOf(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var date_added: String = "",
        var name: String = "",
        var order_id: String = "",
        var order_num: String = "",
        var products: List<Product> = listOf(),
        var products_total: Int = 0,
        var status: String = "",
        var total: String = ""
    )

    data class Product(
        var image: String = "",
        var name: String = "",
        var options:List<Options> = listOf(),
        var order_product_id: String = "",
        var product_id: String = "",
        var quantity: String = ""
    )
    data class Options(
        var name: String = "",
        var value: String = ""
    )
}