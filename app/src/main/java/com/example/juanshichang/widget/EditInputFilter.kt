package com.example.juanshichang.widget

import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import com.example.juanshichang.utils.LogTool
import java.util.regex.Pattern

/**
 * @作者：yzq
 * @创建时间：2019/9/18 14:03
 * @文件作用: 这是一个Edit的控制工具 控制输入的小数
 * @参考： https://blog.csdn.net/wuyinlei/article/details/78631043
 *      https://blog.csdn.net/kingsley1212/article/details/90109330
 */
class EditInputFilter : InputFilter{

    companion object{
        private val MAX_VALUE:Int = 9999 //输入的最大金额
        private val POINTER_LENGTH :Int = 2   //小数点后的位数
        private val POINTER:String = "."
        private val ZERO:String = "0"
    }
    private var mPattern:Pattern? = null
    constructor(){
        /**
         * 正则表达式，表示匹配0-9的数字和.
         */
        mPattern = Pattern.compile("([0-9]|\\.)*")
    }
    /**
     * @param source    新输入的字符串
     * @param start     新输入的字符串起始下标，一般为0
     * @param end       新输入的字符串终点下标，一般为source长度-1
     * @param dest      输入之前文本框内容
     * @param dstart    原内容起始坐标，一般为0
     * @param dend      原内容终点坐标，一般为dest长度-1
     * @return          输入内容
     */
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        val sourceText = source.toString() //新输入的字符串
        val destText  = dest.toString()   //输入之前文本框内容
        //验证删除等按键
        if(TextUtils.isEmpty(sourceText)){
            return ""
        }
        val matcher = mPattern?.matcher(source)
        //已经输入小数点的情况下，只能输入数字
        if(dest!!.contains(POINTER)){
            if(!matcher!!.matches()){
                return ""
            }else{
                if(POINTER.equals(source.toString())){ //只能输入一个小数点 新输入 返回""
                    return ""
                }
            }
            /**
             *   验证小数点精度，保证小数点后只能输入{@link POINTER_LENGTH}位
             */
            val index = destText.indexOf(POINTER)
            val length = dend - index
            if(length > POINTER_LENGTH){
                return dest.subSequence(dstart, dend)
            }
        }else{
            /**
             * 没有输入小数点的情况下，只能输入小数点和数字
             * 1. 首位不能输入小数点
             * 2. 如果首位输入0，则接下来只能输入小数点了
             */
            if (!matcher!!.matches()) {
                return ""
            } else {
                if ((POINTER.equals(source.toString())) && TextUtils.isEmpty(destText)) {  //首位不能输入小数点
                    return ""
                } else if (!POINTER.equals(source.toString()) && ZERO.equals(destText)) { //如果首位输入0，接下来只能输入小数点
                    return ""
                }
            }
        }
        var sumText:Double? = 0.0

//            sumText =  (destText.toDouble() + sourceText.toDouble())
        //用上面那一句 会爆 空指针... 需要判断
        if(destText == "" || destText.isEmpty()){ //输入之前文本框内容 为空
            if(sourceText == "" || sourceText.isEmpty()){//新输入的字符串 为空
                sumText =0.0
            }else{
                sumText = sourceText.toDouble()
            }
        }else{
            if(sourceText == "" || sourceText.isEmpty()){
                sumText = destText.toDouble()
            }else{
                sumText = (destText+sourceText).toDouble()
            }
        }
        LogTool.e("sumText","sumText ： $sumText  str: $destText+$sourceText")
        /**
         * 这里判断最大输入的值
         */
        if (sumText > MAX_VALUE) {
            return dest.subSequence(dstart, dend)
        }
        LogTool.e("sumText2","$sumText")
        LogTool.e("sumText3","${dest.subSequence(dstart, dend)}$sourceText")
        return "${dest.subSequence(dstart, dend)}$sourceText"
}


}