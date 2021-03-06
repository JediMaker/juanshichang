package com.example.juanshichang.http;


import com.alibaba.fastjson.JSONObject;
import com.example.juanshichang.base.Api;
import com.example.juanshichang.utils.SpUtil;

import org.jetbrains.annotations.NotNull;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @作者：yzq
 * @创建时间：2019/11/6 11:16
 * @文件作用: 用于加载本地接口
 */

public class HttpManager {


    private static final int READ_TIME_OUT = 5;
    private static final int CONNECT_TIME_OUT = 5; //5

    private ApiService mApiService;

    private HttpManager() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT);//打印retrofit日志
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        TokenInterceptor tokenInterceptor = new TokenInterceptor();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)  //new add
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {//  https://www.jianshu.com/p/32612f7e6e41//为请求添加Header
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header(ConstantsKt.HTTP_HEADER_AUTH,
                                        ConstantsKt.BEARER + " " + SpUtil.getIstance().getUser().getAccess_token())
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(tokenInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //网络请求的域名
                .baseUrl(Api.BASEURL)
                .build();
        mApiService = retrofit.create(ApiService.class);
    }

    private static HttpManager instanse = null;

    public static HttpManager getInstance() {
        if (instanse == null) {
            synchronized (HttpManager.class) {
                if (instanse == null) {
                    return new HttpManager();
                }
            }
        }
        return instanse;
    }

    public void get(String url, Map<String, String> params, Subscriber<String> subscriber) {
        Observable<String> observable = mApiService.get(url, params);
        call(observable, subscriber);
    }

    public void get(String url, Subscriber<String> subscriber) {
        Observable<String> observable = mApiService.get(url);
        call(observable, subscriber);
    }

    public void post(String url, Map<String, String> params, Subscriber<String> subscriber) {
        Observable<String> observable = mApiService.post(url, params);
        call(observable, subscriber);
    }

    public void post(String url, Subscriber<String> subscriber) {
        Observable<String> observable = mApiService.post(url);
        call(observable, subscriber);
    }

    public void upload(String url, Map<String, String> params, MultipartBody.Part file, Subscriber<String> subscriber) {
        Observable<String> observable = mApiService.upload(url, params, file);
        call(observable, subscriber);
    }

    private void call(Observable<String> observable, Subscriber<String> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void post(String url, JSONObject json, Subscriber<String> subscriber) {
        Observable<String> observable = mApiService.post(url, json);
        call(observable, subscriber);
    }

}
