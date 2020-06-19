package com.example.juanshichang.utils

import java.math.BigDecimal


/**
 * 加、减、乘、除 高精度计算工具类
 * @author yzq 20190725
 * */
object UtilsBigDecimal {

    // 需要精确至小数点后几位
    const val DECIMAL_POINT_NUMBER: Int = 2

    // 加法运算
    @JvmStatic
    fun add(d1: Double, d2: Double): Double =
        BigDecimal(d1.toString()).add(BigDecimal(d2.toString())).setScale(DECIMAL_POINT_NUMBER, BigDecimal.ROUND_DOWN)
            .toDouble()

    // 减法运算
    @JvmStatic
    fun sub(d1: Double, d2: Double): Double =
        BigDecimal(d1.toString()).subtract(BigDecimal(d2.toString()))
            .setScale(DECIMAL_POINT_NUMBER, BigDecimal.ROUND_DOWN).toDouble()

    // 乘法运算
    @JvmStatic
    fun mul(d1: Double, d2: Double, decimalPoint: Int): Double =
        BigDecimal(d1.toString()).multiply(BigDecimal(d2.toString())).setScale(decimalPoint, BigDecimal.ROUND_DOWN)
            .toDouble()

    // 乘法运算
    @JvmStatic
    fun mul2(d1: BigDecimal, d2: BigDecimal, decimalPoint: Int): Double =
        d1.multiply(d2).setScale(decimalPoint, BigDecimal.ROUND_DOWN).toDouble()

    // 除法运算
    @JvmStatic
    fun div(d1: Double, d2: Double, decimalPoint: Int): Double =
        BigDecimal(d1.toString()).divide(BigDecimal(d2.toString())).setScale(decimalPoint, BigDecimal.ROUND_DOWN)
            .toDouble()
    /**
     * setScale(2)  //表示保留2位小数，默认是四舍五入方式

    setScale(2, BigDecimal.ROUND_DOWN)  //删除多余的小数位，例如：2.125 → 2.12

    setScale(2, BigDecimal.ROUND_UP)  //进位处理，例如：2.125 → 2.13

    setScale(2, BigDecimal.ROUND_HALF_UP)  //四舍五入，例如：2.125 → 2.13
     */
}
