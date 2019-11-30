package com.example.juanshichang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_site_list.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者：yzq
 * @创建时间：2019/11/13 15:50
 * @文件作用: 收货地址 详情页面
 */
class SiteListActivity : BaseActivity(),View.OnClickListener{
    override fun getContentView(): Int {
        return R.layout.activity_site_list
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@SiteListActivity, R.color.white)

    }

    override fun initData() {
        siteRet.setOnClickListener(this)
        addNewSite.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        getSites()
    }
    override fun onClick(v: View?) {
        when(v){
            siteRet ->{
                finish()
            }
            addNewSite->{
                val intent = Intent(this@SiteListActivity,EditSiteActivity::class.java)
                intent.putExtra("type",1)
                startActivity(intent)
            }
        }
    }
    //获取地址列表
    private fun getSites() {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.ADDRESS,
            NewParameter.getBaseMap(),
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
                    LogTool.e("onCompleted", "地址列表请求完成")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "地址列表请求失败: ${e.toString()}")
                    this@SiteListActivity.runOnUiThread {
                        finish()
                    }
                }
            })
    }
}
