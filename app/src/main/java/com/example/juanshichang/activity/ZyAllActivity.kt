package com.example.juanshichang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ZyAllAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.ZyAllBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_zy_all.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

class ZyAllActivity : BaseActivity(), View.OnClickListener {
    private var category_id: String? = null
    private var zyAdapter: ZyAllAdapter? = null
    private var zyData: List<ZyAllBean.Product>? = null
    override fun getContentView(): Int {
        return R.layout.activity_zy_all
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@ZyAllActivity, R.color.white)
        if (intent.getStringExtra("category_id").isNotEmpty()) {
            category_id = intent.getStringExtra("category_id")
        } else {
            finish()
        }
    }

    override fun initData() {
        if (category_id != null) {
            zyAdapter = ZyAllAdapter()
            zyAdapter?.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
            zyRecycler.adapter = zyAdapter
            showProgressDialog()
            reqCateSon(category_id!!)
        }
        zyRet.setOnClickListener(this)
        zyAdapter?.setOnItemClickListener { adapter, view, position ->
            zyData?.let {
                val intent = Intent(this@ZyAllActivity,ShangPinZyContains::class.java)
                intent.putExtra("product_id",it[position].product_id)
                startActivity(intent)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.zyRet -> {
                finish()
            }
        }
    }

    //商品请求
    //一/N级列表请求
    private fun reqCateSon(category_id: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.NEWCATEGORYCON,
            NewParameter.getNewCGoodMap(category_id),
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
                                this@ZyAllActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val data = Gson().fromJson(t, ZyAllBean.ZyAllBeans::class.java)
                            zyData = data.data.products
                            zyAdapter?.setNewData(zyData)
                            zyAdapter?.emptyView = View.inflate(
                                this@ZyAllActivity,
                                R.layout.activity_not_null,
                                null
                            )
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "列表详情请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "列表详情请求失败: ${e.toString()}")
                }
            })
    }
}