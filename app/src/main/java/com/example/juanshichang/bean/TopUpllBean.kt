package com.example.juanshichang.bean

class TopUpllBean {
    data class TopUpllBeans(
        var error_code: Int = 0,
        var reason: String = "",
        var result: List<Result> = listOf()
    )

    data class Result(
        var city: String = "", //全国
        var company: String = "", //中国移动
        var companytype: String = "", //2
        var flows: List<Flow> = listOf(),
        var name: String = "", //中国移动全国流量套餐
        var type: String = ""  //1
    )

    data class Flow(
        var id: String = "",
        var inprice: String = "",
        var p: String = "",
        var v: String = ""
    )
}