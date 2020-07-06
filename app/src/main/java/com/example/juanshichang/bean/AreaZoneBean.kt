package com.example.juanshichang.bean
class AreaZoneBean{
    data class AreaZoneBean(
        val `data`: List<AreaData> = listOf(),
        val err_code: Any = Any(),
        val err_msg: Any = Any(),
        val status: Boolean = false
    )

    data class AreaData(
        val id: String = "",
        val name: String = "",
        val pid: String = "",
        val pinyin: String = ""
    )
}
