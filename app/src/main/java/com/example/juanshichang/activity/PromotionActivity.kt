package com.example.juanshichang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.PromotionListAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.BannnerDetailBean
import com.example.juanshichang.http.HttpManager
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
class PromotionActivity : BaseActivity() {
    var theme_id: Long = 0
    var goods_num:Int = 0
    val theme_id_def: Long = 0
    var adapter: PromotionListAdapter? = null
    var goodsList = mutableListOf<BannnerDetailBean.Goods>()
    override fun getContentView(): Int {
        return R.layout.activity_promotion
    }

    override fun initView() {
        if (theme_id_def != intent.getLongExtra("theme_id", 0)) {
            theme_id = intent.getLongExtra("theme_id", 0) //id
            goods_num = intent.getIntExtra("goods_num", 0) //条目数量
            val grid = GridLayoutManager(this@PromotionActivity, 2)
            adapter = PromotionListAdapter(R.layout.item_banner_pro,goodsList)
            adapter?.openLoadAnimation()//  （默认为渐显效果） 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
            adapter?.emptyView = View.inflate(this, R.layout.activity_not_null, null)
            mTypeClassView.layoutManager = grid
            mTypeClassView.adapter = adapter
            searchDetailListBanner(theme_id)
        }
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


    //从首页Banner进入的请求 传入商品列表id
    private fun searchDetailListBanner(theme_id: Long) {
        HttpManager.getInstance().post(Api.BANNERITEM, Parameter.getBannerClickMap(theme_id), object :
            Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@PromotionActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        var bannnerDetailBean = Gson().fromJson(str, BannnerDetailBean.BannnerDetailBeans::class.java)
                        val goods2:List<BannnerDetailBean.Goods> = bannnerDetailBean.data.theme_list_get_response.goods_list
                        goodsList.addAll(goods2)
                        if (null != goods2) {
                            this@PromotionActivity.runOnUiThread(object : Runnable {
                                override fun run() {
                                    adapter?.notifyDataSetChanged()
                                }
                            })
                        }
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted", "促销加载完成!")
            }

            override fun onError(e: Throwable?) {
                Log.e("onError", "促销加载失败!" + e)
            }
        })
    }
}
