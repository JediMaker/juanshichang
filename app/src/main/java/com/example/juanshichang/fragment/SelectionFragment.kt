package com.example.juanshichang.fragment


import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity

import com.example.juanshichang.R
import com.example.juanshichang.adapter.HomeAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.*
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import com.qmuiteam.qmui.arch.QMUIFragment
import org.jetbrains.anko.layoutInflater
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/8/23 17:49
 * @文件作用: 首页 精选
 */
class SelectionFragment : QMUIFragment(),BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    var seleView:View? = null
    var rvData = mutableListOf<MainRecyclerBean.Theme>()
    var mainList = arrayListOf<HomeEntity>()
    var homeAdapter: HomeAdapter? = null
    var hr: RecyclerView? = null
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    var nextSize = 5
    var next: Int = 1
    var bHome: HomeEntity? = null
    var gHome: HomeEntity? = null
    var rHome: HomeEntity? = null
    var addHome: HomeEntity? = null
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                1->{
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
                        this.sendEmptyMessageDelayed(1, 50)
                    }
                }
            }
        }
    }
    override fun onCreateView(): View {
        seleView = context?.layoutInflater!!.inflate(R.layout.fragment_selection,null)
        initData()
        return seleView!!
    }

    override fun onResume() {
        super.onResume()
        getBanner()
        getGrid()
        getRecycler(2, next)
        handler.sendEmptyMessageDelayed(1,50)
    }
    private fun initData() {
        setRecycler()
    }
    //上滑加载更多
    override fun onLoadMoreRequested() {
        val numSize = rvData.size
        val oldNextSize = nextSize
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
        },3000)
    }
    //下拉刷新
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
        homeAdapter?.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM) //SCALEIN
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
    //todo 数据写入
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
    //todo 网络请求
    private fun getBanner() {
        HttpManager.getInstance().post(Api.MAINBANNER, Parameter.getMainBannerMap(), object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject = JSONObject(str)
                    if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(context!!, jsonObj.optString(JsonParser.JSON_MSG))
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
                            ToastUtil.showToast(context!!, jsonObj.optString(JsonParser.JSON_MSG))
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
                        ToastUtil.showToast(context!!, jsonObj!!.optString(JsonParser.JSON_MSG))
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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
