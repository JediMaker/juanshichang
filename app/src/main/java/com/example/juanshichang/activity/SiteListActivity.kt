package com.example.juanshichang.activity

import android.content.Intent
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.example.juanshichang.R
import com.example.juanshichang.adapter.SiteListAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.SiteBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
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
    private var siteAdapter:SiteListAdapter? = null
    private var adds:List<SiteBean.Addresse>? = null
    override fun getContentView(): Int {
        return R.layout.activity_site_list
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@SiteListActivity, R.color.white)
        siteAdapter = SiteListAdapter()
        siteList.adapter = siteAdapter
    }

    override fun initData() {
        siteRet.setOnClickListener(this)
        addNewSite.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        getSites()
        siteAdapter?.setOnItemChildClickListener(object : BaseQuickAdapter.OnItemChildClickListener{
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                when(view?.id){
                    R.id.siteEdit ->{ //编辑按钮
                        adds?.let {
                            val intent = Intent(this@SiteListActivity,EditSiteActivity::class.java)
                            intent.putExtra("type",2)
//                            intent.putExtra("address_id",it[position].address_id)
                            intent.putExtra("data",it[position]) //传输序列化内容
                            startActivity(intent)
                        }
                    }
                }
            }
        })
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
                            val data:SiteBean.SiteBeans = Gson().fromJson(t,SiteBean.SiteBeans::class.java)
                            adds = data.data.addresses
                            siteAdapter?.setNewData(adds)
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
