package com.example.juanshichang.activity

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.PromotionListAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.BannnerDetailBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_promotion.*
import kotlinx.coroutines.Runnable
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/7/31 16:21
 * @文件作用:首页 banner 活动商品 列表
 */
class PromotionActivity : BaseActivity(),View.OnClickListener , BaseQuickAdapter.RequestLoadMoreListener,
    SwipeRefreshLayout.OnRefreshListener{
    //    var banner_id: Long = 0
    val banner_id_def: Long = Long.MAX_VALUE
    var offset: Int = 0
    var adapter: PromotionListAdapter? = null
    var goodsList = mutableListOf<BannnerDetailBean.X>()
    var type = Int.MAX_VALUE
    var id:Long = 0
    override fun getContentView(): Int {
        return R.layout.activity_promotion
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this, R.color.white)
        proRet.setOnClickListener(this)
        if (banner_id_def != intent.getLongExtra("id", 0) && null != intent.getStringExtra("idName")) {
            id = intent.getLongExtra("id", 0)
            val idName = intent.getStringExtra("idName").toString().trim()  //idName 仅供参考  后面请求数据 有 type 为约束
            when (idName) {
                "banner_id" -> {
                    type = 1
                }
                "channel_id" -> {
                    type = 2
                }
                "theme_id" -> {
                    type = 3
                }
                else -> {
                    ToastUtil.showToast(this@PromotionActivity, "数据加载错误 请稍后重试!!!")
                    finish()
                }
            }
            val grid = GridLayoutManager(this@PromotionActivity, 2)
            adapter = PromotionListAdapter(R.layout.item_banner_pro, goodsList)
            adapter?.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)//  （默认为渐显效果） 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
            adapter?.setOnLoadMoreListener(this,mTypeClassView) //开启加载更多
            mSwipeRefresh.setOnRefreshListener(this) //设置下拉刷新
            adapter?.emptyView = View.inflate(this, R.layout.activity_not_null, null)
            mTypeClassView.layoutManager = grid
            mTypeClassView.adapter = adapter
            showProgressDialog()
            searchDetailListBanner(id, type,offset)
        } else {
            ToastUtil.showToast(this@PromotionActivity, "数据异常 请稍后重试!!!")
            Log.i("shopping","异常地址："+intent.getLongExtra("id", 0))
            finish()
        }
        /*if (banner_id_def != intent.getLongExtra("banner_id", 0)) {
            val banner_id = intent.getLongExtra("banner_id", 0) //id
            val grid = GridLayoutManager(this@PromotionActivity, 2)
            adapter = PromotionListAdapter(R.layout.item_banner_pro, goodsList)
            adapter?.openLoadAnimation()//  （默认为渐显效果） 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
            adapter?.emptyView = View.inflate(this, R.layout.activity_not_null, null)
            mTypeClassView.layoutManager = grid
            mTypeClassView.adapter = adapter
            searchDetailListBanner(banner_id,1)
        } else if (banner_id_def != intent.getLongExtra("channel_id", 0)) {
            val channel_id = intent.getLongExtra("channel_id", 0) //id

            val grid = GridLayoutManager(this@PromotionActivity, 2)
            adapter = PromotionListAdapter(R.layout.item_banner_pro, goodsList)
            adapter?.openLoadAnimation()//  （默认为渐显效果） 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
            adapter?.emptyView = View.inflate(this, R.layout.activity_not_null, null)
            mTypeClassView.layoutManager = grid
            mTypeClassView.adapter = adapter
            searchDetailListBanner(channel_id,2)
        }else{
            ToastUtil.showToast(this@PromotionActivity,"数据异常 请稍后重试!!!")
            finish()
        }*/
    }

    override fun initData() {
        adapter?.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener { // item 点击事件
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                var intent = Intent(this@PromotionActivity, ShangPinContains::class.java)
                if (goodsList.size > position) {
                    intent.putExtra("goods_id", goodsList[position].goods_id)
                    intent.putExtra("mall_name", goodsList[position].mall_name)
                    startActivity(intent)
                } else {
                    ToastUtil.showToast(this@PromotionActivity, "数据发生未知错误,请稍后重试!!!")
                    finish()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.proRet ->{
                finish()
            }
        }
    }
    override fun onLoadMoreRequested() {
        offset++
        mTypeClassView.postDelayed(object:Runnable{
            override fun run() {
                searchDetailListBanner(id, type,offset)
            }
        },800)
    }

    override fun onRefresh() {
        offset = 0
        adapter?.setEnableLoadMore(false) //刷新的时候禁止加载更多
        mTypeClassView.postDelayed(object:Runnable{
            override fun run() {
                searchDetailListBanner(id, type,offset)
                //刷新完成取消刷新动画
                mSwipeRefresh?.setRefreshing(false)
                //刷新完成重新开启加载更多
                adapter?.setEnableLoadMore(true)
            }
        },800)
    }
    //从首页Banner进入的请求 传入商品列表id
    private fun searchDetailListBanner(banner_id: Long, type: Int,offset:Int) { //Banner 为 1,graid 为 2 ,recycler大图 为3
        var url: String? = null
        var map: HashMap<String, String>? = null
        when (type) {
            1 -> {
                url = Api.BANNERITEM
//            map = Parameter.getBannerClickMap(banner_id, 0, 20)
                map = Parameter.getBaseSonMap("banner_id", banner_id, offset, 20)
            }
            2 -> {
                url = Api.CHANNEL
                // channel_id
//            map = Parameter.getGridClickMap(banner_id, 0, 20) //之前共用一个请求体
                map = Parameter.getBaseSonMap("channel_id", banner_id, offset, 20)
            }
            3 -> {
                url = Api.THEME
                map = Parameter.getBaseSonMap("theme_id",banner_id,offset,20)
            }
        }
        HttpManager.getInstance().post(url, map, object :
            Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@PromotionActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        var bannnerDetailBean = Gson().fromJson(str, BannnerDetailBean.BannnerDetailBeans::class.java)
                        val goods2: List<BannnerDetailBean.X> = bannnerDetailBean.data.list
                        if(goods2.size!=0){
                            if(offset == 0){
                                if(goodsList.size!=0){
                                    goodsList.clear()
                                }
                                goodsList.addAll(goods2)
                                this@PromotionActivity.runOnUiThread(object : Runnable {
                                    override fun run() {
                                        if(type == 1){
                                            proTitle.text = bannnerDetailBean.data.banner_name
                                        }
                                        if(type == 2){
                                            proTitle.text = bannnerDetailBean.data.channel_name
                                        }
                                        if(type == 3){ //THEME
                                            proTitle.text = bannnerDetailBean.data.theme_name
                                        }
                                        if(goods2.size!=20){
                                            adapter?.loadMoreEnd()//数据加载完毕
                                        }
                                        adapter?.setNewData(goodsList)
                                    }
                                })
                            }else{
                                goodsList.addAll(goods2)
                                this@PromotionActivity.runOnUiThread(object : Runnable {
                                    override fun run() {
                                        if(goods2.size!=20){
                                            adapter?.loadMoreEnd()//数据加载完毕
                                        }else{
                                            adapter?.loadMoreComplete()//数据加载完成
                                        }
                                        adapter?.setNewData(goodsList)
                                    }
                                })
                            }
                        }else{
                            adapter?.loadMoreEnd()//数据加载完毕
                        }
                    }
                }
            }

            override fun onCompleted() {
                LogTool.e("onCompleted", "促销加载完成!")
                this@PromotionActivity.runOnUiThread(object : Runnable {
                    override fun run() {
                        dismissProgressDialog()
                    }
                })
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onError", "促销加载失败!" + e)
            }
        })
    }
}
