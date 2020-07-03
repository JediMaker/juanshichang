package com.example.juanshichang.http

import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSON
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.Api.Companion.BASEURL
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.TokenBean
import com.example.juanshichang.http.HttpCode.REFRESH_TOKEN_ERROR
import com.example.juanshichang.http.HttpCode.TOKEN_ERROR
import com.example.juanshichang.utils.SpUtil
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber
import java.io.IOException
import java.nio.charset.Charset


class TokenInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)
        try {
            val responseBody = response.body
            //解决response.body().string();只能打印一次
            val source = responseBody!!.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer()
            val UTF8 = Charset.forName("UTF-8")
            val string = buffer.clone().readString(UTF8)
            Log.e("responseBody", string)
            val t = string?.substring(string?.indexOf("{"), string.length)
            val baseResponseBean =
                Gson().fromJson(t, TokenBean::class.java)
            if (baseResponseBean != null) {
                if (baseResponseBean.err_code?.equals(TOKEN_ERROR)) {
                    //token过期
                    //根据RefreshToken同步请求，获取最新的Token
                    val newToken = refreshToken()!!
                    if (!TextUtils.isEmpty(newToken)) {
                        //使用新的Token，创建新的请求
                        val newRequest = chain.request()
                            .newBuilder()
                            .header(
                                HTTP_HEADER_AUTH,
                                "$BEARER ${SpUtil.getIstance().user.access_token}"
                            )
                            .build()
                        //重新请求
                        return chain.proceed(newRequest)
                    }

                } else if (baseResponseBean.err_code == REFRESH_TOKEN_ERROR) {
//                    refreshToken过期,
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    /**
     * 同步请求方式，根据RefreshToken获取最新的Token
     *
     * @return
     */
    private fun getNewToken(): String {
        var newToken = "null"
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.REFRESH_TOKEN,
            NewParameter.getRefreshTokenMap(
                CLIENT_ID,
                SpUtil.getIstance().user.refresh_token.toString(),
                CLIENT_SECRET
            ),
            object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val t = result?.substring(result?.indexOf("{"), result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            //获取到令牌信息写入缓存
                            val data = Gson().fromJson(t, TokenBean::class.java)
                            val user = SpUtil.getIstance().user
                            newToken = data.data.access_token
                            user.apply {
                                access_token = data?.data?.access_token!!
                                expires_in = data?.data?.expires_in!!
                                token_type = data?.data?.token_type!!
                                refresh_token = data?.data?.refresh_token!!
                            }.let {
                                SpUtil.getIstance().user = it //写入
                            }
                        } else { //令牌信息获取失败
                            newToken = "null"
                        }
                    }
                }

                override fun onCompleted() {
                }

                override fun onError(e: Throwable?) {
                }
            })
        return newToken
    }

    private fun refreshToken(): String? {
        var newToken: String? = null
//        var json: MediaType = "application/x-www-form-urlencoded;charset=utf-8".toMediaTypeOrNull()!!
        Log.e("OkHttpManager", "重新请求---");
        val client = OkHttpClient()
        val parameterMap = NewParameter.getRefreshTokenMap(
            CLIENT_ID,
            SpUtil.getIstance().user.refresh_token.toString(),
            CLIENT_SECRET
        )
        /*     val jsonObj = Gson().toJson(parameterMap).toString()
             val requestBody: RequestBody = RequestBody.create(json, parameterMap.toString())*/
        val builder = FormBody.Builder()
        for (key in parameterMap.keys) {
            //追加表单信息
            builder.add(key, parameterMap[key]!!)
        }
        val formBody: RequestBody = builder.build()
        val request: Request =
            Request.Builder().url(Api.NEWBASEURL + Api.REFRESH_TOKEN2).post(formBody).build();
        val call = client.newCall(request)
        val response = call.execute()
        //获取到令牌信息写入缓存
        try {
            //解决response.body().string();只能打印一次
            val source = response!!.body?.source()
            source?.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source?.buffer()
            val UTF8 = Charset.forName("UTF-8")
            val string = buffer?.clone()?.readString(UTF8)
            val t = string?.substring(string?.indexOf("{"), string.length)
            val data = Gson().fromJson(t, TokenBean::class.java)
            val user = SpUtil.getIstance().user
            newToken = data.data.access_token
            user.apply {
                access_token = data?.data?.access_token!!
                expires_in = data?.data?.expires_in!!
                token_type = data?.data?.token_type!!
                refresh_token = data?.data?.refresh_token!!
            }.let {
                SpUtil.getIstance().user = it //写入
            }
        } catch (e: Exception) {
            newToken = ""
        }
        return newToken
    }
/*    private String refreshToken() {
        String newtoken = null;
        */
    /**
     * 必须使用同步请求
     * https://blog.csdn.net/u010312474/java/article/details/103620710
     *//*
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        Log.e("OkHttpManager", "重新请求---" + url);

        OkHttpClient client = new OkHttpClient();
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", getToken());
        map.put("refreshToken", getRefreshToken());

        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder().url(url).post(requestBody).build();
        okhttp3.Call call = client.newCall(request);
        try {
            Response response = call.execute();

//            FLog.i("HttpLog", "refreshToken response=" + response.body().string());//只能有效调用一次,不能打印

            CredentialDTO credentialDTO = new Gson().fromJson(response.body().string(), CredentialDTO.class);
            if (null != credentialDTO.accessToken && null != credentialDTO.refreshToken) {
                newtoken = credentialDTO.accessToken;
                TokenManager.getInstance().setAccessToken(credentialDTO.accessToken);
                TokenManager.getInstance().setRefreshToken(credentialDTO.refreshToken);
                FLog.i("HttpLog", "refreshToken accessToken==" + credentialDTO.accessToken);
            } else {
                newtoken = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            newtoken = "";
        }

        return newtoken;
    }
   */
/*
    private val getNewToken: String
        private get() {
//        通过一个特定的接口获取新的token，此处要用到同步的retrofit请求
            var newToken = ""
            //取出本地的refreshToken
            val refreshToken = SpUtil.getIstance().user.refresh_token
//            Request request = new Request.Builder().url(AppConstant.TOKEN_REFRESH).post(body).build();
//            Response newResponse = new OkHttpClient().newCall(request).execute();
//把参数传进Map中

            //把参数传进Map中
            val paramsMap: HashMap<String, String> = NewParameter.getRefreshTokenMap(
                CLIENT_ID,
                SpUtil.getIstance().user.refresh_token.toString(),
                CLIENT_SECRET
            )
            val builder = FormBody.Builder()
            for (key in paramsMap.keys) {
                //追加表单信息
                builder.add(key, paramsMap[key]!!)
            }
            val formBody: RequestBody = builder.build()

            *//*        val call = mApiService?.requestToken(
                        Api.REFRESH_TOKEN,
                        NewParameter.getRefreshTokenMap(
                            CLIENT_ID,
                            refreshToken.toString(),
                            CLIENT_SECRET
                        )
                    )*//*
            val client = OkHttpClient()
            val request = Request.Builder().post(formBody)
                .url(Api.BASEURL)
                .build()

            val response = client.newCall(request)
            var call = client.newCall(request)


            val responseBody = call?.execute()?.body
            //打印response.body().string()
            val source = responseBody!!.toString()
            Log.e("getRefreshNewToken", source)
            *//*if (responseBody.status) {//刷新令牌成功
                val user = SpUtil.getIstance().user
                user.apply {
                    access_token = responseBody?.data?.access_token
                    expires_in = responseBody?.data?.expires_in
                    token_type = responseBody?.data?.token_type
                    refresh_token = responseBody?.data?.refresh_token
                }.let {
                    SpUtil.getIstance().user = it //写入
                }
                newToken = responseBody?.data?.access_token
            } else {

            }*//*
            return newToken
        }*/
}
