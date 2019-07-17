package com.example.juanshichang.http;


import com.alibaba.fastjson.JSONObject;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import rx.Observable;

import java.util.List;
import java.util.Map;


/**
 * Created by ws on 2017/3/21 0021.
 */

public interface ApiService {



    @GET()
    rx.Observable<String> get(@Url String url, @QueryMap Map<String, String> params);

    @GET()
    rx.Observable<String> get(@Url String url);

    @FormUrlEncoded
    @POST()
    rx.Observable<String> post(@Url String url, @FieldMap Map<String, String> params);

    @POST()
    rx.Observable<String> post(@Url String url);

    //    @Multipart
    //    @POST("upload")
    //    Call<ResponseBody> upload(@Part("description") RequestBody description,
    //                              @Part MultipartBody.Part file);

    @Multipart
    @POST()
    rx.Observable<String> upload(@Url String url, @PartMap Map<String, String> params, @Part MultipartBody.Part file);

    @Streaming
    @GET()
    Call<ResponseBody> download(@Url String url);


    rx.Observable<String> post(String url, JSONObject json);

    /*@FormUrlEncoded
    @POST()
    Observable<MainBean<String>> postByGson(@Url String url, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST()
    Observable<MainBean<String>> postByMain(@Url String url, @FieldMap Map<String, String> params);


    *//**
     * 首页数据
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<HomeBean>> getHomeData(@Url String url, @FieldMap Map<String, String> params);

    *//**
     * 商品详情
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<GoodsDetailBean>> getGoodsDetail(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 商品规格
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<String> getGoodsSpecifications(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 评价列表
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<List<CommentBean>>> getCommentList(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 订单确认
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<OrderCommitBean>> getOrderCommit(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 默认地址
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<RecaddressBean>> getRecaddressBean(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 提交订单
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<PayBean>> getPay(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 支付数据
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<PayUrlBean>> getPayData(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 微信
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<WXpayBean>> getWPayData(@Url String url, @FieldMap Map<String, String> params);

    *//** 支付数据
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<PayDetailBean>> getPayDetail(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 获取订单列表
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<List<OrderBean>>> getOrderList(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 获取订单列表
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<OrderDetailBean>> getOrderDetail(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 物流信息
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<ExpressBean>> getExpress(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 购物车列表
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<CartListBean>> getCartList(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 选择商品评论
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<List<SelectCommentGoodsBean>>> getCommentGoods(@Url String url, @FieldMap Map<String, String> params);

    *//**
     * 3.4.1商品列表（分页、分类名称、顶级分类ID）
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<MainBean<GoodsClassListBean>> getGoodsList2(@Url String url, @FieldMap Map<String, String> params);
    *//**
     * 3.4.1商品列表（分页、分类名称、顶级分类ID）
     * @param url
     * @param params
     * @return
     *//*
    @FormUrlEncoded
    @POST()
    Observable<CollectBean> getCollect(@Url String url, @FieldMap Map<String, String> params);
*/
}
