package com.example.juanshichang.fragment


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.example.juanshichang.R
import com.example.juanshichang.activity.PromotionActivity
import com.example.juanshichang.activity.TopupActivity
import com.example.juanshichang.adapter.MainGridAdapter
import com.example.juanshichang.adapter.NewHomeAdapter
import com.example.juanshichang.base.*
import com.example.juanshichang.bean.GridItemBean
import com.example.juanshichang.bean.HomeBean
import com.example.juanshichang.bean.SiteBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.GlideImageLoader
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import com.example.juanshichang.widget.OrderConfirmGridView
import com.google.gson.Gson
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.listener.OnBannerListener
import kotlinx.coroutines.Runnable
import org.jetbrains.anko.sdk27.coroutines.onItemClick
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

class HomeFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    private var refresh: SwipeRefreshLayout? = null
    private var banner: Banner? = null
    private var bList:List<HomeBean.Slideshow>?= null
    private var grid: OrderConfirmGridView? = null
    private var gList:List<GridItemBean.Channel>? = null
    private var gAdapter: MainGridAdapter? = null
    private var recycler: RecyclerView? = null
    private var rAdapter:NewHomeAdapter? = null
    private var rData:List<HomeBean.Product>? = null
    private val hand = object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                1->{ //更新grid
                    gList?.let {
                        setGrid(mContext!!,it)
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
        refresh = mBaseView?.findViewById(R.id.homeSwipe)
        refresh?.setOnRefreshListener(this)
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
    }
    override fun onRefresh() {
        recycler?.postDelayed(object :Runnable{
            override fun run() {
                getHome("Refresh")
            }
        },800)
    }

    override fun onResume() {
        super.onResume()
        if(gList == null){
            Thread(object :Runnable {
                override fun run() {
                    val str = Util.readLocalJson(mContext!!,"gridjson.json")
                    LogTool.e("home1",str)
                    val data = Gson().fromJson(str,GridItemBean.GridItemBeans::class.java)
                    LogTool.e("home2",data.toString())
                    data?.let {
                        gList = data.data.channel_list
                        hand.sendEmptyMessage(1)
                    }
                }
            }).start()
        }
        if(gList == null || bList == null){
            mContext?.showProgressDialog()
            getHome()
        }
    }

    override fun onStart() {
        super.onStart()
        banner?.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        banner?.stopAutoPlay()
    }
    private fun setUiData(data: HomeBean.HomeBeans) {
        bList = data.data.slideshow
        rData = data.data.products
        setBanner(bList)
        rAdapter?.setNewData(rData)
    }
    private fun setBanner(list:List<HomeBean.Slideshow>?){
        list?.let {
            val imgs = arrayListOf<String>()
            for (i in 0 until it.size){
                imgs.add(it[i].image)
            }
            banner?.setBannerStyle(BannerConfig.CIRCLE_INDICATOR) //显示数字指示器
                ?.setIndicatorGravity(BannerConfig.LEFT)//指示器居右
                ?.setImageLoader(GlideImageLoader(0))//设置图片加载器
                ?.setBannerAnimation(Transformer.Tablet) //设置动画效果
                ?.setDelayTime(4500)//轮播间隔
                ?.isAutoPlay(true)
                ?.setImages(imgs)
                ?.setOnBannerListener(object : OnBannerListener{
                    override fun OnBannerClick(position: Int) {

                    }
                })?.start()
        }
    }
    private fun setGrid(mContext: BaseActivity?, data: List<GridItemBean.Channel>){
        val size = data.size   //动态设置列数
        var column = 0
        if (size % 2 != 0) {
            val l = size / 2 as Int
            column = l + 1
        } else {
            column = size / 2 as Int
        }
        grid?.numColumns = column
        mContext?.let {
            gAdapter = MainGridAdapter(it,data)
            grid?.adapter = gAdapter
            gAdapter?.notifyDataSetChanged()
            grid?.onItemClick { p0, p1, p, p3 ->
                when(data[p].type){
                    "link" ->{
                        OneFragment.WebUrl = null
                        OneFragment.getWebLink(data[p].channel_id, mContext)
                    }
                    "goods" ->{
                        val intent = Intent(mContext, PromotionActivity::class.java)
                        intent.putExtra("id", data[p].channel_id)
                        intent.putExtra("idName","channel_id")
                        BaseActivity.goStartActivity(mContext, intent)
                    }
                    "zy" ->{
                        BaseActivity.goStartActivity(mContext, TopupActivity())
                    }
                    else ->{
                        ToastUtil.showToast(mContext, "不存在的类型:" + data[p].type)
                    }
                }
            }
        }
    }
    //获取地址列表
    private fun getHome(vararg tag:String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.HOME,
            NewParameter.getHomeMap(),
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
                                mContext!!,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val data: HomeBean.HomeBeans =
                                Gson().fromJson(t, HomeBean.HomeBeans::class.java)
                            setUiData(data)
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "Home列表请求完成")
                    if(!tag.isEmpty()){
                        //刷新完成取消刷新动画
                        refresh?.setRefreshing(false)
                    }else{
                        mContext?.dismissProgressDialog()
                    }
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "Home列表请求失败: ${e.toString()}")
                }
            })
    }
    override fun onDestroy() {
        super.onDestroy()
        hand.removeCallbacksAndMessages(null)
    }
}