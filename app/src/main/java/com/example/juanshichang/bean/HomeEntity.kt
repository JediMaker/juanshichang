package com.example.juanshichang.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

class HomeEntity : MultiItemEntity {
    private var itemType: Int? = Int.MAX_VALUE
    constructor()
    constructor(itemType: Int) {
        this.itemType = itemType
    }

    companion object {
        const val TYPE_BANNER: Int = 1  //banner
        const val TYPE_GRID: Int = 2     //grid
        const val TYPE_RECYCLER: Int = 3 //recycler
    }

    var banners: MutableList<MainBannerBean.Banner>? = mutableListOf() //banner 数据源
    var grids: MutableList<GridItemBean.Channel>? = mutableListOf() //grid 数据源
    var recyclers: MutableList<MainRecyclerBean.Theme>? = mutableListOf() //recycler 数据源

    override fun getItemType(): Int {
        return this.itemType!!
    }

}