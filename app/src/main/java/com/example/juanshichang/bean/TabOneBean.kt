package com.example.juanshichang.bean

class TabOneBean {
    data class TabOneBeans(
        val `data`: Data,
        val errmsg: String,
        val errno: Int
    )

    data class Data(
        val category_list: List<Category>,
        val category_name: String
    )

    data class Category(
        val category_id: Int,
        val image: String,
        val name: String
    )
}