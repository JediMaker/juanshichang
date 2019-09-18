package com.example.juanshichang.bean
/**
 * @作者：yzq
 * @创建时间：2019/9/18 15:42
 * @文件作用:  这是提现记录列表的bean类
 */
class TakeOutBean {
    data class TakeOutBeans(
        val `data`: Data,
        val errmsg: String,
        val errno: Int
    )

    data class Data(
        val withdraw_list: List<Withdraw>
    )

    data class Withdraw(
        val account: String,
        val amount: String,
        val create_time: String,
        val is_success: String,
        val order_sn: String,
        val type: String
    )
}