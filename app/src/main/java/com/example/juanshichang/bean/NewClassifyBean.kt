package com.example.juanshichang.bean

/**
 * @作者: yzq
 * @创建日期: 2019/12/7 16:04
 * @文件作用:
 */
class NewClassifyBean {
    data class NewClassifyBeans(
        var `data`: List<Data> = listOf(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var category_id: String = "",
        var column: String = "",
        var date_added: String = "",
        var date_modified: String = "",
        var description: String = "",
        var image: String = "",
        var language_id: String = "",
        var meta_description: String = "",
        var meta_keyword: String = "",
        var meta_title: String = "",
        var name: String = "",
        var parent_id: String = "",
        var sort_order: String = "",
        var status: String = "",
        var store_id: String = "",
        var top: String = ""
    )
}