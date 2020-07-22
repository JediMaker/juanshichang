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
 * @作者: yzq
 * @创建日期: 2019/12/13 17:57
 * @文件作用: 自营商品首页面
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
                1 -> { //更新grid
                    gList?.let {
                        setGrid(mContext!!, it)
                    }
                }
                2 -> { //用于结束首次加载的网络不佳问题
                    mContext?.let {
                        if (Util.ifCurrentActivityTopStack(it) && !IsInternet.isNetworkAvalible(it)) {//如果在前台 但是 无网络
                            it.dismissProgressDialog()
                            if (refreshLayout?.state?.isOpening!!) {
                                refreshLayout?.finishRefresh(false)
                                refreshLayout?.finishLoadMore(false)
                            }
                            ToastUtil.showToast(it, "您的网络状态不佳,请检查网络...")
                        } else if (Util.ifCurrentActivityTopStack(it) && IsInternet.isNetworkAvalible(
                                it
                            )
                        ) { //在前台并且有网络
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
                3 -> {//用于结束 刷新 的网络不佳问题
                    mContext?.let {
                        if (Util.ifCurrentActivityTopStack(it) && !IsInternet.isNetworkAvalible(it)) {//如果在前台 但是 无网络
//                            refresh?.setRefreshing(false) todo 替换refreshLayout
                            if (refreshLayout?.state?.isOpening!!) {
                                refreshLayout?.finishRefresh(false)
                                refreshLayout?.finishLoadMore(false)
                            }
                            ToastUtil.showToast(it, "您的网络状态不佳,请检查网络...")
                        } else if (Util.ifCurrentActivityTopStack(it) && IsInternet.isNetworkAvalible(
                                it
                            )
                        ) { //在前台并且有网络
                            if (gList == null || bList == null) {
                                getHome()
                            } else {
//                                setBanner(bList)
//                                rAdapter?.setNewData(rData)
//                                refresh?.setRefreshing(false) todo 替换refreshLayout
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
//        refresh = mBaseView?.findViewById(R.id.homeSwipe) todo 替换refreshLayout
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
                //追加最新商品
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
                return false //关闭滑动
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
                    R.id.topHMore -> {//点击更多跳转到商品列表页
                        rData?.let {
                            val text= it[position].name
                            val intent = Intent(mContext!!, ZyAllActivity::class.java)
                            if (text.contains("热")) {
                                intent.putExtra("type", "0")
                            } else if (text.contains("新")) {
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

            banner!!.setAdapter(ImageBannerNetAdapter(list)) //设置适配器
                //              .setCurrentItem(3,false)
                .addBannerLifecycleObserver(this) //添加生命周期观察者
                //              .setBannerRound(BannerUtils.dp2px(5))//圆角
                .addPageTransformer(RotateYTransformer())//添加切换效果
                .setDelayTime(4500)//轮播间隔
                .setIndicator(CircleIndicator(mContext)) //设置指示器
                //添加间距(如果使用了画廊效果就不要添加间距了，因为内部已经添加过了)
                .addPageTransformer(MarginPageTransformer(BannerUtils.dp2px(0F).toInt()))
                .setOnBannerListener { data: Any, position: Int ->
                    when (it[position].type) {
                        "app/product" -> { //商品详情
                            val intent = Intent(mContext!!, ShangPinZyContains::class.java)
                            intent.putExtra("product_id", it[position].value)
                            startActivity(intent)
                        }
                        "app/category/goods" -> { //商品列表
                            val intent = Intent(mContext!!, ZyAllActivity::class.java)
                            intent.putExtra("type", "2")
                            intent.putExtra("category_id", it[position].value)
                            startActivity(intent)
                        }
                        else -> {
                            ToastUtil.showToast(mContext!!, "未知类型:${it[position].type}")
                        }
                    }
                } //设置点击事件,传this也行

            /*banner?.setBannerStyle(BannerConfig.CIRCLE_INDICATOR) //显示数字指示器
                ?.setIndicatorGravity(BannerConfig.CENTER)//指示器居右
                ?.setImageLoader(GlideImageLoader(0))//设置图片加载器
                ?.setBannerAnimation(Transformer.Tablet) //设置动画效果
                ?.setDelayTime(4500)//轮播间隔
                ?.isAutoPlay(true)
                ?.setImages(imgs)
                ?.setOnBannerListener(object : OnBannerListener {
                    override fun OnBannerClick(position: Int) {
                        when (it[position].type) {
                            "app/product" -> { //商品详情
                                val intent = Intent(mContext!!, ShangPinZyContains::class.java)
                                intent.putExtra("product_id", it[position].value)
                                startActivity(intent)
                            }
                            "app/category/goods" -> { //商品列表
                                val intent = Intent(mContext!!, ZyAllActivity::class.java)
                                intent.putExtra("category_id", it[position].value)
                                startActivity(intent)
                            }
                            else -> {
                                ToastUtil.showToast(mContext!!, "未知类型:${it[position].type}")
                            }
                        }
                    }
                })?.start()*/
        }
    }

    private fun setGrid(mContext: BaseActivity?, data: List<HomeBean.Items>) {
        val size = data.size   //动态设置列数
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
                        //todo 偷天换日法
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
                        ToastUtil.showToast(mContext, "不存在的类型:" + data[p].type)
                    }
                }*/
            }
        }
    }

    //获取主页面数据
    private fun getHome(vararg tag: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.HOME,
            NewParameter.getHomeMap(),
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
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            ToastUtil.showToast(
                                mContext!!,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            if (!tag.isEmpty()) {
                                hand.removeMessages(3) //加载完成则删除hand
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
                    LogTool.e("onCompleted", "Home列表请求完成")
                    if (refreshLayout?.state?.isOpening!!) {
                        refreshLayout?.finishRefresh(true)
                        refreshLayout?.finishLoadMore(true)
                    }
                    if (!tag.isEmpty()) {
                        //刷新完成取消刷新动画
//                        refresh?.setRefreshing(false) todo 替换refreshLayout
                    } else {
                        mContext?.dismissProgressDialog()
                    }
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "Home列表请求失败: ${e.toString()}")
                    if (refreshLayout?.state?.isOpening!!) {
                        refreshLayout?.finishRefresh(false)
                        refreshLayout?.finishLoadMore(false)
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
