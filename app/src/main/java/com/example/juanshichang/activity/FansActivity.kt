package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juanshichang.R
import com.example.juanshichang.adapter.FansAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.FansBean
import com.example.juanshichang.bean.OrdersBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastTool
import com.example.juanshichang.utils.ToastUtil
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.activity_fans.*
import org.json.JSONObject
import rx.Subscriber

class FansActivity : BaseActivity(), View.OnClickListener {
    private var  fansListData:ArrayList<FansBean.Data>? = null
    private var  fansAdapter:FansAdapter? = null
    private val handler: Handler = object : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                0 ->{
                    fansAdapter?.setNewData(fansListData)
                    ToastUtil.showToast(this@FansActivity,""+fansListData?.size)
                }
            }
        }
    }
    override fun getContentView(): Int {
        return R.layout.activity_fans
    }

    override fun initView() {
        fansTop.setPadding(0, QMUIStatusBarHelper.getStatusbarHeight(this),0,0)
        mbackLayout.setOnClickListener(this)
        mSearchBt.setOnClickListener(this)
        setTab()
    }
    override fun initData() {
        getFans(0,20)
    }

    override fun onClick(v: View?) {
        when(v){
            mbackLayout ->{
                finish()
            }
            mSearchBt ->{
                //搜索
            }
        }
    }
    private fun setTab() {
        fansTab.addTab(fansTab.newTab().setText("全部"))
        fansTab.addTab(fansTab.newTab().setText("直荐"))
        fansTab.addTab(fansTab.newTab().setText("推荐"))
        fansTab.addOnTabSelectedListener(mTabLayoutBottom)
        fansListData = ArrayList()
        fansList.layoutManager = LinearLayoutManager(this@FansActivity, RecyclerView.VERTICAL,false)
        fansAdapter = FansAdapter(R.layout.item_fans)
        fansAdapter?.emptyView = View.inflate(this, R.layout.activity_not_null, null)
        fansList.adapter = fansAdapter
    }
    private val mTabLayoutBottom = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {

        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
        }

        override fun onTabSelected(t: TabLayout.Tab?) {
            ToastTool.showToast(this@FansActivity,"走到了"+t?.position)
        }
    }

    private fun getFans(offset:Int,limit:Int){
        HttpManager.getInstance().post(Api.FANS, Parameter.getOrders(offset,limit),object : Subscriber<String>(){
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@FansActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else{
                        val fansList: FansBean.FansBeans= Gson().fromJson(str,FansBean.FansBeans::class.java)
                        if (fansListData != null && fansListData?.size != 0){
                            fansListData?.clear()
                        }
                        fansListData?.addAll(fansList.data)
                        handler.sendEmptyMessage(0)
                    }
                }
            }

            override fun onCompleted() {
                LogTool.e("onCompleted", "订单加载完成!")
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onError", "订单加载失败!" + e)
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
