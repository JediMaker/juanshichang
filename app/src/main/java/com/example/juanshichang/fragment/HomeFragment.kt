package com.example.juanshichang.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.MarginPageTransformer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.activity.*
import com.example.juanshichang.adapter.ImageBannerNetAdapter
import com.example.juanshichang.adapter.MainGridAdapter
import com.example.juanshichang.adapter.NewHomeAdapter
import com.example.juanshichang.adapter.ZyAllAdapter
import com.example.juanshichang.base.*
import com.example.juanshichang.bean.GridItemBean
import com.example.juanshichang.bean.HomeBean
import com.example.juanshichang.bean.User
import com.example.juanshichang.bean.ZyAllBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.glide.GlideUtil
import com.example.juanshichang.widget.IsInternet
import com.example.juanshichang.widget.LiveDataBus
import com.example.juanshichang.widget.OrderConfirmGridView
import com.google.gson.Gson
import com.qmuiteam.qmui.kotlin.onClick
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.youth.banner.Banner
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.transformer.RotateYTransformer
import com.youth.banner.util.BannerUtils
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_zy_all.*
import kotlinx.coroutines.Runnable
import org.jetbrains.anko.sdk27.coroutines.onItemClick
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @??????: yzq
 * @????????????: 2019/12/13 17:57
 * @????????????: ?????????????????????
 */
class HomeFragment : BaseFragment() {
    //    private var refresh: SwipeRefreshLayout? = null
    private var refreshLayout: SmartRefreshLayout? = null
    private var header: MaterialHeader? = null
    private var banner: Banner<HomeBean.Slideshow?, ImageBannerNetAdapter>? = null
    private var bList: List<HomeBean.Slideshow>? = null
    private var grid: OrderConfirmGridView? = null
    private var gList: List<HomeBean.Items>? = null
    private var gAdapter: MainGridAdapter? = null
    private var recycler: RecyclerView? = null
    private var rAdapter: NewHomeAdapter? = null
    private var rData: List<HomeBean.Product>? = null
    private var itemData: List<HomeBean.Items>? = null
    private var homeNewList: RecyclerView? = null
    private var hlAdapter: ZyAllAdapter? = null
    private var zyData: List<ZyAllBean.Product>? = null

    private var bus = LiveDataBus.get()
    private var page = 1
    private val hand = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> { //??????grid
                    gList?.let {
                        setGrid(mContext!!, it)
                    }
                }
                2 -> { //?????????????????????????????????????????????
                    mContext?.let {
                        if (Util.ifCurrentActivityTopStack(it) && !IsInternet.isNetworkAvalible(it)) {//??????????????? ?????? ?????????
                            it.dismissProgressDialog()
                            if (refreshLayout?.state?.isOpening!!) {
                                refreshLayout?.finishRefresh(false)
                                refreshLayout?.finishLoadMore(false)
                            }
                            ToastUtil.showToast(it, "????????????????????????,???????????????...")
                        } else if (Util.ifCurrentActivityTopStack(it) && IsInternet.isNetworkAvalible(
                                it
                            )
                        ) { //????????????????????????
                            if (gList == null || bList == null) {
                                getHome()
                            } else {
                                if (refreshLayout?.state?.isOpening!!) {
                                    refreshLayout?.finishRefresh(true)
                                    refreshLayout?.finishLoadMore(true)
                                }
//                                setBanner(bList)
//                                rAdapter?.setNewData(rData)
                                mContext?.dismissProgressDialog()
                            }
                        }
                    }
                }
                3 -> {//???????????? ?????? ?????????????????????
                    mContext?.let {
                        if (Util.ifCurrentActivityTopStack(it) && !IsInternet.isNetworkAvalible(it)) {//??????????????? ?????? ?????????
//                            refresh?.setRefreshing(false) todo ??????refreshLayout
                            if (refreshLayout?.state?.isOpening!!) {
                                refreshLayout?.finishRefresh(false)
                                refreshLayout?.finishLoadMore(false)
                            }
                            ToastUtil.showToast(it, "????????????????????????,???????????????...")
                        } else if (Util.ifCurrentActivityTopStack(it) && IsInternet.isNetworkAvalible(
                                it
                            )
                        ) { //????????????????????????
                            if (gList == null || bList == null) {
                                getHome()
                            } else {
//                                setBanner(bList)
//                                rAdapter?.setNewData(rData)
//                                refresh?.setRefreshing(false) todo ??????refreshLayout
                                if (refreshLayout?.state?.isOpening!!) {
                                    refreshLayout?.finishRefresh(true)
                                    refreshLayout?.finishLoadMore(true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun initViews(savedInstanceState: Bundle) {
        banner = mBaseView?.findViewById(R.id.mainBanner)
        grid = mBaseView?.findViewById(R.id.homeGrid)
        recycler = mBaseView?.findViewById(R.id.homeRyl)
//        refresh = mBaseView?.findViewById(R.id.homeSwipe) todo ??????refreshLayout
//        refresh?.setOnRefreshListener(this)
        refreshLayout = mBaseView?.findViewById(R.id.refreshLayout)
        header = mBaseView?.findViewById(R.id.header)
        refreshLayout?.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshlayout: RefreshLayout) {
                page = 1
                hand.sendEmptyMessageDelayed(3, 3000)
                recycler?.postDelayed(object : Runnable {
                    override fun run() {
                        getHome("Refresh")
                        reqProductNewList(page.toString())
                        bus.with("refresh").value = true
                    }
                }, 800)
            }
        })
        refreshLayout?.setOnLoadMoreListener { refreshlayout ->
            if (page < 3) {
                page++
                //??????????????????
                hand.sendEmptyMessageDelayed(3, 3000)
                recycler?.postDelayed(object : Runnable {
                    override fun run() {
                        reqProductNewList(page.toString())
                    }
                }, 800)
            } else {
                if (refreshLayout?.isLoading!!)
                    refreshLayout?.finishLoadMoreWithNoMoreData()
            }

        }
        header?.setColorSchemeResources(R.color.colorPrimary)


    }

    override fun initData() {
        rAdapter = NewHomeAdapter()
        /*val lm = object :LinearLayoutManager(mContext){
            override fun canScrollVertically(): Boolean {
                return false //????????????
            }
        }
        recycler?.layoutManager = lm*/
        recycler?.adapter = rAdapter
        rAdapter!!.setOnItemChildClickListener(object : BaseQuickAdapter.OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                when (view?.id) {
                    R.id.topHMore -> {//????????????????????????????????????
                        rData?.let {
                            val text= it[position].name
                            val intent = Intent(mContext!!, ZyAllActivity::class.java)
                            if (text.contains("???")) {
                                intent.putExtra("type", "0")
                            } else if (text.contains("???")) {
                                intent.putExtra("type", "1")
                            }
                            startActivity(intent)
                        }

                    }

                }
            }
        })

        homeNewList = mBaseView?.findViewById(R.id.homeNewList)
        hlAdapter = ZyAllAdapter()
        homeNewList?.adapter = hlAdapter
        hlAdapter?.setOnItemClickListener { adapter, view, position ->
            zyData?.let {
                val intent = Intent(mContext!!, ShangPinZyContains::class.java)
                intent.putExtra("product_id", it[position].product_id)
                startActivity(intent)
            }
        }
        reqProductNewList(page.toString())

    }


    override fun onResume() {
        super.onResume()
        if (gList == null) {
            Thread(object : Runnable {
                override fun run() {
                    val str = Util.readLocalJson(mContext!!, "gridjson.json")
                    LogTool.e("home1", str)
                    /*  val data = Gson().fromJson(str, GridItemBean.GridItemBeans::class.java)
                      LogTool.e("home2", data.toString())
                      data?.let {
                          gList = data.data.channel_list
                          hand.sendEmptyMessage(1)
                      }*/
                }
            }).start()
        }
        if (rData == null || bList == null || itemData == null) {
            mContext?.showProgressDialog()
            hand.sendEmptyMessageDelayed(2, 3000)
            getHome()
        }
    }

    private fun setUiData(data: HomeBean.HomeBeans) {
        bList = data.data.slideshow
        rData = data.data.products
        if (bList != null) {
            banner?.visibility = View.VISIBLE
            setBanner(bList)
        } else {
            banner?.visibility = View.GONE
        }
        rAdapter?.setNewData(rData)
        itemData = data.data.items
        data?.let {
            gList = itemData
            hand.sendEmptyMessage(1)
        }
    }

    private fun setBanner(list: List<HomeBean.Slideshow>?) {
        list?.let {
            val imgs = arrayListOf<String>()
            for (i in 0 until it.size) {
                imgs?.add(it[i].image)
            }

            banner!!.setAdapter(ImageBannerNetAdapter(list)) //???????????????
                //              .setCurrentItem(3,false)
                .addBannerLifecycleObserver(this) //???????????????????????????
                //              .setBannerRound(BannerUtils.dp2px(5))//??????
//                .addPageTransformer(RotateYTransformer())//??????????????????
                .setDelayTime(4500)//????????????
                .setIndicator(CircleIndicator(mContext)) //???????????????
                //????????????(????????????????????????????????????????????????????????????????????????????????????)
                .addPageTransformer(MarginPageTransformer(BannerUtils.dp2px(0F).toInt()))
                .setOnBannerListener { data: Any, position: Int ->
                    when (it[position].type) {
                        "app/product" -> { //????????????
                            val intent = Intent(mContext!!, ShangPinZyContains::class.java)
                            intent.putExtra("product_id", it[position].value)
                            startActivity(intent)
                        }
                        "app/category/goods" -> { //????????????
                            val intent = Intent(mContext!!, ZyAllActivity::class.java)
                            intent.putExtra("type", "2")
                            intent.putExtra("category_id", it[position].value)
                            startActivity(intent)
                        }
                        else -> {
                            ToastUtil.showToast(mContext!!, "????????????:${it[position].type}")
                        }
                    }
                } //??????????????????,???this??????

            /*banner?.setBannerStyle(BannerConfig.CIRCLE_INDICATOR) //?????????????????????
                ?.setIndicatorGravity(BannerConfig.CENTER)//???????????????
                ?.setImageLoader(GlideImageLoader(0))//?????????????????????
                ?.setBannerAnimation(Transformer.Tablet) //??????????????????
                ?.setDelayTime(4500)//????????????
                ?.isAutoPlay(true)
                ?.setImages(imgs)
                ?.setOnBannerListener(object : OnBannerListener {
                    override fun OnBannerClick(position: Int) {
                        when (it[position].type) {
                            "app/product" -> { //????????????
                                val intent = Intent(mContext!!, ShangPinZyContains::class.java)
                                intent.putExtra("product_id", it[position].value)
                                startActivity(intent)
                            }
                            "app/category/goods" -> { //????????????
                                val intent = Intent(mContext!!, ZyAllActivity::class.java)
                                intent.putExtra("category_id", it[position].value)
                                startActivity(intent)
                            }
                            else -> {
                                ToastUtil.showToast(mContext!!, "????????????:${it[position].type}")
                            }
                        }
                    }
                })?.start()*/
        }
    }

    private fun setGrid(mContext: BaseActivity?, data: List<HomeBean.Items>) {
        val size = data.size   //??????????????????
        var column = 0
        if (size % 2 != 0) {
            val l = size / 2 as Int
            column = l + 1
        } else {
            column = size / 2 as Int
        }
        grid?.numColumns = 4
        mContext?.let {
            gAdapter = MainGridAdapter(it, data)
            grid?.adapter = gAdapter
            gAdapter?.notifyDataSetChanged()
            grid?.onItemClick { p0, p1, p, p3 ->
                OneFragment.WebUrl = data[p].uri
                val intent = Intent(mContext, WebActivity::class.java)
                intent.putExtra(
                    "mobile_short_url",
                    OneFragment.WebUrl!!.trim()
                )
                BaseActivity.goStartActivity(mContext, intent)
                /* when (data[p].type) {
                     "link" -> {
                         *//*       OneFragment.WebUrl = null
                               OneFragment.getWebLink(data[p].channel_id, mContext)*//*
                        OneFragment.WebUrl = data[p].image_url
                        val intent = Intent(mContext, WebActivity::class.java)
                        intent.putExtra(
                            "mobile_short_url",
                            OneFragment.WebUrl!!.trim()
                        )
                        BaseActivity.goStartActivity(mContext, intent)
                        //todo ???????????????
                    }
                    "goods" -> {
                        val intent = Intent(mContext, PromotionActivity::class.java)
                        intent.putExtra("id", data[p].channel_id)
                        intent.putExtra("idName", "channel_id")
                        BaseActivity.goStartActivity(mContext, intent)
                    }
                    "zy" -> {
                        BaseActivity.goStartActivity(mContext, TopupActivity())
                    }
                    else -> {
                        ToastUtil.showToast(mContext, "??????????????????:" + data[p].type)
                    }
                }*/
            }
        }
    }

    //?????????????????????
    private fun getHome(vararg tag: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.HOME,
            NewParameter.getHomeMap(),
            object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo???????????????????????????????????????????????????
                    val t = result?.substring(result?.indexOf("{"), result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            ToastUtil.showToast(
                                mContext!!,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            if (!tag.isEmpty()) {
                                hand.removeMessages(3) //?????????????????????hand
                            } else {
                                hand.removeMessages(2)
                            }
                            val data: HomeBean.HomeBeans =
                                Gson().fromJson(t, HomeBean.HomeBeans::class.java)
                            setUiData(data)
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "Home??????????????????")
                    if (refreshLayout?.state?.isOpening!!) {
                        refreshLayout?.finishRefresh(true)
                        refreshLayout?.finishLoadMore(true)
                    }
                    if (!tag.isEmpty()) {
                        //??????????????????????????????
//                        refresh?.setRefreshing(false) todo ??????refreshLayout
                    } else {
                        mContext?.dismissProgressDialog()
                    }
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "Home??????????????????: ${e.toString()}")
                    if (refreshLayout?.state?.isOpening!!) {
                        refreshLayout?.finishRefresh(false)
                        refreshLayout?.finishLoadMore(false)
                    }
                }
            })
    }

    //????????????
    //??????????????????
    private fun reqProductNewList(page: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.PRODUCTNEWLIST,
            NewParameter.getNewProductListMap(page),
            object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo???????????????????????????????????????????????????
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
                            ) { //????????????????????????
                                if (refreshLayout?.isLoading!!)
                                    refreshLayout?.finishLoadMoreWithNoMoreData()
                                /*            zyRecycler?.postDelayed(kotlinx.coroutines.Runnable {
                                                myLoading?.dismiss()
                                                finish()
                                            }, 800)*/
                            } else if (jsonObj.optString(JsonParser.JSON_CODE)
                                    .equals("215")
                            ) {
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
                                zyData = data.data.products!!
                            } else {
                                zyData = zyData?.plus(data.data.products!!)
                            }
                            if (page.toInt() > 1) {
                                hlAdapter?.addData(data.data.products!!)
                            } else {
                                hlAdapter?.setNewData(data.data.products!!)
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    if (refreshLayout?.state?.isOpening!!) {
                        refreshLayout?.finishRefresh(true)
                        refreshLayout?.finishLoadMore(true)
                    }
                }

                override fun onError(e: Throwable?) {
                    if (refreshLayout?.state?.isOpening!!) {
                        refreshLayout?.finishRefresh(false)
                        refreshLayout?.finishLoadMore(false)
                    }
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        hand.removeCallbacksAndMessages(null)
    }
}
