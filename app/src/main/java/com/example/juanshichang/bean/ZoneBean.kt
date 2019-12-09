package com.example.juanshichang.bean

/**
 * @作者: yzq
 * @创建日期: 2019/12/9 11:55
 * @文件作用:
 */
class ZoneBean {
    data class ZoneBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var address_format: String = "",
        var country_id: String = "",
        var iso_code_2: String = "",
        var iso_code_3: String = "",
        var name: String = "",
        var postcode_required: String = "",
        var status: String = "",
        var zone: List<Zone> = listOf()
    )

    data class Zone(
        var code: String = "",
        var country_id: String = "",
        var name: String = "",
        var status: String = "",
        var zone_id: String = ""
    )
}