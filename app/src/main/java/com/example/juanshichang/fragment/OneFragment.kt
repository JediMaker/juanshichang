package com.example.juanshichang.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.OnClick
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.juanshichang.MyApp

import com.example.juanshichang.R
import com.example.juanshichang.activity.SearcheActivity
import com.example.juanshichang.activity.WebActivity
import com.example.juanshichang.adapter.HomeAdapter
import com.example.juanshichang.base.*
import com.example.juanshichang.bean.*
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.fragment_one.*
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:52
 * @文件作用: 首页
 */
class OneFragment : BaseFragment(), BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    /* override fun onCreateView(
         inflater: LayoutInflater, container: ViewGroup?,
         savedInstanceState: Bundle?
     ): View? {
         // Inflate the layout for this fragment
         return inflater.inflate(R.layout.fragment_one, container, false)
     }*/
    var next: Int = 1
    var nextSize = 5
    var rvData = mutableListOf<MainRecyclerBean.Theme>()
    var mainList = arrayListOf<HomeEntity>()
    var homeAdapter: HomeAdapter? = null
    var hr: RecyclerView? = null
    var mSwipeRefreshLayout:SwipeRefreshLayout? = null
    var bHome: HomeEntity? = null
    var gHome: HomeEntity? = null
    var rHome: HomeEntity? = null
    var addHome: HomeEntity? = null
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                if (b != 1 && g != 1 && r != 1) {
                    mainList.add(bHome!!)
                    mainList.add(gHome!!)
                    mainList.add(rHome!!)
                    homeAdapter?.setNewData(mainList as List<MultiItemEntity>?)
                    homeAdapter?.setEnableLoadMore(true)
                    onCreate(null)
                    b = 1
                    g = 1
                    r = 1
                } else {
                    this.sendEmptyMessageDelayed(1, 100)
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_one
    }

    override fun initViews(savedInstanceState: Bundle) {
        QMUIStatusBarHelper.translucent(mContext)
        StatusBarUtil.addStatusViewWithColor(mContext, R.color.colorPrimary)
        MyApp.requestPermission(mContext!!)
        getBanner()
        getGrid()
        getRecycler(2, next)
        handler.sendEmptyMessageDelayed(1, 100) //延时 100 ms
    }

    override fun initData() {
        hr = mBaseView?.findViewById<RecyclerView>(R.id.home_recycler)
        mSwipeRefreshLayout = mBaseView?.findViewById<SwipeRefreshLayout>(R.id.mSwipeRefreshLayout)
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
        homeAdapter?.openLoadAnimation(BaseQuickAdapter.SCALEIN)
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
        val lm = LinearLayoutManager(mContext!!, RecyclerView.VERTICAL, false)
        hr?.layoutManager = lm
        hr?.adapter = homeAdapter
    }

    @OnClick(R.id.etsearch)
    fun onViewClicked(v: View) {
        when (v.id) {
            R.id.etsearch -> {
                val intent = Intent(mContext, SearcheActivity::class.java)
                //...
                startActivity(intent)
            }
            else -> {
            }
        }
    }

    override fun onStart() {
        super.onStart()
//        main_banner.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
//        main_banner.stopAutoPlay()
    }

    private fun getBanner() {
        HttpManager.getInstance().post(Api.MAINBANNER, Parameter.getMainBannerMap(), object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject = JSONObject(str)
                    if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@OneFragment.mContext!!, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val Data = Gson().fromJson(str, MainBannerBean.MainBannerBeans::class.java)
                        val bannerList = Data.data.banner_list
                        bHome = setBanner2(bannerList)
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted", "Banner加载完成!")
            }

            override fun onError(e: Throwable?) {
                getBanner()
                Log.e("onError", "Banner加载失败!" + e)
            }
        })
    }

    private fun getRecycler(theme_goods_count: Int, next: Int) {
        HttpManager.getInstance()
            .post(Api.THEMELIST, Parameter.getRecyclerMap(theme_goods_count), object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(this@OneFragment.mContext!!, jsonObj.optString(JsonParser.JSON_MSG))
                        } else {
                            if (next == 1) {
                                rvData.clear()
                            }
                            val Data = Gson().fromJson(str, MainRecyclerBean.MainRecyclerBeans::class.java)
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
                    Log.e("onCompleted", "Recycler加载完成!")
                }

                override fun onError(e: Throwable?) {
                    getRecycler(theme_goods_count, next)
                    Log.e("onError", "Recycler加载失败!" + e)
                }
            })
    }

    private fun getGrid() {
        HttpManager.getInstance().post(Api.CHANNELLIST, Parameter.getMainBannerMap(), object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = JSONObject(str)
                    if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@OneFragment.mContext!!, jsonObj!!.optString(JsonParser.JSON_MSG))
                    } else {
                        val data = Gson().fromJson(str, GridItemBean.GridItemBeans::class.java)
                        val gridList = data.data.channel_list
                        gHome = setGrid2(gridList)
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted", "Grid加载完成!")
            }

            override fun onError(e: Throwable?) {
                getGrid()
                Log.e("onError", "Grid加载失败!" + e)
            }

        })
    }

    companion object {
        var WebUrl: String? = null
        //获取链接 跳转
        fun getWebLink(channel_id: Long, context: Context) {
            //Parameter.getGridClickMap(channel_id, 0, 20)
            HttpManager.getInstance()
                .post(
                    Api.CHANNEL,
                    Parameter.getBaseSonMap("channel_id", channel_id, 0, 20),
                    object : Subscriber<String>() {
                        override fun onNext(str: String?) {
                            if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                                var jsonObj: JSONObject = JSONObject(str)
                                if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                                    ToastUtil.showToast(context, jsonObj.optString(JsonParser.JSON_MSG))
                                } else {
                                    val jsonObjs = jsonObj.getJSONObject("data")
                                    Log.e("kkkkk", jsonObjs.toString())
                                    WebUrl = jsonObjs.getString("url")
                                    Log.e("kkkkk", WebUrl.toString())
//                            context.runOnUiThread(Runnable {
//                                var intent = Intent(context,WebActivity::class.java)
//                                intent.putExtra("mobile_short_url",WebUrl!!.trim())  //todo 偷天换日法
//                                startActivity(intent)
//                            })
                                    context.runOnUiThread {
                                        var intent = Intent(context, WebActivity::class.java)
                                        intent.putExtra("mobile_short_url", WebUrl!!.trim())  //todo 偷天换日法
                                        BaseActivity.goStartActivity(context, intent)
                                    }
                                }
                            }
                        }

                        override fun onCompleted() {
                            Log.e("onCompleted", "Banner加载完成!")
                        }

                        override fun onError(e: Throwable?) {
                            Log.e("onError", "Banner加载失败!")
                        }
                    })
        }
    }

    //废弃的方法
    private fun setGrid(gridList: List<GridItemBean.Channel>) {
        /*var gridAdapter = MainGridAdapter(this.mContext!!, gridList)
        val size = gridList.size   //动态设置列数
        var column = 0
        if (size % 2 != 0) {
            val l = size / 2 as Int
            column = l + 1
        } else {
            column = size / 2 as Int
        }
        main_gridView.numColumns = column
        main_gridView.adapter = gridAdapter
        gridAdapter.notifyDataSetChanged()
        main_gridView.onItemClick { p0, p1, p, p3 ->
            if (gridList[p].type.equals("link")) {
                WebUrl = null
                getWebLink(gridList[p].channel_id, mContext!!)
            } else if (gridList[p].type.equals("goods")) {
                var intent = Intent(mContext!!, PromotionActivity::class.java)
                intent.putExtra("channel_id", gridList[p].channel_id)
                startActivity(intent)
            } else {
                ToastUtil.showToast(mContext!!, "不存在的类型:" + gridList[p].type)
            }
        }*/
    }

    var imgs: MutableList<MainBannerBean.Banner>? = null
    //废弃的方法 和 参数
    private fun setBanner(bannerList: List<MainBannerBean.Banner>) {
        /*//随机数
        imgs = mutableListOf()
        var imgUrls: MutableList<String> = mutableListOf() //设置返回数据如果多于5 则 只随机抽取5张轮播 否则 显示全部
        if (bannerList.size > 5) {
            imgs?.clear()
            while (imgs?.size!! < 5) {
                var r = (0 until bannerList.size).random()// (0…10).random() === (0 until 11).random()
                if (!imgs!!.contains(bannerList[r])) {
                    imgs?.add(bannerList[r])
                }
            }
        } else {
            imgs?.clear()
            imgs?.addAll(bannerList)
        }
        for (index in 0 until imgs!!.size) {
            imgUrls.add(imgs!![index].image_url)
        }

//        main_banner.visibility = View.VISIBLE
        main_banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR) //显示数字指示器
        //设置指示器位置（当banner模式中有指示器时）
        main_banner.setIndicatorGravity(BannerConfig.LEFT)//指示器居右
        //设置图片加载器
        main_banner.setImageLoader(GlideImageLoader())
        //设置图片集合
        main_banner.setImages(imgUrls)
        //设置动画效果
        main_banner.setBannerAnimation(Transformer.Default)
        //设置轮播图片间隔时间（不设置默认为2000）
        main_banner.setDelayTime(4500)
        //设置是否自动轮播（不设置则默认自动）
        main_banner.isAutoPlay(true)
        //设置轮播要显示的标题和图片对应（如果不传默认不显示标题）
        //meBanner.setBannerTitles(images);
        //点击事件请放到start()前
        main_banner.setOnBannerListener(OnBannerListener {
            if (imgs!![it].type.equals("goods")) {
                var intent = Intent(this.mContext!!, PromotionActivity::class.java)
                intent.putExtra("banner_id", imgs!![it].banner_id.toLong())
                startActivity(intent)
            } else {
                ToastUtil.showToast(this.mContext!!, "这个商品类型异常,快去看日志...")
                Log.e(
                    "shopping",
                    "id:" + imgs!![it].banner_id + "  type" + imgs!![it].type
                ) //1.列表
            }
        })
        //banner设置方法全部调用完毕时最后调用
        main_banner.start()*/
    }


    var b = 1
    var g = 1
    var r = 1
    private fun setRv2(recyclerList: List<MainRecyclerBean.Theme>): HomeEntity {
        rHome = null
        if (recyclerList != null && recyclerList.size > 0) {
            var entity = HomeEntity(HomeEntity.TYPE_RECYCLER)
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
            var entity = HomeEntity(HomeEntity.TYPE_GRID)
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
            var entity = HomeEntity(HomeEntity.TYPE_BANNER)
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

    /**
     * 下拉刷新
     */
    override fun onRefresh() {
        //刷新的时候禁止加载更多
        homeAdapter?.setEnableLoadMore(false)
        hr?.postDelayed(object : Runnable{
            override fun run() {
                homeAdapter?.b_i = 1
                homeAdapter?.g_i = 1
                homeAdapter?.r_i = 1
                nextSize = 5
                mainList.clear()
                getBanner()
                getGrid()
                getRecycler(2, next)
                Log.e("onLoadMoreRequested","nextSize:$nextSize")
                Log.e("onLoadMoreRequested","sendEmptyMessage:1")
                //更新数据
                handler.sendEmptyMessage(1)
                //刷新完成取消刷新动画
                mSwipeRefreshLayout?.setRefreshing(false)
                //刷新完成重新开启加载更多
                homeAdapter?.setEnableLoadMore(true)
            }
        },1000)//刷新延迟
    }

    /**
     * 上拉加载更多
     */
    override fun onLoadMoreRequested() {
        val numSize = rvData.size
        val oldNextSize = nextSize
        Log.e("onLoadMoreRequested","nextSize:$nextSize   oldNextSize:$oldNextSize  rvData.size"+rvData.size)
        addHome = null
        if((numSize - nextSize) <= 1){
            nextSize += 1
        }else{
            if ((numSize - nextSize) % 2 != 0) {
                nextSize += 3
            }else{
                nextSize += 2
            }
        }
        Log.e("onLoadMoreRequested2","nextSize:$nextSize")
        val addData = arrayListOf<MainRecyclerBean.Theme>()
        hr?.postDelayed(object : Runnable{
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
        },1500)

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
