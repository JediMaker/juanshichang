package com.example.juanshichang.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ZyAllAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.ZyAllBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_zy_all.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber


/**
 * @作者: yzq
 * @创建日期: 2019/12/14 18:48
 * @文件作用:
 */
class ZyAllActivity : BaseActivity(), View.OnClickListener {
    private var category_id: String? = null
    private var type: String? = null//type 0 热销商品 1 最新商品  2 分类商品
    private var zyAdapter: ZyAllAdapter? = null
    private var page = 1
    private var zyData: List<ZyAllBean.Product>? = null
    override fun getContentView(): Int {
        return R.layout.activity_zy_all
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@ZyAllActivity, R.color.colorPrimary)
        if (intent.hasExtra("type")) {
            if (intent.getStringExtra("type").isNotEmpty()) {//type 0 热销商品 1 最新商品  2 分类商品
                type = intent.getStringExtra("type")
                if (intent.hasExtra("category_id")) {
                    if (intent.getStringExtra("category_id").isNotEmpty()) {//分类页跳转
                        category_id = intent.getStringExtra("category_id")
                    }
                }
            }
        }

        refreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshlayout: RefreshLayout) {
//                refreshlayout.finishRefresh(2000 /*,false*/) //传入false表示刷新失败
                page = 1
                if (type != null) {
                    when (type?.trim()) {
                        "0" -> {// 热销商品
                            reqHostList(page.toString())
                        }
                        "1" -> {//最新商品
                            reqProductNewList(page.toString())
                        }
                        "2" -> {//分类商品
                            reqCateSon(category_id!!, page.toString())
                        }
                    }
                }
            }
        })
        refreshLayout.setOnLoadMoreListener { refreshlayout ->
            page++
            if (type != null) {
                when (type?.trim()) {
                    "0" -> {// 热销商品
                        reqHostList(page.toString())
                    }
                    "1" -> {//最新商品
                        reqProductNewList(page.toString())
                    }
                    "2" -> {//分类商品
                        reqCateSon(category_id!!, page.toString())
                    }
                }
            }
//            refreshlayout.finishLoadMore(2000 /*,false*/) //传入false表示加载失败
        }
        header.setColorSchemeResources(R.color.colorPrimary)
    }

    override fun initData() {
        zyAdapter = ZyAllAdapter()
//        zyAdapter?.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
        zyRecycler.adapter = zyAdapter
        showProgressDialog()
        if (type != null) {
            when (type?.trim()) {
                "0" -> {// 热销商品
                    reqHostList(page.toString())
                }
                "1" -> {//最新商品
                    reqProductNewList(page.toString())
                }
                "2" -> {//分类商品
                    if (category_id != null) {
                        reqCateSon(category_id!!, page.toString())
                    }

                }
            }
        }

        zyRet.setOnClickListener(this)
        zyAdapter?.setOnItemClickListener { adapter, view, position ->
            zyData?.let {
                val intent = Intent(this@ZyAllActivity, ShangPinZyContains::class.java)
                intent.putExtra("product_id", it[position].product_id)
                startActivity(intent)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.zyRet -> {
                finish()
            }
        }
    }

    //商品请求
    //分类商品列表
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
                            if (jsonObj.optString(JsonParser.JSON_CODE)
                                    .equals("10007")
                            ) { //对于无商品的处理
                                zyAdapter?.emptyView = View.inflate(
                                    this@ZyAllActivity,
                                    R.layout.activity_not_null,
                                    null
                                )
                                if (refreshLayout.isLoading)
                                    refreshLayout.finishLoadMoreWithNoMoreData()
                                /*      zyRecycler?.postDelayed(kotlinx.coroutines.Runnable {
                                          myLoading?.dismiss()
                                          finish()
                                      }, 800)*/
                            } else {
                                ToastUtil.showToast(
                                    this@ZyAllActivity,
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
                                zyAdapter?.addData(data.data.products!!)
                            } else {
                                zyAdapter?.setNewData(data.data.products!!)
                            }
                            zyAdapter?.emptyView = View.inflate(
                                this@ZyAllActivity,
                                R.layout.activity_not_null,
                                null
                            )
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "列表详情请求完成")
                    dismissProgressDialog()
                    if (refreshLayout.state.isOpening) {
                        refreshLayout.finishRefresh(true)
                        refreshLayout.finishLoadMore(true)
                    }
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "列表详情请求失败: ${e.toString()}")
                    if (refreshLayout.state.isOpening) {
                        refreshLayout.finishRefresh(false)
                        refreshLayout.finishLoadMore(false)
                    }
                }
            })
    }

    //商品请求
    //最新商品列表
    private fun reqProductNewList(page: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.PRODUCTNEWLIST,
            NewParameter.getNewProductListMap(page),
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
                            if (jsonObj.optString(JsonParser.JSON_CODE)
                                    .equals("10007")
                            ) { //对于无商品的处理
                                zyAdapter?.emptyView = View.inflate(
                                    this@ZyAllActivity,
                                    R.layout.activity_not_null,
                                    null
                                )
                                if (refreshLayout.isLoading)
                                    refreshLayout.finishLoadMoreWithNoMoreData()
                                /*            zyRecycler?.postDelayed(kotlinx.coroutines.Runnable {
                                                myLoading?.dismiss()
                                                finish()
                                            }, 800)*/
                            } else if (jsonObj.optString(JsonParser.JSON_CODE)
                                    .equals("215")
                            ) {
                                if (refreshLayout.isLoading)
                                    refreshLayout.finishLoadMoreWithNoMoreData()
                            } else {
                                ToastUtil.showToast(
                                    this@ZyAllActivity,
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
                                zyAdapter?.addData(data.data.products!!)
                            } else {
                                zyAdapter?.setNewData(data.data.products!!)
                            }
                            zyAdapter?.emptyView = View.inflate(
                                this@ZyAllActivity,
                                R.layout.activity_not_null,
                                null
                            )
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "列表详情请求完成")
                    dismissProgressDialog()
                    if (refreshLayout.state.isOpening) {
                        refreshLayout.finishRefresh(true)
                        refreshLayout.finishLoadMore(true)
                    }
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "列表详情请求失败: ${e.toString()}")
                    if (refreshLayout.state.isOpening) {
                        refreshLayout.finishRefresh(false)
                        refreshLayout.finishLoadMore(false)
                    }
                }
            })
    }

    //商品请求
    //热门商品列表
    private fun reqHostList(page: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.PRODUCTHOTLIST,
            NewParameter.getHotProductListMap(page),
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
                            if (jsonObj.optString(JsonParser.JSON_CODE)
                                    .equals("10007")
                            ) { //对于无商品的处理
                                zyAdapter?.emptyView = View.inflate(
                                    this@ZyAllActivity,
                                    R.layout.activity_not_null,
                                    null
                                )
                                if (refreshLayout.isLoading)
                                    refreshLayout.finishLoadMoreWithNoMoreData()
                                /*   showMyLoadD(QMUITipDialog.Builder.ICON_TYPE_FAIL, "暂无商品", true)
                                   zyRecycler?.postDelayed(kotlinx.coroutines.Runnable {
                                       myLoading?.dismiss()
                                       finish()
                                   }, 800)*/
                            }else if (jsonObj.optString(JsonParser.JSON_CODE)
                                    .equals("215")
                            ) {
                                if (refreshLayout.isLoading)
                                    refreshLayout.finishLoadMoreWithNoMoreData()
                            } else {
                                ToastUtil.showToast(
                                    this@ZyAllActivity,
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
                                zyAdapter?.addData(data.data.products!!)
                            } else {
                                zyAdapter?.setNewData(data.data.products!!)
                            }
                            zyAdapter?.emptyView = View.inflate(
                                this@ZyAllActivity,
                                R.layout.activity_not_null,
                                null
                            )
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "列表详情请求完成")
                    dismissProgressDialog()
                    if (refreshLayout.state.isOpening) {
                        refreshLayout.finishRefresh(true)
                        refreshLayout.finishLoadMore(true)
                    }
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "列表详情请求失败: ${e.toString()}")
                    if (refreshLayout.state.isOpening) {
                        refreshLayout.finishRefresh(false)
                        refreshLayout.finishLoadMore(false)
                    }
                }
            })
    }
}
