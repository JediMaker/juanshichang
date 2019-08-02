package com.example.juanshichang.bean

data class MainBannerBean(
    var `data`: Data = Data(),
    var errmsg: String = "",
    var errno: Int = 0
)

data class Data(
    var banner_list: List<Banner> = listOf()
)

data class Banner(
    var banner_id: Int = 0,
    var image_url: String = "",
    var order: Int = 0,
    var type: String = ""
)