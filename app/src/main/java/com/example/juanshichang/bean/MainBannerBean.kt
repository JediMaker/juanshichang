package com.example.juanshichang.bean

data class MainBannerBean(
    var `data`: Data = Data(),
    var errmsg: String = "",
    var errno: Int = 0
)

data class Data(
    var theme_list_get_response: ThemeListGetResponse = ThemeListGetResponse()
)

data class ThemeListGetResponse(
    var request_id: String = "",
    var theme_list: List<Theme> = listOf(),
    var total: Int = 0
)

data class Theme(
    var goods_num: Int = 0,
    var id: Int = 0,
    var image_url: String = "",
    var name: String = "",
    var type: Int = 0
)