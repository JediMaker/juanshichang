package com.example.juanshichang.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

class GridItemBean : MultiItemEntity {

    data class GridItemBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var channel_list: List<Channel> = listOf()
    )

    data class Channel(
        var channel_id: Long = 0,
        var image_url: String = "",
        var order: Int = 0,
        var type: String = ""
    )
    override fun getItemType(): Int {
        return HomeEntity.TYPE_GRID
    }
}