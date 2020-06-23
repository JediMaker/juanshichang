package com.example.juanshichang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ZySearchAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.TopUpllBean
import com.example.juanshichang.bean.ZySearchBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastTool
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_seek_bar.*
import kotlinx.android.synthetic.main.activity_topup.*
import kotlinx.android.synthetic.main.activity_zy_search.*
import org.json.JSONObject
import rx.Subscriber

class ZySearchActivity : BaseActivity(), View.OnClickListener {
    private var allData: ZySearchBean.ZySearchBeans? = null
    private var listData: ArrayList<ZySearchBean.Product>? = null
    private var keySearch: String = ""
    private var sAdapter: ZySearchAdapter? = null
    override fun getContentView(): Int {
        return R.layout.activity_zy_search
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this, R.color.colorPrimary)
        if (null != getIntent().getStringExtra("search")) {
            keySearch = getIntent().getStringExtra("search")
            etsearch.setText(keySearch)
            getSearchData(keySearch)
        } else {
            ToastUtil.showToast(this@ZySearchActivity, "哎呀  出错啦!!!")
            finish()
        }
    }

    override fun initData() {
        if (!TextUtils.isEmpty(keySearch)) {
            sAdapter = ZySearchAdapter()
            sAdapter?.openLoadAnimation()
            zySRec.adapter = sAdapter
        }
        mbackLayout.setOnClickListener(this)
        etsearch.setOnClickListener(this)
        mSearchBt.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        sAdapter?.setOnItemClickListener { adapter, view, position ->
            listData?.let {
                val intent = Intent(this@ZySearchActivity, ShangPinZyContains::class.java)
                intent.putExtra("product_id", it[position].product_id)
                startActivity(intent)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            mbackLayout -> {//返回
                finish()
            }
            etsearch -> {//搜索框
//                ToastUtil.showToast(this, "")
            }
            mSearchBt -> {//搜索按钮
                val str = getEditText()
                if (!TextUtils.isEmpty(str) && !TextUtils.equals(str,keySearch)) {
                    keySearch = str
                    getSearchData(keySearch)
                } else {
//                    ToastTool.showToast(this@ZySearchActivity, "你想找啥？？？")
                }

            }
        }
    }

    private fun getEditText(): String {//获取Edit数据
        val text = etsearch.text.toString().trim()
        if (text.length > 0 && !TextUtils.isEmpty(text)) {
            return text
        }
        return ""
    }

    //自营商品搜索
    private fun getSearchData(search: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL)
            .post(Api.NEWAEARCH, NewParameter.getSearchMap(search), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str =result?.substring(result?.indexOf("{"),result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        val jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(
                                this@ZySearchActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            allData = Gson().fromJson(str, ZySearchBean.ZySearchBeans::class.java)
                            allData?.let {
                                listData = it.data.products as ArrayList
                                sAdapter?.setNewData(listData)
                                sAdapter?.emptyView = View.inflate(
                                    this@ZySearchActivity,
                                    R.layout.activity_not_null,
                                    null
                                )
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "自营商品搜索 完成")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "自营商品搜索 Error")
                }
            })
    }
}
