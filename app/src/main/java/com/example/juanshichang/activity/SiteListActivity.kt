package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者：yzq
 * @创建时间：2019/11/13 15:50
 * @文件作用: 收货地址 详情页面
 */
class SiteListActivity : BaseActivity(){
    override fun getContentView(): Int {
        return R.layout.activity_site_list
    }

    override fun initView() {

    }

    override fun initData() {

    }

    //获取地址列表
    private fun getSites(productId: Long) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.ADDRESS,
            NewParameter.getProductMap(productId),
            object : Subscriber<String>() {
                override fun onNext(t: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optString(JsonParser.JSON_CODE)!!.equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(
                                this@SiteListActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {

                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "Zy商品详情请求完成")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "Zy商品详情请求失败: ${e.toString()}")
                    this@SiteListActivity.runOnUiThread {
                        finish()
                    }
                }
            })
    }
}
