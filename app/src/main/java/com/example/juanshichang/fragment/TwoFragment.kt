package com.example.juanshichang.fragment


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ClassifyListAdpater
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseFragment
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.TabOneBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import org.json.JSONObject
import rx.Subscriber

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
    private var mRecycler: RecyclerView? = null
    private var leftSelect:Int = 0
    override fun getLayoutId(): Int {
        return  R.layout.fragment_two
    }

    override fun initViews(savedInstanceState: Bundle) {
        mTSwipeRefreshLayout = mBaseView?.findViewById<SwipeRefreshLayout>(R.id.mTSwipeRefreshLayout)
        mList = mBaseView?.findViewById<ListView>(R.id.mList)
        mRecycler = mBaseView?.findViewById<RecyclerView>(R.id.mRecycler)
        listData = ArrayList()
    }

    override fun initData() {
        mTSwipeRefreshLayout?.setOnRefreshListener(this)
        getOneT(0)
    }

    override fun onResume() {
        super.onResume()
        mList?.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, v: View?, position: Int, id: Long) {
                leftSelect = position
                mLA?.setSelect(leftSelect)
                //因为选中左侧的菜单后背景颜色会变，所以每次点击都要刷新一下
                mLA?.notifyDataSetChanged()
                //todo 这里建立请求
            }
        })
    }
    //下拉刷新
    override fun onRefresh() {
        getOneT(0)  //todo 简单设置 待优化
        //刷新完成取消刷新动画
        mTSwipeRefreshLayout?.setRefreshing(false)
    }
    //网络请求
    private fun getOneT(parent_id:Int){
        HttpManager.getInstance().post(
            Api.CATEGORY,
            Parameter.getTabData(parent_id),object : Subscriber<String>() {
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
                                    mLA = ClassifyListAdpater(mContext!!, listData!!)
                                    mList?.adapter = mLA
//                                    mLA?.notifyDataSetChanged()
                                }
                            })
                        }
                    }
                }

                override fun onCompleted() {
                    Log.e("onCompleted", "Tab加载完成!")
                }

                override fun onError(e: Throwable?) {
                    Log.e("onError", "Tab加载失败!"+e)
                }
            })
    }
}
