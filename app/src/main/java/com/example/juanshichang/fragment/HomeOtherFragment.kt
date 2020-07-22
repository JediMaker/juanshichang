package com.example.juanshichang.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.example.juanshichang.R
import com.example.juanshichang.activity.ShangPinZyContains
import com.example.juanshichang.adapter.ZyAllAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseFragment
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.ZyAllBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.widget.LiveDataBus
import com.google.gson.Gson
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_zy_all.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

class HomeOtherFragment : BaseFragment() {
    private var FathPage: String = ""
    private var zyRc: RecyclerView? = null
    private var zyAd: ZyAllAdapter? = null
    private var header: MaterialHeader? = null
    private var refreshLayout: SmartRefreshLayout? = null
    private var page = 1
    private var zyData: List<ZyAllBean.Product>? = null
    override fun getLayoutId(): Int {
        return R.layout.fragment_home_other
    }

    override fun initViews(savedInstanceState: Bundle) {
        zyRc = mBaseView?.findViewById(R.id.zyRe)
        zyAd = ZyAllAdapter()
        zyRc?.adapter = zyAd
        refreshLayout = mBaseView?.findViewById(R.id.refreshLayout)
        header = mBaseView?.findViewById(R.id.header)
        refreshLayout?.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshlayout: RefreshLayout) {
                page = 1
                reqCateSon(FathPage, page.toString())//网络请求
            }
        })
        refreshLayout?.setOnLoadMoreListener { refreshlayout ->
            page++
            reqCateSon(FathPage, page.toString())//网络请求
        }
        header?.setColorSchemeResources(R.color.colorPrimary)
    }


    override fun initData() {
        LiveDataBus.get()
            .with("main_tab", String::class.java)
            .observe(this, object : Observer<String> {
                override fun onChanged(t: String?) {
                    LogTool.e("yyyyyyy", "监听到了消息:" + t)
                    page = 1
                    refreshLayout?.setNoMoreData(false)
                    if (!t?.contentEquals(FathPage)!!) { //判断是否重复点击同一条目
                        FathPage = t
                        mContext?.showProgressDialog()
                        reqCateSon(FathPage, page.toString())//网络请求
                    }
                }
            })

        zyAd?.setOnItemClickListener { adapter, view, position ->
            zyData?.let {
                val intent = Intent(mContext!!, ShangPinZyContains::class.java)
                intent.putExtra("product_id", it[position].product_id)
                startActivity(intent)
            }
        }
    }

    //商品列表
    private fun reqCateSon(category_id: String, page: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.NEWCATEGORYCON,
            NewParameter.getNewCGoodMap(category_id, page),
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
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                            if (jsonObj.optString(JsonParser.JSON_CODE).equals("10007")) { //
                                zyAd?.emptyView = View.inflate(
                                    mContext,
                                    R.layout.activity_not_null,
                                    null
                                )
                                if (page.toInt() == 1) {
                                    zyAd?.setNewData(null)
                                }
                                if (refreshLayout?.isLoading!!)
                                    refreshLayout?.finishLoadMoreWithNoMoreData()
                            } else {
                                ToastUtil.showToast(
                                    mContext!!,
                                    jsonObj.optString(JsonParser.JSON_MSG)
                                )
                            }

                        } else {
                            val data = Gson().fromJson(t, ZyAllBean.ZyAllBeans::class.java)
                            if (page.toInt() == 1) {
                                zyData = data.data.products
                            } else {
                                zyData = zyData?.plus(data.data.products)
                            }
                            if (page.toInt() > 1) {
                                zyAd?.addData(data.data.products!!)
                            } else {
                                zyAd?.setNewData(data.data.products!!)
                            }
                            zyAd?.emptyView = View.inflate(
                                mContext,
                                R.layout.activity_not_null,
                                null
                            )
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "列表详情请求完成")
                    mContext?.dismissProgressDialog()
                    if (refreshLayout?.state?.isOpening!!) {
                        refreshLayout?.finishRefresh(true)
                        refreshLayout?.finishLoadMore(true)
                    }
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "列表详情请求失败: ${e.toString()}")
                    mContext?.dismissProgressDialog()
                    if (refreshLayout?.state?.isOpening!!) {
                        refreshLayout?.finishRefresh(false)
                        refreshLayout?.finishLoadMore(false)
                    }
                }
            })
    }
}
