package com.example.juanshichang.http

import android.text.TextUtils
import android.util.Log
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.TokenBean
import com.example.juanshichang.http.HttpCode.TOKEN_ERROR
import com.example.juanshichang.utils.SpUtil
import com.google.gson.Gson
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
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
                Gson().fromJson(t, TokenBean.TokenBean::class.java)
            if (baseResponseBean != null && baseResponseBean.err_code != null) {
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

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
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
            val data = Gson().fromJson(t, TokenBean.TokenBean::class.java)
            if (data.data.error != null && data.data.token_type == null) {//refreshToken失效,
                //获取code授权码
                val clientCode = OkHttpClient()
                val requestBuilder = Request.Builder()
                val urlBuilder = (Api.NEWBASEURL + Api.AUTHORIZE2).toHttpUrlOrNull()?.newBuilder()
                urlBuilder?.addQueryParameter("client_id", CLIENT_ID)
                urlBuilder?.addQueryParameter("response_type", CODE)
                urlBuilder?.addQueryParameter("redirect_uri", REDIRECT_URI)
                requestBuilder.url(urlBuilder?.build()!!).get().build();
                val callCode = clientCode.newCall(requestBuilder.build())
                val responseCode = callCode.execute()
                val sourceCode = responseCode!!.body?.source()
                sourceCode?.request(Long.MAX_VALUE) // Buffer the entire body.
                val bufferCode = sourceCode?.buffer()
                val UTF8Code = Charset.forName("UTF-8")
                val stringCode = bufferCode?.clone()?.readString(UTF8Code)
                val tCode = stringCode?.substring(stringCode?.indexOf("{"), stringCode.length)
                val dataCode = Gson().fromJson(tCode, TokenBean.TokenBean::class.java)
                if (dataCode.data.code != null) {
                    //获取access_token
                    val client_access_token = OkHttpClient()
                    val access_token_parameterMap = NewParameter.getAuthorizeTokenMap(
                        CLIENT_ID,
                        dataCode.data.code.toString(),
                        CLIENT_SECRET,
                        REDIRECT_URI
                    )
                    /*     val jsonObj = Gson().toJson(parameterMap).toString()
                         val requestBody: RequestBody = RequestBody.create(json, parameterMap.toString())*/
                    val access_token_builder = FormBody.Builder()
                    for (key in access_token_parameterMap.keys) {
                        //追加表单信息
                        access_token_builder.add(key, access_token_parameterMap[key]!!)
                    }
                    val access_token_formBody: RequestBody = access_token_builder.build()
                    val access_token_request: Request =
                        Request.Builder().url(Api.NEWBASEURL + Api.ACCESS_TOKEN2)
                            .post(access_token_formBody)
                            .build();
                    val access_token_call = client_access_token.newCall(access_token_request)
                    val access_token_response = access_token_call.execute()
                    val access_token_source = access_token_response!!.body?.source()
                    access_token_source?.request(Long.MAX_VALUE) // Buffer the entire body.
                    val access_token_buffer = access_token_source?.buffer()
                    val access_token_UTF8 = Charset.forName("UTF-8")
                    val access_token_string =
                        access_token_buffer?.clone()?.readString(access_token_UTF8)
                    val access_token_t = access_token_string?.substring(
                        access_token_string?.indexOf("{"),
                        access_token_string.length
                    )
                    val access_token_data =
                        Gson().fromJson(access_token_t, TokenBean.TokenBean::class.java)
                    val user = SpUtil.getIstance().user
                    newToken = access_token_data.data.access_token
                    user.apply {
                        access_token = access_token_data?.data?.access_token!!
                        expires_in = access_token_data?.data?.expires_in!!
                        token_type = access_token_data?.data?.token_type!!
                        refresh_token = access_token_data?.data?.refresh_token!!
                    }.let {
                        SpUtil.getIstance().user = it //写入
                    }
                }
            } else {
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
            }

        } catch (e: Exception) {
            newToken = ""
        }
        return newToken
    }
}
