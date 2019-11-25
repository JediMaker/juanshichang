package com.example.juanshichang.bean

/**
 * @作者: yzq
 * @创建日期: 2019/11/25 20:04
 * @文件作用: 解决HashMap键值不可重复问题
 */
class AKey {
    var  head :String? = ""
    var  center:String? = ""
    var  behind:String? = ""
    override fun hashCode(): Int {
        return head.hashCode()+center.hashCode()+behind.hashCode()
    }

    override fun toString(): String {
        return "$head$center$behind"
    }
}