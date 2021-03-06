package com.example.juanshichang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ZySearchAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.TopUpllBean
import com.example.juanshichang.bean.ZySearchBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.http.LIMIT_SIZE
import com.example.juanshichang.http.OrderDESC
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastTool
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_seek_bar.*
import kotlinx.android.synthetic.main.activity_topup.*
import kotlinx.android.synthetic.main.activity_zy_all.*
import kotlinx.android.synthetic.main.activity_zy_search.*
import kotlinx.android.synthetic.main.activity_zy_search.refreshLayout
import org.json.JSONObject
import rx.Subscriber

class ZySearchActivity : BaseActivity(), View.OnClickListener {
    private var allData: ZySearchBean.ZySearchBeans? = null
    private var listData: ArrayList<ZySearchBean.Product>? = null
    private var keySearch: String = ""
    private var page = 1
    private var sAdapter: ZySearchAdapter? = null
    override fun getContentView(): Int {
        return R.layout.activity_zy_search
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this, R.color.colorPrimary)
        if (null != getIntent().getStringExtra("search")) {
            keySearch = getIntent().getStringExtra("search")
            getSearchData(keySearch, page.toString(), LIMIT_SIZE, OrderDESC)
            etsearch.setText(keySearch)
//            getSearchData(keySearch)
        } else {
            ToastUtil.showToast(this@ZySearchActivity, "??????  ?????????!!!")
            finish()
        }
        refreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshlayout: RefreshLayout) {
//                refreshlayout.finishRefresh(2000 /*,false*/) //??????false??????????????????
                page = 1
                getSearchData(keySearch, page.toString(), LIMIT_SIZE, OrderDESC)
            }
        })
        refreshLayout.setOnLoadMoreListener { refreshlayout ->
            page++
            getSearchData(keySearch, page.toString(), LIMIT_SIZE, OrderDESC)
//            refreshlayout.finishLoadMore(2000 /*,false*/) //??????false??????????????????
        }
        headerSearch.setColorSchemeResources(R.color.colorPrimary)
    }

    override fun initData() {
        if (!TextUtils.isEmpty(keySearch)) {
            sAdapter = ZySearchAdapter()
            sAdapter?.openLoadAnimation()
            zySRec.adapter = sAdapter
        }
        mbackLayout.setOnClickListener(this)
        etsearch.setOnClickListener(this)
        mSearchBt.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        sAdapter?.setOnItemClickListener { adapter, view, position ->
            listData?.let {
                val intent = Intent(this@ZySearchActivity, ShangPinZyContains::class.java)
                intent.putExtra("product_id", it[position].product_id)
                startActivity(intent)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            mbackLayout -> {//??????
                finish()
            }
            etsearch -> {//?????????
//                ToastUtil.showToast(this, "")
            }
            mSearchBt -> {//????????????
                val str = getEditText()
                if (!TextUtils.isEmpty(str) && !TextUtils.equals(str, keySearch)) {
                    keySearch = str
//                    getSearchData(keySearch)
                    getSearchData(keySearch, page.toString(), LIMIT_SIZE, OrderDESC)
                } else {
//                    ToastTool.showToast(this@ZySearchActivity, "?????????????????????")
                }

            }
        }
    }

    private fun getEditText(): String {//??????Edit??????
        val text = etsearch.text.toString().trim()
        if (text.length > 0 && !TextUtils.isEmpty(text)) {
            return text
        }
        return ""
    }

    //??????????????????
    private fun getSearchData(search: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL)
            .post(Api.NEWAEARCH, NewParameter.getSearchMap(search), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo???????????????????????????????????????????????????
                    val str = result?.substring(result?.indexOf("{"), result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        val jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                            ToastUtil.showToast(
                                this@ZySearchActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            allData = Gson().fromJson(str, ZySearchBean.ZySearchBeans::class.java)
                            allData?.let {
                                listData = it.data.products as ArrayList
                                sAdapter?.setNewData(listData)
                                sAdapter?.emptyView = View.inflate(
                                    this@ZySearchActivity,
                                    R.layout.activity_not_null,
                                    null
                                )
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "?????????????????? ??????")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "?????????????????? Error")
                }
            })
    }

    //??????????????????
    private fun getSearchData(
        search: String,
        page: String,
        limit: String,
        order: String

    ) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL)
            .post(
                Api.NEWAEARCH,
                NewParameter.getSearchMap(search, page, limit, order),
                object : Subscriber<String>() {
                    override fun onNext(result: String?) {
                        //todo???????????????????????????????????????????????????
                        val str = result?.substring(result?.indexOf("{"), result.length)
                        if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                            val jsonObj: JSONObject = JSONObject(str)
                            if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                                if (jsonObj.optString(JsonParser.JSON_CODE)
                                        .equals("10007")
                                ) { //????????????????????????
                                    sAdapter?.emptyView = View.inflate(
                                        this@ZySearchActivity,
                                        R.layout.activity_not_null,
                                        null
                                    )
                                    if (refreshLayout.isLoading)
                                        refreshLayout.finishLoadMoreWithNoMoreData()
                                    /*            zyRecycler?.postDelayed(kotlinx.coroutines.Runnable {
                                                    myLoading?.dismiss()
                                                    finish()
                                                }, 800)*/
                                } else {
                                    ToastUtil.showToast(
                                        this@ZySearchActivity,
                                        jsonObj.optString(JsonParser.JSON_MSG)
                                    )
                                }
                            } else {
                                allData =
                                    Gson().fromJson(str, ZySearchBean.ZySearchBeans::class.java)
                                allData?.let {
                                    if (it.data.products != null && it.data.products.size > 0) {
                                        if (page.toInt() == 1) {
                                            listData = it.data.products as ArrayList
                                        } else {
                                            listData = listData?.plus(it.data.products) as ArrayList
                                        }
                                    }

                                    if (page.toInt() > 1) {
                                        if (it.data.products != null && it.data.products.size > 0) {
                                            sAdapter?.addData(it.data.products!!)
                                        } else {
                                            if (refreshLayout.isLoading)
                                                refreshLayout.finishLoadMoreWithNoMoreData()
                                        }
                                    } else {
                                        sAdapter?.setNewData(listData)
                                    }
//                                    sAdapter?.setNewData(listData)
                                    sAdapter?.emptyView = View.inflate(
                                        this@ZySearchActivity,
                                        R.layout.activity_not_null,
                                        null
                                    )
                                }
                            }
                        }
                    }

                    override fun onCompleted() {
                        if (refreshLayout.state.isOpening) {
                            refreshLayout.finishRefresh(true)
                            refreshLayout.finishLoadMore(true)
                        }
                    }

                    override fun onError(e: Throwable?) {
                        if (refreshLayout.state.isOpening) {
                            refreshLayout.finishRefresh(false)
                            refreshLayout.finishLoadMore(false)
                        }
                    }
                })
    }

}
