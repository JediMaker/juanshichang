package com.example.juanshichang.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

class MainBannerBean: MultiItemEntity {
    data class MainBannerBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var banner_list: List<Banner> = listOf()  //banner_list
    )

    data class Banner(
        var banner_id: Int = 0,
        var image_url: String = "",
        var order: Int = 0,
        var type: String = ""
    )
    override fun getItemType(): Int {
        return HomeEntity.TYPE_BANNER
    }
}
