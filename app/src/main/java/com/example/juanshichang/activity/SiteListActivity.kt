package com.example.juanshichang.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import com.example.juanshichang.utils.ToastTool
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
class SiteListActivity : BaseActivity(), View.OnClickListener {
    private var siteAdapter: SiteListAdapter? = null
    private var adds: List<SiteBean.Addresse>? = null
    private var defId:String? = "" //默认地址id
    private var checkLocal: String? = null
    override fun getContentView(): Int {
        return R.layout.activity_site_list
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@SiteListActivity, R.color.colorPrimary)
        checkLocal = intent.getStringExtra("checkLocal") //用于标记 提交订单 选择地址
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
        siteAdapter?.setOnItemChildClickListener(object :
            BaseQuickAdapter.OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                when (view?.id) {
                    R.id.siteEdit -> { //编辑按钮
                        adds?.let {
                            val intent = Intent(this@SiteListActivity, EditSiteActivity::class.java)
                            intent.putExtra("type", 2)
//                            intent.putExtra("address_id",it[position].address_id)
                            intent.putExtra("data", it[position]) //传输序列化内容
                            intent.putExtra("defid",defId)
                            startActivity(intent)
//                            ToastTool.showToast(this@SiteListActivity, "点击编辑")
                        }
                    }
                    R.id.allCon -> { //跳转选择收货地址
                        if (!TextUtils.isEmpty(checkLocal)) {
                            adds?.let {
                                val intent: Intent = Intent()
                                val bund = Bundle()
                                bund.putParcelable("data", it[position])
                                intent.putExtra("bundle", bund)
                                setResult(2, intent)
                                finish()
                            }
//                            ToastTool.showToast(this@SiteListActivity, "点击返回")
                        }
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            siteRet -> {
                finish()
            }
            addNewSite -> {
                val intent = Intent(this@SiteListActivity, EditSiteActivity::class.java)
                intent.putExtra("type", 1)
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
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val t =result?.substring(result?.indexOf("{"),result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                            ToastUtil.showToast(
                                this@SiteListActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val data: SiteBean.SiteBeans =
                                Gson().fromJson(t, SiteBean.SiteBeans::class.java)
                            defId = data.data.default_address_id
                            adds = data.data.addresses
                            siteAdapter?.setMyData(adds,defId)
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "地址列表请求完成")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "地址列表请求失败: ${e.toString()}")
                    //todo  接口数据为空 就有  com.google.gson.JsonSyntaxException 爆出....
                    siteAdapter?.emptyView = View.inflate(
                        this@SiteListActivity,
                        R.layout.activity_not_null,
                        null
                    )
                }
            })
    }
}
