package com.example.juanshichang.fragment


import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.juanshichang.R
import com.example.juanshichang.adapter.HomeAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.GridItemBean
import com.example.juanshichang.bean.HomeEntity
import com.example.juanshichang.bean.MainBannerBean
import com.example.juanshichang.bean.MainRecyclerBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import com.qmuiteam.qmui.arch.QMUIFragment
import kotlinx.coroutines.Runnable
import org.jetbrains.anko.layoutInflater
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/8/23 17:49
 * @文件作用: 首页 精选
 */
class SelectionFragment : QMUIFragment(), BaseQuickAdapter.RequestLoadMoreListener,
    SwipeRefreshLayout.OnRefreshListener {
    private var seleView: View? = null
    private var rvData = mutableListOf<MainRecyclerBean.Theme>()
    private var mainList = arrayListOf<HomeEntity>()
    private var homeAdapter: HomeAdapter? = null
    private var hr: RecyclerView? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var nextSize = 5
    private var next: Int = 1  //这个 字段 设计目的 为了 加载更多 拉取页面 但是 ... 后台数据一次性给到 故 此字段闲置
    private var bHome: HomeEntity? = null
    private var gHome: HomeEntity? = null
    private var rHome: HomeEntity? = null
    private var base: BaseActivity? = null
    private var isOneNotify:Boolean = false
    private var BotEnd:Int = 0
    private var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    LogTool.e("tools_Sekection", "b：$b g:$g r:$r")
                    if (b != 1 && g != 1 && r != 1) {
                        if (mainList.size != 0) {
                            mainList.clear()
                        }
                        synchronized(SelectionFragment::class) {
                            mainList.add(0, bHome!!)
                            mainList.add(1, gHome!!)
                            mainList.add(2, rHome!!)
                            homeAdapter?.setNewData(mainList as List<MultiItemEntity>?)
                        }
//                        homeAdapter?.setEnableLoadMore(true)
                        b = 1
                        g = 1
                        r = 1
                        BotEnd = 0
                        isOneNotify = true
                        homeAdapter?.emptyView =
                            View.inflate(context, R.layout.activity_not_null, null)
                        base?.dismissProgressDialog()
                        this.removeMessages(1)
                    }else if(b != 1 && g != 1 && r == 1){ //这里用来应对 第三个接口 走空问题！！！
                        if (mainList.size != 0) {
                            mainList.clear()
                        }
                        synchronized(SelectionFragment::class) {
                            mainList.add(0, bHome!!)
                            mainList.add(1, gHome!!)
//                            mainList.add(2, rHome!!)
                            homeAdapter?.setNewData(mainList as List<MultiItemEntity>?)
                        }
                        getRecycler(2, next)
//                        isOneNotify = true
                        base?.dismissProgressDialog()
                        if(BotEnd<1){
                            this.sendEmptyMessageDelayed(1,1000)
                            BotEnd++
                        }
                    }else {
                        this.sendEmptyMessageDelayed(1, 50)
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        base = context as BaseActivity
    }
    override fun onCreateView(): View {
        seleView = context?.layoutInflater!!.inflate(R.layout.fragment_selection, null)
        initData()
        return seleView!!
    }


    private fun initData() {
        base?.showProgressDialog()
        setRecycler()
        synchronized(SelectionFragment::class){
//            getBanner()
//            getGrid()
//            getRecycler(2, next)
            handler.sendEmptyMessageDelayed(1, 50)
        }
        timerLogin.start() //启动定时器
    }
    override fun onResume() {
        super.onResume()
        //写在这里 无论 切换回来 还是 息屏唤醒 都会 请求网络... 增加流量消耗
        if(!isOneNotify){
            getBanner()
            getGrid()
            getRecycler(2, next)
        }
//        getBanner()
//        getGrid()
//        getRecycler(2, next)
//        handler.sendEmptyMessageDelayed(1,50)
        //迁移至于initData....
    }
    override fun onPause() {
        super.onPause()
        timerLogin.cancel()
    }
    //上滑加载更多
    override fun onLoadMoreRequested() {
        val numSize = rvData.size
        val oldNextSize = nextSize
        if ((numSize - nextSize) <= 1) {
            nextSize += 1
        } else {
            if ((numSize - nextSize) % 2 != 0) {
                nextSize += 3
            } else {
                nextSize += 2
            }
        }
        val addData = arrayListOf<MainRecyclerBean.Theme>()
        hr?.postDelayed(object : Runnable {
            override fun run() {
                if (nextSize <= rvData.size) {
                    for (index in oldNextSize until nextSize) {
                        addData.add(rvData[index])
                    }
                    homeAdapter?.recyclerAddData(addData)
                    //数据加载完成
                    homeAdapter?.loadMoreComplete()
                } else {
                    //数据加载完毕
                    homeAdapter?.loadMoreEnd()
                }
            }
        }, 1800)
    }

    //下拉刷新
    override fun onRefresh() {
        //刷新的时候禁止加载更多
//        base?.showProgressDialog()
        homeAdapter?.setEnableLoadMore(false)
        hr?.postDelayed(object : Runnable {
            override fun run() {
                synchronized(SelectionFragment::class) {
                    homeAdapter?.b_i = 1
                    homeAdapter?.g_i = 1
                    homeAdapter?.r_i = 1
                    nextSize = 5
                    mainList.clear()
                    getBanner()
                    getGrid()
                    getRecycler(2, next)
                    //更新数据
                    handler.sendEmptyMessage(1)
                    //刷新完成取消刷新动画
                    mSwipeRefreshLayout?.setRefreshing(false)
                    //刷新完成重新开启加载更多
                    homeAdapter?.setEnableLoadMore(true)
                }
            }
        }, 1000)//刷新延迟
    }

    //todo界面初始化
    private fun setRecycler() {
        hr = seleView?.findViewById<RecyclerView>(R.id.home_recycler)
        mSwipeRefreshLayout = seleView?.findViewById<SwipeRefreshLayout>(R.id.mSwipeRefreshLayout)
        //初始化 adapter
        homeAdapter = HomeAdapter(mainList)
        //Add
        /**
         * 渐显 ALPHAIN
         * 缩放 SCALEIN
         * 从下到上 SLIDEIN_BOTTOM
         * 从左到右 SLIDEIN_LEFT
         * 从右到左 SLIDEIN_RIGHT
         */
        homeAdapter?.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT) //SCALEIN
        homeAdapter?.setOnLoadMoreListener(this, hr)//设置加载更多
        mSwipeRefreshLayout?.setOnRefreshListener(this)
        //默认第一次加载会进入回调，如果不需要可以配置
        homeAdapter?.disableLoadMoreIfNotFullPage()
        //当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        homeAdapter?.setPreLoadNumber(1)
        //初始化 RecyclerView
        //解决数据加载不完的问题
        hr?.setNestedScrollingEnabled(false)
        //当知道Adapter内Item的改变不会影响RecyclerView宽高的时候，可以设置为true让RecyclerView避免重新计算大小
        hr?.setHasFixedSize(true)
        //解决数据加载完成后, 没有停留在顶部的问题
        hr?.setFocusable(false)
        val lm = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        hr?.layoutManager = lm
        hr?.adapter = homeAdapter
    }

    //todo 数据写入  数据类型 转换
    var b = 1
    var g = 1
    var r = 1
    private fun setRv2(recyclerList: List<MainRecyclerBean.Theme>): HomeEntity {
        rHome = null
        if (recyclerList != null && recyclerList.size > 0) {
            val entity = HomeEntity(HomeEntity.TYPE_RECYCLER)
            for (index in 0 until recyclerList.size) {
                val r = recyclerList[index]
                if (!entity.recyclers!!.contains(r)) {
                    entity.recyclers!!.add(r)
                }
            }
            r = 2
            return entity
        }
        return HomeEntity()
    }

    private fun setGrid2(gridList: List<GridItemBean.Channel>): HomeEntity {
        gHome = null
        if (gridList != null && gridList.size > 0) {
            val entity = HomeEntity(HomeEntity.TYPE_GRID)
            for (index in 0 until gridList.size) {
                val g = gridList[index]
                if (!entity.grids!!.contains(g)) {
                    entity.grids!!.add(g)
                }
            }
            g = 2
            return entity
        }
        return HomeEntity()
    }

    private fun setBanner2(bannerList: List<MainBannerBean.Banner>): HomeEntity {
        bHome = null
        if (bannerList != null && bannerList.size > 0) {
            val entity = HomeEntity(HomeEntity.TYPE_BANNER)
            for (index in 0 until bannerList.size) {
                val b = bannerList[index]
                if (!entity.banners!!.contains(b)) {
                    entity.banners!!.add(b)
                }
            }
            b = 2
            return entity
        }
        return HomeEntity()
    }

    //todo 网络请求
    private fun getBanner() {
        LogTool.e("okgo","启动Banner请求")
        HttpManager.getInstance()
            .post(Api.MAINBANNER, Parameter.getMainBannerMap(), object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(context!!, jsonObj.optString(JsonParser.JSON_MSG))
                        } else {
                            val Data =
                                Gson().fromJson(str, MainBannerBean.MainBannerBeans::class.java)
                            val bannerList = Data.data.banner_list
                            bHome = setBanner2(bannerList)
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "Banner加载完成!")
                }

                override fun onError(e: Throwable?) {
//                getBanner()
                    LogTool.e("onError", "Banner加载失败!" + e)
                }
            })
    }

    private fun getRecycler(theme_goods_count: Int, next: Int) {
        LogTool.e("okgo","启动Recycler请求")
        HttpManager.getInstance()
            .post(
                Api.THEMELIST,
                Parameter.getRecyclerMap(theme_goods_count),
                object : Subscriber<String>() {
                    override fun onNext(str: String?) {
                        if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                            var jsonObj: JSONObject = JSONObject(str)
                            if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                                ToastUtil.showToast(
                                    context!!,
                                    jsonObj.optString(JsonParser.JSON_MSG)
                                )
                            } else {
                                if (next == 1) {
                                    rvData.clear()
                                }
                                val Data = Gson().fromJson(
                                    str,
                                    MainRecyclerBean.MainRecyclerBeans::class.java
                                )
                                val recyclerList = Data.data.theme_list
                                //此处进行处理 默认预加载五条
                                if (recyclerList.size >= 5) {
                                    val goData = arrayListOf<MainRecyclerBean.Theme>()
                                    for (index in 0 until 5) {
                                        goData.add(recyclerList[index])
                                    }
                                    rHome = setRv2(goData)
                                } else {
                                    rHome = setRv2(recyclerList)
                                }
                                rvData.addAll(recyclerList) //todo 需要 不可注释...
                            }
                        }
                    }

                    override fun onCompleted() {
                        LogTool.e("onCompleted", "Recycler加载完成!")
                    }

                    override fun onError(e: Throwable?) {
//                    getRecycler(theme_goods_count, next)
                        LogTool.e("onError", "Recycler加载失败!" + e)
                    }
                })
    }

    private fun getGrid() {
        LogTool.e("okgo","启动Grid请求")
        HttpManager.getInstance()
            .post(Api.CHANNELLIST, Parameter.getMainBannerMap(), object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        val jsonObj: JSONObject? = JSONObject(str)
                        if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(context!!, jsonObj!!.optString(JsonParser.JSON_MSG))
                        } else {
                            val data = Gson().fromJson(str, GridItemBean.GridItemBeans::class.java)
                            val gridList = data.data.channel_list
                            gHome = setGrid2(gridList)
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "Grid加载完成!")
                }

                override fun onError(e: Throwable?) {
//                getGrid()
                    LogTool.e("onError", "Grid加载失败!" + e)
                }

            })
    }


    //这个计时器用于轮询首次进入页面的是否成功刷新显示 超时没有成功显示 就关闭进度框 并 提示
    var timerLogin: CountDownTimer = object : CountDownTimer(5000, 5000) {
        override fun onFinish() {
            LogTool.e("tools_Sekection", "onFinish  首次加载 轮询结束！！！")
        }

        override fun onTick(millisUntilFinished: Long) {
            if (!isOneNotify) {
                if(base?.progressdialog?.isShowing()!!){
                    base?.runOnUiThread(object : Runnable {
                        override fun run() {
                            base?.dismissProgressDialog()
                            ToastUtil.showToast(this@SelectionFragment.context!!,"请稍后刷新 以获取最新优惠资讯")
                            LogTool.e("tools_Sekection", "onTick  首次加载失败了")
                            LogTool.e("okgo","启动应急策略")
                            onRefresh()
                        }
                    })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
