package com.example.juanshichang.http;


import android.widget.Toast;
import com.example.juanshichang.MyApp;
import rx.Subscriber;

/**
 * Created by Administrator on 2018/7/25.
 */

public  class MainSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ApiError){
            Toast.makeText(MyApp.applicationContext,((ApiError) e).getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNext(T t) {

    }


}
