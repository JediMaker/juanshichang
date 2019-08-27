package com.example.juanshichang.fragment


import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ClassifyListAdpater
import com.example.juanshichang.adapter.TwoRecyclerAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseFragment
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.TabOneBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import kotlinx.coroutines.Runnable
import org.json.JSONObject
import rx.Subscriber
import java.util.*
import kotlin.collections.ArrayList

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:53
 * @文件作用: 学院页面
 */
class TwoFragment : BaseFragment(),SwipeRefreshLayout.OnRefreshListener{
    var mTSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mList: ListView? = null
    private var mLA: ClassifyListAdpater? = null
    private var listData:ArrayList<TabOneBean.Category>? = null
    private var recyclerData:ArrayList<TabOneBean.Data>? = null
    private var mRecycler: RecyclerView? = null
    private var mRA:TwoRecyclerAdapter? = null
    private var leftSelect:Int = 0
    override fun getLayoutId(): Int {
        return  R.layout.fragment_two
    }

    override fun initViews(savedInstanceState: Bundle) {
        mTSwipeRefreshLayout = mBaseView?.findViewById<SwipeRefreshLayout>(R.id.mTSwipeRefreshLayout)
        mList = mBaseView?.findViewById<ListView>(R.id.mList)
        listData = ArrayList()
        mLA = ClassifyListAdpater(mContext!!)
        mList?.adapter = mLA
        mRecycler = mBaseView?.findViewById<RecyclerView>(R.id.mRecycler)
        recyclerData = ArrayList()
        mRA = TwoRecyclerAdapter()
        /**
         * 渐显 ALPHAIN
         * 缩放 SCALEIN
         * 从下到上 SLIDEIN_BOTTOM
         * 从左到右 SLIDEIN_LEFT
         * 从右到左 SLIDEIN_RIGHT
         */
        mRA?.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
        val lm = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        mRecycler?.layoutManager = lm
        mRecycler?.adapter = mRA
    }

    override fun initData() {
        mTSwipeRefreshLayout?.setOnRefreshListener(this)
        getOneT(0,leftSelect)
    }

    override fun onResume() {
        super.onResume()
        mList?.setOnScrollListener(object : AbsListView.OnScrollListener{
            override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {

            }
            //用于解决 ListView 与 滑动 冲突
            override fun onScrollStateChanged(v: AbsListView?, p: Int) {
                if(v?.childCount!! > 0 && v.firstVisiblePosition == 0 && v.getChildAt(0).top >= v.paddingTop){
                    //判断 已经到达顶部
                    mTSwipeRefreshLayout?.isEnabled = true
                }else{
                    mTSwipeRefreshLayout?.isEnabled = false
                }
            }
        })
        mList?.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, v: View?, position: Int, id: Long) {
                if(position!=leftSelect){
                    leftSelect = position
                    //网络请求
                    if(listData!=null && listData!!.size > position){
                        getTwoT(listData!![position].category_id)
                    }else{
                        ToastUtil.showToast(mContext!!,"数据错误 请刷新后重试")
                    }
                    mLA?.setSelect(leftSelect)
                    //因为选中左侧的菜单后背景颜色会变，所以每次点击都要刷新一下
                    mLA?.notifyDataSetChanged()
                }else{
                    goRefresh()
                }
            }
        })
        //耳机列表点击事件
        mRA?.setOnItemChildClickListener(object : BaseQuickAdapter.OnItemChildClickListener{
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                when(view?.id){
                    R.id.twoLowAllR->{
                        ToastUtil.showToast(mContext!!,"查看全部："+listData!![leftSelect].name)
                    }
                }
            }
        })
    }
    //下拉刷新
    override fun onRefresh() {
        mList?.postDelayed(object : Runnable{
            override fun run() {
                getOneT(0,leftSelect)  //todo 简单设置 待优化
                //刷新完成取消刷新动画
                mTSwipeRefreshLayout?.setRefreshing(false)
            }
        },1000)
    }

    //计时 刷新器
    var isRefresh = false
    private fun goRefresh(){
        var tRe: Timer? = null
        if(!isRefresh){
            isRefresh = true
            tRe = Timer()
            tRe.schedule(object : TimerTask(){
                override fun run() {
                    isRefresh = false
                }
            },2000)
            ToastUtil.showToast(mContext!!,"双击刷新 当前条目")
        }else{
            onRefresh() // 调用刷新
            isRefresh = false
        }
    }
    //网络请求
    private fun getOneT(parent_id:Int,leftSelect:Int){
        HttpManager.getInstance().post(
            Api.CATEGORY,
            Parameter.getTabData(parent_id,0),object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(mContext!!, jsonObj.optString(JsonParser.JSON_MSG))
                        } else {
                            val data = Gson().fromJson(str, TabOneBean.TabOneBeans::class.java)
                            val list = data.data.category_list
                            if(list.size!=0){
                                if(listData?.size != 0){
                                    listData?.clear()
                                }
                                listData?.addAll(list)
                            }
                            mList?.post(object : Runnable{
                                override fun run() {
                                    if(listData!=null && listData?.size != 0){
                                        mLA?.setDatas(listData!!)
                                    }
                                    if(listData?.size != 0){
                                        getTwoT(listData!![leftSelect].category_id)
                                    }
//                                    mLA?.notifyDataSetChanged()
                                }
                            })
                        }
                    }
                }

                override fun onCompleted() {
                    Log.e("onCompleted", "T - Tab加载完成!")
                }

                override fun onError(e: Throwable?) {
                    Log.e("onError", "T - Tab加载失败!"+e)
                }
            })
    }
    // 二级页面 请求
    fun getTwoT(parent_id:Int){
        HttpManager.getInstance().post(Api.CATEGORY,Parameter.getTabData(parent_id,1),object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject = JSONObject(str)
                    if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(mContext!!, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val bean = Gson().fromJson(str, TabOneBean.TabOneBeans::class.java)
                        val data = bean.data
                        if(recyclerData == null){
                            recyclerData = ArrayList()
                        }
                        recyclerData?.clear()
                        mRecycler?.post(object : Runnable{
                            override fun run() {
                                recyclerData?.add(data)
                                mRA?.setNewData(recyclerData)
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
}
