package com.example.juanshichang.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.OnClick
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.juanshichang.MyApp

import com.example.juanshichang.R
import com.example.juanshichang.activity.PromotionActivity
import com.example.juanshichang.activity.SearcheActivity
import com.example.juanshichang.activity.WebActivity
import com.example.juanshichang.adapter.HomeAdapter
import com.example.juanshichang.adapter.MainGridAdapter
import com.example.juanshichang.adapter.MainRecyclerAdapter
import com.example.juanshichang.base.*
import com.example.juanshichang.bean.*
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.GlideImageLoader
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.fragment_one.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk27.coroutines.onItemClick
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:52
 * @文件作用: 首页
 */
class OneFragment : BaseFragment() {
    /* override fun onCreateView(
         inflater: LayoutInflater, container: ViewGroup?,
         savedInstanceState: Bundle?
     ): View? {
         // Inflate the layout for this fragment
         return inflater.inflate(R.layout.fragment_one, container, false)
     }*/
    var next: Int = 1
    var rvData = mutableListOf<MainRecyclerBean.Theme>()
    var rcAdapter: MainRecyclerAdapter? = null
    var mainList = arrayListOf<HomeEntity>()
    var homeAdapter: HomeAdapter? = null
    var hr: RecyclerView? = null
    var bHome: HomeEntity? = null
    var gHome: HomeEntity? = null
    var rHome: HomeEntity? = null
    var handler:Handler = object :Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 1){
                if(b != 1 && g != 1 && r != 1){
                    mainList.add(bHome!!)
                    mainList.add(gHome!!)
                    mainList.add(rHome!!)
                    homeAdapter?.setNewData(mainList as List<MultiItemEntity>?)
                    onCreate(null)
                }else{
                    this.sendEmptyMessageDelayed(1,100)
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
    }

    override fun initData() {
        hr = mBaseView?.findViewById<RecyclerView>(R.id.home_recycler)
        homeAdapter = HomeAdapter(mainList)
        getBanner()
        getGrid()
        getRecycler(2, next)
        handler.sendEmptyMessageDelayed(1,100) //延时 100 ms
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
        main_banner.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        main_banner.stopAutoPlay()
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
                        this@OneFragment.mContext?.runOnUiThread(Runnable {
                            setBanner(bannerList)
                        })
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
                            rHome = setRv2(recyclerList)
                            rvData.addAll(recyclerList)
                            this@OneFragment.mContext?.runOnUiThread(Runnable {
                                if (next == 1) {
                                    setRv()
                                }
                                rcAdapter?.notifyDataSetChanged()
                            })
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

    private fun setRv() {
        val lm: LinearLayoutManager = LinearLayoutManager(mContext!!, RecyclerView.VERTICAL, false)
        main_recycler.layoutManager = lm
        rcAdapter = MainRecyclerAdapter(R.layout.item_main_recycler, rvData, mContext!!)
        main_recycler.adapter = rcAdapter
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
                        this@OneFragment.mContext?.runOnUiThread(Runnable {
                            setGrid(gridList)
                        })
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
        public fun getWebLink(channel_id: Long, context: Context) {
            HttpManager.getInstance()
                .post(Api.CHANNEL, Parameter.getGridClickMap(channel_id, 0, 20), object : Subscriber<String>() {
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


    //    var gridLists: MutableList<GridItemBean.Channel> = mutableListOf()
//    var gridAdapter: MainGridAdapter? = null
    private fun setGrid(gridList: List<GridItemBean.Channel>) {
        var gridAdapter = MainGridAdapter(this.mContext!!, gridList)
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
        }
    }


    var imgs: MutableList<MainBannerBean.Banner>? = null
    private fun setBanner(bannerList: List<MainBannerBean.Banner>) {
        //随机数
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
        main_banner.start()
    }


    var b = 1
    var g = 1
    var r = 1
    private fun setRv2(recyclerList: List<MainRecyclerBean.Theme>): HomeEntity {
        if (recyclerList != null && recyclerList.size > 0) {
            var entity = HomeEntity(HomeEntity.TYPE_RECYCLER)
            for (index in 0 until recyclerList.size) {
                val r = recyclerList[index]
                if (!entity.recyclers!!.contains(r)) {
                    entity.recyclers!!.add(r)
                }
            }
//            if(mainList.size >= 3){
//                mainList.removeAt(2)
//            }
//            mainList.add(2, entity)
//            homeAdapter?.setNewData(mainList as List<MultiItemEntity>?)
            r = 2
            return entity
        }
        return HomeEntity()
    }

    private fun setGrid2(gridList: List<GridItemBean.Channel>): HomeEntity {
        if (gridList != null && gridList.size > 0) {
            var entity = HomeEntity(HomeEntity.TYPE_GRID)
            for (index in 0 until gridList.size) {
                val g = gridList[index]
                if (!entity.grids!!.contains(g)) {
                    entity.grids!!.add(g)
                }
            }
//            if(mainList.size >= 2) {
//                mainList.removeAt(1)
//            }
//            mainList.add(1, entity)
//            homeAdapter?.setNewData(mainList as List<MultiItemEntity>?)
//            homeAdapter?.notifyDataSetChanged()
            g = 2
            return entity
        }
        return HomeEntity()
    }

    private fun setBanner2(bannerList: List<MainBannerBean.Banner>): HomeEntity {
        if (bannerList != null && bannerList.size > 0) {
            var entity = HomeEntity(HomeEntity.TYPE_BANNER)
            for (index in 0 until bannerList.size) {
                val b = bannerList[index]
                if (!entity.banners!!.contains(b)) {
                    entity.banners!!.add(b)
                }
            }
            if (mainList.size >= 1) {
                mainList.removeAt(0)
            }
//            mainList.add(0, entity)
//            homeAdapter?.setNewData(mainList as List<MultiItemEntity>?)
//            homeAdapter?.notifyDataSetChanged()
            b = 2
            return entity
        }
        return HomeEntity()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
