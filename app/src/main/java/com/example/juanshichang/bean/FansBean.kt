package com.example.juanshichang.bean

class FansBean {
    data class FansBeans(
        var `data`: List<Data> = ArrayList(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var avatar: String = "",
        var join_time: Int = 0,
        var mobile: String = "",
        var nick_name: String = ""
    )
}