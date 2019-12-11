package com.example.juanshichang.bean

import android.os.Parcel
import android.os.Parcelable


/**
 * @作者: yzq
 * @创建日期: 2019/12/2 15:12
 * @文件作用:地址类
 */
class SiteBean() {
    data class SiteBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var addresses: List<Addresse> = listOf(),
        var default_address_id: String = ""
    )

    data class Addresse(
        var address_detail: String = "",
        var address_id: String = "",
        var city: String = "",
        var iphone: String = "",
        var name: String = "",
        var zone: String = ""

    ) : Parcelable {
        constructor(source: Parcel) : this(
            source.readString().toString(),
            source.readString().toString(),
            source.readString().toString(),
            source.readString().toString(),
            source.readString().toString(),
            source.readString().toString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(address_detail)
            writeString(address_id)
            writeString(city)
            writeString(iphone)
            writeString(name)
            writeString(zone)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Addresse> = object : Parcelable.Creator<Addresse> {
                override fun createFromParcel(source: Parcel): Addresse = Addresse(source)
                override fun newArray(size: Int): Array<Addresse?> = arrayOfNulls(size)
            }
        }
    }

}