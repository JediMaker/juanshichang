package com.example.juanshichang.activity

import android.content.Intent
import android.content.Intent.getIntent
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.CargoListAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.CLB
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_class_type.*
import kotlinx.android.synthetic.main.activity_seek_bar.*
import kotlinx.android.synthetic.main.fragment_one.*
import kotlinx.coroutines.Runnable
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/7/24 16:14
 * @文件作用: 商品分类
 */
class ClassTypeActivity : BaseActivity(), View.OnClickListener {
    var keyWord: String = ""
    var adapter: CargoListAdapter? = null
    var page: Int = 1
    var goodsList = mutableListOf<CLB.Goods>()
    override fun getContentView(): Int {
        return R.layout.activity_class_type
    }

    override fun initView() {
        if (null != getIntent().getStringExtra("keyword")) {
            keyWord = intent.getStringExtra("keyword")
            etsearch.setText(keyWord.toCharArray(), 0, keyWord.length)
            val grid = GridLayoutManager(this@ClassTypeActivity, 2)
            adapter = CargoListAdapter(R.layout.item_type, goodsList, this@ClassTypeActivity)
            adapter?.openLoadAnimation()//  （默认为渐显效果） 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
            adapter?.emptyView = View.inflate(this, R.layout.activity_not_null, null)
            adapter?.setOnLoadMoreListener(object : BaseQuickAdapter.RequestLoadMoreListener {
                override fun onLoadMoreRequested() {
                    mTypeClassView.post(object : Runnable {
                        override fun run() {
                            if (goodsList.size % 20 != 0) {
                                adapter?.loadMoreEnd() //不再有数据
                            } else {
                                val str = getEditText()
                                page++
                                cargoList(Api.Pdd, str, page)
                                adapter?.loadMoreComplete()
                            }
                        }
                    })
                }
            }, mTypeClassView)
            mTypeClassView.layoutManager = grid
            mTypeClassView.adapter = adapter
            cargoList(Api.Pdd, keyWord.trim(), page)
        } else {
            ToastUtil.showToast(this@ClassTypeActivity, "哎呀  出错啦!!!")
            finish()
        }
        etsearch.setOnClickListener(this)
        mbackLayout.setOnClickListener(this)
        mSearchBt.setOnClickListener(this)
    }

    override fun initData() {
        adapter?.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener { // item 点击事件
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                var intent = Intent(this@ClassTypeActivity, ShangPinContains::class.java)
                if (goodsList.size > position) {
                    intent.putExtra("goods_id", goodsList[position].goods_id)
                    intent.putExtra("mall_name", goodsList[position].mall_name)
                    startActivity(intent)
                } else {
                    ToastUtil.showToast(this@ClassTypeActivity, "数据发生未知错误,请稍后重试!!!")
                    finish()
                }
            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mbackLayout -> {//返回
                finish()
            }
            etsearch -> {//搜索框
//                ToastUtil.showToast(this@ClassTypeActivity, "")
            }
            mSearchBt -> {//搜索按钮
                val str = getEditText()
                if (!TextUtils.isEmpty(str)) {
                    page = 1
                    cargoList(Api.Pdd, str, page)
                } else {
                    ToastUtil.showToast(this@ClassTypeActivity, "大侠 你想找什么")
                }

            }
        }
    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_class_type)
//    }
    /**
     *  商品搜索
     */
    private fun cargoList(servicer: String, keyword: String, page: Int) {
        HttpManager.getInstance().post(Api.SEARCH, Parameter.getSearchMap(servicer, keyword, page), object :
            Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@ClassTypeActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else { //fastjson 解析
//                        val cargoListBean:CargoListBean = JSON.parseObject(str,CargoListBean::class.java)
                        val cargoListBean: CLB.CargoListBean = Gson().fromJson(str, CLB.CargoListBean::class.java)
                        val goodsBean = cargoListBean.data.goods_search_response.goods_list  // 商品列表
                        if (page == 1) {
                            goodsList.clear()
//                            if (TextUtils.isEmpty(goodsBean.toString()) || goodsBean.size <= 0) {
//                                mTypeClassView.visibility = View.GONE
//                                mNotTypeClassNull.visibility = View.VISIBLE
//                            } else {
//                                mNotTypeClassNull.visibility = View.GONE
//                                mTypeClassView.visibility = View.VISIBLE
//                            }
                            if (goodsList.size % 20 == 0) {
                                adapter?.setEnableLoadMore(true)
                            }
                        }
//                        if (!TextUtils.isEmpty(goodsBean.toString()) || goodsBean.size > 0) {
//                            setLoadMores(goodsBean.size)
//                        }
                        goodsList.addAll(goodsBean)
//                        adapter?.addData(goodsBean) //todo
                        adapter?.notifyDataSetChanged()
//                        adapter?.setAutoLoadMoreSize(adapter!!.itemCount)
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted", "搜索商品完成!")
            }

            override fun onError(e: Throwable?) {
                Log.e("onError", "搜索商品请求错误!" + e)
            }

        })
    }
    /**
     * @param size 为本次加载的数据长度
     */
    private fun setLoadMores(size: Int) {
        val mCurrentCounter: Int = 0
        adapter?.setOnLoadMoreListener(object : BaseQuickAdapter.RequestLoadMoreListener {
            override fun onLoadMoreRequested() {
                if (size % 20 != 0) {
                    adapter?.loadMoreEnd() //不再有数据
                } else {
                    val str = getEditText()
                    page++
                    cargoList(Api.Pdd, str, page)
                    adapter?.loadMoreComplete()
                }
            }
        })
    }

    private fun getEditText(): String {//获取Edit数据
        val text = etsearch.text.toString().trim()
        if (text.length > 0 && !TextUtils.isEmpty(text)) {
            return text
        }
        return ""
    }
}
