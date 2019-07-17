package com.example.juanshichang.http;


import com.example.juanshichang.base.Api;
import com.example.juanshichang.bean.MainBean;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/7/23.
 */

public class HttpManagerByGson{


    private static final int READ_TIME_OUT = 5;
    private static final int CONNECT_TIME_OUT = 5;

    private ApiService mApiService;
    private static HttpManagerByGson instanse = null;
    public HttpManagerByGson() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT);//打印retrofit日志
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build();
    Retrofit retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            //网络请求的域名
            .baseUrl(Api.BASEURL)
            .build();
    mApiService = retrofit.create(ApiService.class);

    }

    //
    public static HttpManagerByGson getInstance() {
        if (instanse == null) {
            synchronized (HttpManager.class) {
                if (instanse == null) {
                    return new HttpManagerByGson();
                }
            }
        }
        return instanse;
    }


    /*private void callByGson(Observable<MainBean<String>> observable, Subscriber<String> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void postByGson(String url, Map<String, String> json, Subscriber<String> subscriber){
        Observable<MainBean<String>> observable=mApiService.postByGson(url,json);
        callByGson(observable,subscriber);
    }

    *//**
     * 带错误码 错误信息
     * @param observable
     * @param subscriber
     *//*
    private void callMainHasCode(Observable<MainBean<String>> observable, MainSubscriber<String> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getMainHasCode(String url, Map<String, String> json, MainSubscriber<String> subscriber){
        Observable<MainBean<String>> observable=mApiService.postByMain(url,json);
        callMain(observable,subscriber);
    }


    *//**
     * 主页数据
     * @param observable
     * @param subscriber
     *//*
    private void callHomeData(Observable<MainBean<HomeBean>> observable, Subscriber<HomeBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getHomeData(String url, Map<String, String> json, Subscriber<HomeBean> subscriber){
        Observable<MainBean<HomeBean>> observable=mApiService.getHomeData(url,json);
        callHomeData(observable,subscriber);
    }

    *//**
     * 商品详情
     * @param observable
     * @param subscriber
     *//*
    private void callGoodsDetail(Observable<MainBean<GoodsDetailBean>> observable, Subscriber<GoodsDetailBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getGoodsDetail(String url, Map<String, String> json, Subscriber<GoodsDetailBean> subscriber){
        Observable<MainBean<GoodsDetailBean>> observable=mApiService.getGoodsDetail(url,json);
        callGoodsDetail(observable,subscriber);
    }


    *//**
     * 商品规格
     * @param observable
     * @param subscriber
     *//*
    private void callGoodsGuige(Observable<String> observable, Subscriber<String> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getGoodsGuige(String url, Map<String, String> json, Subscriber<String> subscriber){
        Observable<String> observable=mApiService.getGoodsSpecifications(url,json);
        callGoodsGuige(observable,subscriber);
    }



    private void callMain(Observable<MainBean<String>> observable, Subscriber<String> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getMain(String url, Map<String, String> json, Subscriber<String> subscriber){
        Observable<MainBean<String>> observable=mApiService.postByMain(url,json);
        callMain(observable,subscriber);
    }

    private void callMainByCode(Observable<MainBean<String>> observable, MainSubscriber<String> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getMainBycode(String url, Map<String, String> json, MainSubscriber<String> subscriber){
        Observable<MainBean<String>> observable=mApiService.postByMain(url,json);
        callMainByCode(observable,subscriber);
    }
    private void callCollect(Observable<CollectBean> observable, Subscriber<CollectBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getCollect(String url, Map<String, String> json, Subscriber<CollectBean> subscriber){
        Observable<CollectBean> observable=mApiService.getCollect(url,json);
        callCollect(observable,subscriber);
    }

    *//**
     * 评论
     * @param observable
     * @param subscriber
     *//*
    private void callCommentList(Observable<MainBean<List<CommentBean>>> observable, Subscriber<List<CommentBean>> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getCommentList(String url, Map<String, String> json, Subscriber<List<CommentBean>> subscriber){
        Observable<MainBean<List<CommentBean>>> observable=mApiService.getCommentList(url,json);
        callCommentList(observable,subscriber);
    }

    *//**
     * 提交订单
     * @param observable
     * @param subscriber
     *//*

    private void callOrderCommit(Observable<MainBean<OrderCommitBean>> observable, Subscriber<OrderCommitBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getOrderCommit(String url, Map<String, String> json, Subscriber<OrderCommitBean> subscriber){
        Observable<MainBean<OrderCommitBean>> observable=mApiService.getOrderCommit(url,json);
        callOrderCommit(observable,subscriber);
    }

    *//**
     * 默认地址
     * @param observable
     * @param subscriber
     *//*
    private void callDefaultAddress(Observable<MainBean<RecaddressBean>> observable, Subscriber<RecaddressBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getDefaultAddress(String url, Map<String, String> json, Subscriber<RecaddressBean> subscriber){
        Observable<MainBean<RecaddressBean>> observable=mApiService.getRecaddressBean(url,json);
        callDefaultAddress(observable,subscriber);
    }

    *//**
     * 支付
     * @param observable
     * @param subscriber
     *//*
    private void callPay(Observable<MainBean<PayBean>> observable, Subscriber<MainBean<PayBean>> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getPay(String url, Map<String, String> json, Subscriber<MainBean<PayBean>> subscriber){
        Observable<MainBean<PayBean>> observable=mApiService.getPay(url,json);
        callPay(observable,subscriber);
    }
    *//**
     * 获取支付参数
     * @param observable
     * @param subscriber
     *//*
    private void callPayData(Observable<MainBean<PayUrlBean>> observable, MainSubscriber<PayUrlBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }
    *//**
     * 获取支付参数 微信
     * @param observable
     * @param subscriber
     *//*
    private void callWPayData(Observable<MainBean<WXpayBean>> observable, MainSubscriber<WXpayBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }
    //支付宝
    public void getPayData(String url, Map<String, String> json, MainSubscriber<PayUrlBean> subscriber){
        Observable<MainBean<PayUrlBean>> observable=mApiService.getPayData(url,json);
        callPayData(observable,subscriber);
    }
    //微信
    public void getWPayData(String url, Map<String, String> json, MainSubscriber<WXpayBean> subscriber){
        Observable<MainBean<WXpayBean>> observable=mApiService.getWPayData(url,json);
        callWPayData(observable,subscriber);
    }
    *//**
     * 获取支付参数
     * @param observable
     * @param subscriber
     *//*
    private void callPayDetail(Observable<MainBean<PayDetailBean>> observable, MainSubscriber<PayDetailBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getPayDetail(String url, Map<String, String> json, MainSubscriber<PayDetailBean> subscriber){
        Observable<MainBean<PayDetailBean>> observable=mApiService.getPayDetail(url,json);
        callPayDetail(observable,subscriber);
    }

    *//**
     * 订单列表
     * @param observable
     * @param subscriber
     *//*
    private void callOrderList(Observable<MainBean<List<OrderBean>>> observable, MainSubscriber<List<OrderBean>> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getOrderList(String url, Map<String, String> json, MainSubscriber<List<OrderBean>> subscriber){
        Observable<MainBean<List<OrderBean>>> observable=mApiService.getOrderList(url,json);
        callOrderList(observable,subscriber);
    }
    private void callOrderDetail(Observable<MainBean<OrderDetailBean>> observable, MainSubscriber<OrderDetailBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getOrderDetail(String url, Map<String, String> json, MainSubscriber<OrderDetailBean> subscriber){
        Observable<MainBean<OrderDetailBean>> observable=mApiService.getOrderDetail(url,json);
        callOrderDetail(observable,subscriber);
    }
    private void callExpress(Observable<MainBean<ExpressBean>> observable, MainSubscriber<ExpressBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getExpress(String url, Map<String, String> json, MainSubscriber<ExpressBean> subscriber){
        Observable<MainBean<ExpressBean>> observable=mApiService.getExpress(url,json);
        callExpress(observable,subscriber);
    }
    private void callCartList(Observable<MainBean<CartListBean>> observable, MainSubscriber<CartListBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getCartList(String url, Map<String, String> json, MainSubscriber<CartListBean> subscriber){
        Observable<MainBean<CartListBean>> observable=mApiService.getCartList(url,json);
        callCartList(observable,subscriber);
    }
    private void callCommentGoodsSelect(Observable<MainBean<List<SelectCommentGoodsBean>>> observable, MainSubscriber<List<SelectCommentGoodsBean>> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getCommentGoodsSelect(String url, Map<String, String> json, MainSubscriber<List<SelectCommentGoodsBean>> subscriber){
        Observable<MainBean<List<SelectCommentGoodsBean>>> observable=mApiService.getCommentGoods(url,json);
        callCommentGoodsSelect(observable,subscriber);
    }
    private void callGoodsList2(Observable<MainBean<GoodsClassListBean>> observable, Subscriber<GoodsClassListBean> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new MainFunc1())
                .subscribe(subscriber);
    }

    public void getGoodsList2(String url, Map<String, String> json, Subscriber<GoodsClassListBean> subscriber){
        Observable<MainBean<GoodsClassListBean>> observable=mApiService.getGoodsList2(url,json);
        callGoodsList2(observable,subscriber);
    }*/



}
