package com.example.juanshichang.http;

import com.alibaba.fastjson.JSONObject;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
/**
 * @作者：yzq
 * @创建时间：2019/11/6 11:16
 * @文件作用:  这个类用于加载自定义url
 */
public class JhApiHttpManager {
    private static final int READ_TIME_OUT = 5;
    private static final int CONNECT_TIME_OUT = 5; //5

    private ApiService mApiService;

    private JhApiHttpManager(String url) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT);//打印retrofit日志
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(5,TimeUnit.SECONDS)  //new add
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //网络请求的域名
                .baseUrl(url)
                .build();
        mApiService = retrofit.create(ApiService.class);
    }

    private static JhApiHttpManager instanse = null;

    private static String BaseUrl = "";
    public static JhApiHttpManager getInstance(String url) {
        if (instanse == null || BaseUrl.equals(url)) {
            synchronized (JhApiHttpManager.class) {
                if (instanse == null) {
                    BaseUrl = url;
                    return new JhApiHttpManager(url);
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

    public void post(String url, JSONObject json, Subscriber<String> subscriber){
        Observable <String> observable=mApiService.post(url,json);
        call(observable,subscriber);
    }
}
