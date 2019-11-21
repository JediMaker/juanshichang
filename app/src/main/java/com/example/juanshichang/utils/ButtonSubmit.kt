package com.example.juanshichang.utils

/**
 * @作者: yzq
 * @创建日期: 2019/11/21 14:52
 * @文件作用: 处理按钮 避免频繁点击操作
 */
class ButtonSubmit {
    companion object {
        private var lastClickTime: Long = 0
        fun isFastDoubleClick(): Boolean {
            val time = System.currentTimeMillis()
            val timeD = time - lastClickTime
            if (0 < timeD && timeD < 500) {  //500毫秒内按钮无效，这样可以控制快速点击，自己调整频率
                return true
            }
            lastClickTime = time
            return false
        }
    }
}