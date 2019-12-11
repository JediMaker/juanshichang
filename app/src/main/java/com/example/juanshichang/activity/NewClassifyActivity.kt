package com.example.juanshichang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.NewCLeftAdapter
import com.example.juanshichang.adapter.NewCRightAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.ConOrderBean
import com.example.juanshichang.bean.NewClassifyBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_new_classify.*
import kotlinx.coroutines.Runnable
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/12/7 16:48
 * @文件作用: 自营商品列表
 * todo 在Manifrsts文件中删除了 该 activity的注册....
 */
class NewClassifyActivity : BaseActivity(), View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener {
    private var leftAdapter: NewCLeftAdapter? = null
    private var leftData: ArrayList<NewClassifyBean.Data>? = null
    private var rightAdapter: NewCRightAdapter? = null
    private var rigData: ArrayList<NewClassifyBean.Data>? = null
    override fun getContentView(): Int {
        return R.layout.activity_new_classify
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@NewClassifyActivity, R.color.white)
    }

    override fun initData() {
        mNSwipeRefreshLayout.setOnRefreshListener(this)
        leftAdapter = NewCLeftAdapter(this@NewClassifyActivity)
        mList.adapter = leftAdapter
        rightAdapter = NewCRightAdapter()
        /**
         * 渐显 ALPHAIN
         * 缩放 SCALEIN
         * 从下到上 SLIDEIN_BOTTOM
         * 从左到右 SLIDEIN_LEFT
         * 从右到左 SLIDEIN_RIGHT
         */
        rightAdapter?.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
        mRecycler.adapter = rightAdapter
        reqCateFat("0")
    }

    override fun onClick(v: View?) {

    }

    override fun onRefresh() { //刷新
        reqCateFat("0")
        mList?.postDelayed(object : Runnable {
            override fun run() {
                //刷新完成取消刷新动画
                mNSwipeRefreshLayout?.setRefreshing(false)
            }
        }, 300)
    }

    override fun onResume() {
        super.onResume()
        mList?.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != leftAdapter?.getSelect()) {
                    showProgressDialog()
                    reqCateFat(leftData!![position].category_id)
                    leftAdapter?.setSelect(position)
                }
            }
        })
        rightAdapter?.setOnItemClickListener(object:BaseQuickAdapter.OnItemClickListener{
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                rigData?.let {
                    val intent = Intent(this@NewClassifyActivity,ZyAllActivity::class.java)
                    intent.putExtra("category_id",it[position].category_id)
                    startActivity(intent)
                }
            }
        })
        //解决刷新 冲突
        mList?.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {

            }

            //用于解决 ListView 与 滑动 冲突
            override fun onScrollStateChanged(v: AbsListView?, p: Int) {
                if (v?.childCount!! > 0 && v.firstVisiblePosition == 0 && v.getChildAt(0).top >= v.paddingTop) {
                    //判断 已经到达顶部
                    mNSwipeRefreshLayout?.isEnabled = true
                } else {
                    mNSwipeRefreshLayout?.isEnabled = false
                }
            }
        })
    }

    //一/N级列表请求
    private fun reqCateFat(parent_category_id: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.NEWCATEGORY,
            NewParameter.getNewClassMap(parent_category_id),
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
                                this@NewClassifyActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val data =
                                Gson().fromJson(t, NewClassifyBean.NewClassifyBeans::class.java)
                            if (parent_category_id == "0") { //一级列表
                                leftData = data.data as ArrayList<NewClassifyBean.Data>
                                leftAdapter?.setNewData(leftData!!)
                                if (leftData?.size != 0) { //直接唤起第一个子页面
                                    reqCateFat(leftData!![0].category_id)
                                }
                            } else { //赋予子列表
                                rigData = data.data as ArrayList<NewClassifyBean.Data>
                                rightAdapter?.setNewData(rigData)
                                //设置 空数据view
                                rightAdapter?.emptyView = View.inflate(
                                    this@NewClassifyActivity,
                                    R.layout.activity_not_null,
                                    null
                                )
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    if (parent_category_id == "0") {
                        LogTool.e("onCompleted", "一级列表请求完成")
                    } else {
                        LogTool.e("onCompleted", "N级列表请求完成")
                    }
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "订单详情请求失败: ${e.toString()}")
                }
            })
    }

}
