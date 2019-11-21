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
        var model: String = "",  //类型
        var name: String = "",  //名称
        var option: List<Any> = listOf(),
        var price: String = "",  //单价
        var quantity: String = "", //数量
        var recurring: String = "",
        var reward: String = "",
        var stock: Boolean = false,  //是否 有货 默认 false
        var thumb: String = "",   //商品图片
        var total: String = "",  //总价
        var isSelect:Boolean = false  //是否选中 默认 false
    )
}