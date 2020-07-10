package com.example.juanshichang.bean

import java.io.Serializable
/**
 * @作者: yzq
 * @创建日期: 2019/12/4 18:34
 * @文件作用: 购物车bean 序列化数据
 */
class CartBean : Serializable {
    data class CartBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var products: List<Product> = listOf(),
        var total: Total = Total(),
        var totals: List<TotalX> = listOf()
    )

    data class Product(
        var cart_id: String = "",
        var product_id: String = "",
        var href: String = "",
        var model: String = "",  //类型
        var minTotals: Int = 10,  //最小库存数量
        var name: String = "",  //名称
        var option: List<Option> = ArrayList(),
        var price: String = "",  //单价
        var quantity: String = "", //数量
        var recurring: String = "",
        var reward: String = "",
        var stock: Boolean = false,  //是否 有货 默认 false
        var thumb: String = "",   //商品图片
        var total: String = "",  //总价
        var isSelect: Boolean = false  //是否选中 默认 false
    )

    data class Option(
        var name: String = "",
        var value: String = ""
    )

    data class Total(
        var code: String = "",
        var sort_order: String = "",
        var title: String = "",
        var value: Double = 0.0
    )

    data class TotalX(
        var text: String = "",
        var title: String = ""
    )
}