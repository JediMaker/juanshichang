package com.example.juanshichang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.CargoListAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.CargoListBean
import com.example.juanshichang.bean.TabOneBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.ActivityManager
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.TabCreateUtils
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_look_all.*
import kotlinx.android.synthetic.main.fragment_one.*
import kotlinx.android.synthetic.main.fragment_two.*
import kotlinx.coroutines.Runnable
import org.json.JSONObject
import rx.Subscriber

/**
 * author:翊-yzq
 * type: LookAllActivity.kt
 * details: 这是一个查看全部的Activity....
 * create-date:2019/8/28 12:40
 */
class LookAllActivity : BaseActivity(), View.OnClickListener, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    var category_id:Int = 0
    var itemType:Int = 0
    var categoryidDef:Int = Int.MAX_VALUE
    var categoryList:ArrayList<String>? = null
    var recyclerAdapter: CargoListAdapter? = null
    var recyclerData:ArrayList<CargoListBean.Goods>? = null
    override fun getContentView(): Int {
        return R.layout.activity_look_all
    }
    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this, R.color.white)
        LaRet.setOnClickListener(this)
        if(categoryidDef != intent.getIntExtra("category_id",Int.MAX_VALUE)){
            category_id = intent.getIntExtra("category_id",Int.MAX_VALUE)
            itemType = intent.getIntExtra("itemtype",Int.MAX_VALUE)  // 全部为 0
            LaRecycler.layoutManager = GridLayoutManager(this,2)
            recyclerData = ArrayList()
            recyclerAdapter = CargoListAdapter(R.layout.item_banner_pro,recyclerData)
            /**
             * 渐显 ALPHAIN
             * 缩放 SCALEIN
             * 从下到上 SLIDEIN_BOTTOM
             * 从左到右 SLIDEIN_LEFT
             * 从右到左 SLIDEIN_RIGHT
             */
            recyclerAdapter?.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
            LaRefresh.setOnRefreshListener(this)
            //当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
            recyclerAdapter?.setPreLoadNumber(2)
            recyclerAdapter?.setOnLoadMoreListener(this, LaRecycler)
            getTwoT(category_id,0)
        }else{
            finish()
            ToastUtil.showToast(this@LookAllActivity,"网络异常 请稍后重试")
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        recyclerAdapter?.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener{
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                val intent = Intent(this@LookAllActivity, ShangPinContains::class.java)
                if (recyclerData?.size!! > position) {
                    intent.putExtra("goods_id", recyclerData!![position].goods_id)
                    intent.putExtra("mall_name", recyclerData!![position].mall_name)
                    startActivity(intent)
                } else {
                    ToastUtil.showToast(this@LookAllActivity, "数据发生未知错误,请稍后重试!!!")
                    finish()
                }
            }
        })
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.LaRet->{ //返回
                finish()
            }
        }
    }
    //加载更多
    override fun onLoadMoreRequested() {
        val oldSize:Int = recyclerData?.size!!
        page++
        cargoList(page,mRequestId)
        LaRecycler.postDelayed(object : Runnable{
            override fun run() {
                if(recyclerData?.size!! - oldSize !=20){
                    recyclerAdapter?.loadMoreEnd()
                }else{
                    //数据加载完成
                    recyclerAdapter?.loadMoreComplete()
                }
            }
        },1800)
    }
    //刷新
    override fun onRefresh() {
        //刷新的时候禁止加载更多
        recyclerAdapter?.setEnableLoadMore(false)
        page = 1
        LaRecycler.postDelayed(object : Runnable{
            override fun run() {
                cargoList(page,mRequestId)
                //刷新完成取消刷新动画
                LaRefresh?.setRefreshing(false)
                //刷新完成重新开启加载更多
                recyclerAdapter?.setEnableLoadMore(true)
            }
        },1800)
    }

    //网络请求
    // 二级页面 请求 不需要图片
    private fun getTwoT(parent_id:Int,childItem:Int){
        HttpManager.getInstance().post(
            Api.CATEGORY,
            Parameter.getTabData(parent_id,1),object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(this@LookAllActivity, jsonObj.optString(JsonParser.JSON_MSG))
                        } else {
                            val bean = Gson().fromJson(str, TabOneBean.TabOneBeans::class.java)
                            val data = bean.data.category_list
                            if(categoryList == null){
                                categoryList = ArrayList()
                            }else{
                                categoryList?.clear()
                            }
                            LaTab?.post(object : Runnable{
                                override fun run() {
                                    setTabData(data)
                                    //todo 这里进行默认请求变更
                                    if(itemType == 0){
                                        mRequestId = parent_id
                                        cargoList(1,mRequestId)
                                    }else{
                                        mRequestId = data[itemType-1].category_id
                                        cargoList(1,data[itemType-1].category_id)
                                    }
                                }
                            })
                        }
                    }
                }

                override fun onCompleted() {
                    Log.e("onCompleted", "T - Tab2加载完成!")
                }

                override fun onError(e: Throwable?) {
                    Log.e("onError", "T - Tab2加载失败!"+e)
                }
            })
    }
    //请求recycler接口
    private fun cargoList(page: Int,category_id: Int) { //keyword: String, page: Int, page_size: Int, sort_type: Int,   参数填默认值
        HttpManager.getInstance()
            .post(Api.SEARCH, Parameter.getSearchMap("", page, 20, 0, category_id), object :
                Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        jsonObj = JSONObject(str)
                        if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(this@LookAllActivity, jsonObj.optString(JsonParser.JSON_MSG))
                        } else { //fastjson 解析
                            val cargoListBean: CargoListBean.CargoListBeans = Gson().fromJson(str, CargoListBean.CargoListBeans::class.java)
                            val goodsBean = cargoListBean.data.goods_search_response.goods_list  // 商品列表
                            if(page == 1){
                                if(recyclerData==null){
                                    recyclerData = ArrayList()
                                }else{
                                    recyclerData?.clear()
                                }
                            }
                            recyclerData?.addAll(goodsBean)
                            this@LookAllActivity.runOnUiThread {
                                if(page == 1) {
                                    LaRecycler.adapter = recyclerAdapter
                                }else{
                                    recyclerAdapter?.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    Log.e("onCompleted", "商品请求完成!")
                }

                override fun onError(e: Throwable?) {
                    Log.e("onError", "商品请求错误!" + e)
                }

            })
    }
    var mRequestId:Int = 0
    var page:Int = 1
    private fun setTabData(data: List<TabOneBean.Category>) {
        categoryList?.add("全部")
        for (i in 0 until data.size){
            categoryList?.add(data[i].name)
        }
        TabCreateUtils.setOrangeTabT(this@LookAllActivity,LaTab,categoryList,object : TabCreateUtils.onTitleClickListener {
            override fun onTitleClick(index: Int) {
                page = 1
                if(index==0){
                    LaTab.onPageSelected(0)
                    cargoList(page,category_id)
                    mRequestId = category_id
                }else{
                    LaTab.onPageSelected(index)
                    mRequestId = data[index-1].category_id
                    cargoList(page,mRequestId)
                }
            }
        })
        if(itemType!=0){
            LaTab.onPageSelected(itemType)
        }
        LaTitle.text = categoryList!![itemType]
    }
}
