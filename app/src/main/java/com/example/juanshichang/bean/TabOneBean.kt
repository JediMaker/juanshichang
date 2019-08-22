package com.example.juanshichang.bean

class TabOneBean {
        data class TabOneBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
        )

    data class Data(
        var category_list: List<Category> = listOf()
    )

    data class Category(
        var category_id: Int = 0,
        var image: String = "",
        var name: String = ""
    )
}