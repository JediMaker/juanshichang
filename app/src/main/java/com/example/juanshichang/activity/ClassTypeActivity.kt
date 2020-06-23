package com.example.juanshichang.activity

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.CargoListAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.CargoListBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastTool
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_class_type.*
import kotlinx.android.synthetic.main.activity_seek_bar.*
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
    var type: Int = 0
    var goodsList = mutableListOf<CargoListBean.Goods>()
    override fun getContentView(): Int {
        return R.layout.activity_class_type
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this, R.color.colorPrimary)
        if (null != getIntent().getStringExtra("keyword")) {
            keyWord = intent.getStringExtra("keyword")
            etsearch.setText(keyWord.toCharArray(), 0, keyWord.length)
            val grid = GridLayoutManager(this@ClassTypeActivity, 2)
            adapter = CargoListAdapter(R.layout.item_banner_pro, goodsList) //item_type
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
                                cargoList(str, page, 20, type, 0) // 默认走 0
                                adapter?.loadMoreComplete()
                            }
                        }
                    })
                }
            }, mTypeClassView)
            mTypeClassView.layoutManager = grid
            mTypeClassView.adapter = adapter
            cargoList(keyWord.trim(), page, 20, type, 0)
        } else {
            ToastUtil.showToast(this@ClassTypeActivity, "哎呀  出错啦!!!")
            finish()
        }
        etsearch.setOnClickListener(this)
        mbackLayout.setOnClickListener(this)
        mSearchBt.setOnClickListener(this)
        fenLeiBt.setOnClickListener(this) //综合
        priceBt.setOnClickListener(this) //劵后价
        earnBt.setOnClickListener(this)//收益
    }

    override fun initData() {
        adapter?.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener { // item 点击事件
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                val intent = Intent(this@ClassTypeActivity, ShangPinContains::class.java)
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
                    cargoList(str, page, 20, type, 0)
                } else {
                    ToastTool.showToast(this@ClassTypeActivity, "大侠 你想找什么")
                }

            }
            fenLeiBt -> {//综合
                goDefState(type, 0)
            }
            priceBt -> {//劵后价
                //9-券后价升序排序;10-券后价降序排序
                var pricetype: Int = 9
                if (!priceState) {
                    pricetype = 9
                    priceState = true
                    goDefState(type, pricetype)
                }else{
                    pricetype = 10
                    priceState = false
                    goDefState(type, pricetype)
                }
            }
            earnBt -> {//收益
                // 13-按佣金金额升序排序;14-按佣金金额降序排序
                var earntype: Int = 13
                if (!earnState) {
                    earntype = 13
                    earnState = true
                    goDefState(type, earntype)
                }else{
                    earntype = 14
                    earnState = false
                    goDefState(type, earntype)
                }
            }
        }
    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_class_type)
//    }
    //-todo 这里暂时修改为自营商品搜索
    /**
     *  商品搜索
     */
    private fun cargoList(keyword: String, page: Int, page_size: Int, sort_type: Int, category_id: Int) {
        HttpManager.getInstance()
            .post(Api.SEARCH, Parameter.getSearchMap(keyword, page, page_size, sort_type, category_id), object :
                Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str =result?.substring(result?.indexOf("{"),result.length)

                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        jsonObj = JSONObject(str)
                        if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(this@ClassTypeActivity, jsonObj.optString(JsonParser.JSON_MSG))
                        } else { //fastjson 解析
//                        val cargoListBean:CargoListBean = JSON.parseObject(str,CargoListBean::class.java)
                            val cargoListBean: CargoListBean.CargoListBeans = Gson().fromJson(str, CargoListBean.CargoListBeans::class.java)
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
                    LogTool.e("onCompleted", "搜索商品完成!")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "搜索商品请求错误!" + e)
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
                    cargoList(str, page, 20, type, 0)
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

    // 0-综合排序 earnBt 13-按佣金金额升序排序;14-按佣金金额降序排序  priceBt :9-券后价升序排序;10-券后价降序排序
    private var priceState: Boolean = false //劵后价
    private var earnState: Boolean = false //佣金
    //这是选择框逻辑方法
    private fun goDefState(oldType: Int, NewType: Int) {
        //更改为 默认 状态 专用
        if (oldType != NewType) {
            if (oldType != 0 && NewType != 0) {
                if (NewType + 1 == oldType || NewType - 1 == oldType) { //todo 此处取巧...
                    if (oldType == 13 && NewType == 14) {
                        earnImg.drawable.level = 2
                    }
                    if (oldType == 14 && NewType == 13) {
                        earnImg.drawable.level = 1
                    }
                    if (oldType == 10 && NewType == 9) {
                        priceImg.drawable.level = 1
                    }
                    if (oldType == 9 && NewType == 10) {
                        priceImg.drawable.level = 2
                    }
                }else if (oldType == 13 || oldType == 14) {
                    earnState = false
                    earnImg.drawable.level = 0
                    if(NewType == 9){
                        priceImg.drawable.level = 1
                    }
                }else if (oldType == 9 || oldType == 10) {
                    priceState = false
                    priceImg.drawable.level = 0
                    if(NewType == 13){
                        earnImg.drawable.level = 1
                    }
                }
            } else if (NewType == 0) {
                /*if (oldType == 13 || oldType == 14) {
                    earnState = false
//                    earnImg.imageResource = R.drawable.list_un
                    earnImg.drawable.level = 0
                }
                if (oldType == 9 || oldType == 10) {
                    priceState = false
//                    priceImg.imageResource = R.drawable.list_un
                    priceImg.drawable.level = 0
                }*/
                earnState = false
                earnImg.drawable.level = 0
                priceState = false
                priceImg.drawable.level = 0
                fenLeiTv.isEnabled = true
            } else if (oldType == 0) {
                fenLeiTv.isEnabled = false
                if (NewType == 13) {
                    earnImg.drawable.level = 1
                }
                if (NewType == 9) {
                    priceImg.drawable.level = 1
                }
            }
            type = NewType
            page = 1
            if (goodsList.size != 0) {
                goodsList.clear()
            }
            cargoList(keyWord.trim(), page, 20, type, 0)
        }
    }
    //这是一个 关闭缓存 加载 图片的方法
    private fun getImage(view:ImageView,id:Int){
        Glide.with(view).load(id).skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(view)
    }
}
