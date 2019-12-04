package com.example.juanshichang.bean

import java.io.Serializable

/**
 * @作者: yzq
 * @创建日期: 2019/12/2 15:12
 * @文件作用:地址类
 */
class SiteBean : Serializable{
    data class SiteBeas(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var addresses: List<Addresse> = listOf()
    )

    data class Addresse(
        var address_detail: String = "",
        var address_id: String = "",
        var city: String = "",
        var zone: String = ""
    )
}