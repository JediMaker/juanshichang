package com.example.juanshichang.http;

/**
 * Created by Administrator on 2018/7/25.
 */


public class ApiError extends RuntimeException {

    String code;
    String msg;
    public ApiError(String code, String msg){
        super(msg);
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}



