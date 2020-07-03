package com.example.juanshichang.base

import com.alibaba.fastjson.JSONObject

class JsonParser {
    /**
     * errcode：@int32,接口状态，0为成功，其他为错误编码
     *  errmsg：@string,系统返回提示信息
     *   data：@obj,JSON数据包，根据不同接口定义
     */
    companion object {
        val JSON_SUCCESS = "0"
        val JSON_FAILED = ""
        val JSON_CODE = "errno"
        val JSON_MSG = "errmsg"
        val JSON_DATA = "data"
        val JSON_Status = "status"
        /**
         * 校验是否是json格式的数据
         *
         * @param jsonString
         * @return
         */
        fun isValidJsonWithSimpleJudge(json: String): Boolean {
            try {
                val js: JSONObject = JSONObject.parseObject(json)
                return true
            }catch (e:Exception){
                return  false
            }
        }
    }

}