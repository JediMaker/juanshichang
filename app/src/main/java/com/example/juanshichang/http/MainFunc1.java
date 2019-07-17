package com.example.juanshichang.http;

import android.util.Log;
import com.example.juanshichang.bean.MainBean;
import rx.functions.Func1;


/**
 * Created by Administrator on 2018/7/24.
 */

public class MainFunc1 <T> implements Func1<MainBean<T>,T> {


    @Override
    public T call(MainBean<T> mainBean) {
        Log.i("okhttp", "call: "+mainBean.toString());
        if("1".equals(mainBean.getCode())){

            if("".equals(mainBean.getData())&&mainBean.getData()==null){
                return null;
            }else{
                    T data=(T) mainBean.getData();
                    return data;
            }

        }else{
            throw new ApiError(mainBean.getCode(),mainBean.getMsg());
        }
    }
}
