package com.example.juanshichang.fragment


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.OnClick
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.activity.LookAllActivity
import com.example.juanshichang.activity.ShangPinContains
import com.example.juanshichang.adapter.CargoListAdapter
import com.example.juanshichang.adapter.TwoGridAdapterT
import com.example.juanshichang.base.*
import com.example.juanshichang.bean.CargoListBean
import com.example.juanshichang.bean.TabOneBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.widget.LiveDataBus
import com.google.gson.Gson
import kotlinx.coroutines.Runnable
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/8/26 17:47
 * @文件作用: 主页面的其它页面
 */
class OneOtherFragment : BaseFragment() {
    private var emptyView:View? = null
    private var topBView:View? = null
    private var topCheckLinear:LinearLayout? = null
    private var fenLeiTv:TextView? = null //综合
    private var priceImg:ImageView? = null //劵后价
    private var earnImg:ImageView? = null //销量
    private var topGrid:RecyclerView?=null //顶部Recycler
    private var topData:ArrayList<TabOneBean.Category>? = null
    private var topRA: TwoGridAdapterT? = null
    private var botGrid:RecyclerView?=null //底部Recycler
    private var botAdapter: CargoListAdapter? = null
    private var botData:ArrayList<CargoListBean.Goods>? = null
    private var botPage:Int = 1  //这个是页码
    private var FathPage:Int = Int.MAX_VALUE //接收父类 的category_id默认为1
    private var bottype:Int = 0  //这个 对应 sort_type 字段
    private var base:BaseActivity? = null
    override fun getLayoutId(): Int {
        return  R.layout.fragment_one_other
    }

    override fun initViews(savedInstanceState: Bundle) {
        base = this.activity as BaseActivity
        isBindView()
    }
    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        //顶部 点击事件
        topRA?.setOnItemClickListener(object :BaseQuickAdapter.OnItemClickListener{
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                val intent: Intent = Intent(mContext!!, LookAllActivity::class.java)
                intent.putExtra("category_id",FathPage)
                if(topData?.size!!-1 == position){ //todo 特殊添加
                    intent.putExtra("itemtype",0)
                }else{
                    intent.putExtra("itemtype",position+1)
                }
                BaseActivity.goStartActivity(mContext!!,intent)
            }
        })
        //底部 点击事件
        botAdapter?.setOnItemClickListener(object :BaseQuickAdapter.OnItemClickListener{
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                val intent = Intent(mContext!!, ShangPinContains::class.java)
                if (botData?.size!! > position) {
                    intent.putExtra("goods_id", botData!![position].goods_id)
                    intent.putExtra("mall_name", botData!![position].mall_name)
                    startActivity(intent)
                } else {
                    ToastUtil.showToast(mContext!!, "数据发生未知错误,请稍后重试!!!")
                }
            }
        })
    }
    @OnClick(R.id.zongheBt,R.id.ipriceBt,R.id.iearnBt)
    public fun setOnClick(v: View){
        when(v.id){
            R.id.zongheBt ->{ //综合
                goDefState(bottype, 0)
            }
            R.id.ipriceBt ->{ //劵后价 9-券后价升序排序;10-券后价降序排序
                var pricetype: Int = 9
                if (!priceState) {
                    pricetype = 9
                    priceState = true
                    goDefState(bottype, pricetype)
                }else{
                    pricetype = 10
                    priceState = false
                    goDefState(bottype, pricetype)
                }
            }
            R.id.iearnBt ->{ //5-销量升序 6销量降序 todo 销量 此处和 ClassTypeActivity 有出入
                /*var earntype: Int = 5
                if (!earnState) {
                    earntype = 5
                    earnState = true
                    goDefState(bottype, earntype)
                }else{
                    earntype = 6
                    earnState = false
                    goDefState(bottype, earntype)
                }*/
                var earntype: Int = 13
                if (!earnState) {
                    earntype = 13
                    earnState = true
                    goDefState(bottype, earntype)
                }else{
                    earntype = 14
                    earnState = false
                    goDefState(bottype, earntype)
                }
            }
        }
    }
    private fun isBindView() {
        emptyView = mBaseView?.findViewById(R.id.empty)
        topBView = mBaseView?.findViewById<View>(R.id.topBView)
        topCheckLinear = mBaseView?.findViewById<LinearLayout>(R.id.topCheckLinear)
        fenLeiTv =  mBaseView?.findViewById<TextView>(R.id.fenLeiTv)
        priceImg =  mBaseView?.findViewById<ImageView>(R.id.priceImg)
        earnImg =  mBaseView?.findViewById<ImageView>(R.id.earnImg)
        topGrid = mBaseView?.findViewById<RecyclerView>(R.id.topGrid)
        topGrid?.layoutManager = GridLayoutManager(mContext!!,4) as RecyclerView.LayoutManager?
        topData = ArrayList()
        topRA = TwoGridAdapterT(topData!!)
        topGrid?.adapter = topRA
        botGrid = mBaseView?.findViewById<RecyclerView>(R.id.botGrid)
        botGrid?.layoutManager = GridLayoutManager(mContext!!,2)
        botData = ArrayList()
        botAdapter = CargoListAdapter(R.layout.item_banner_pro,botData)
        botAdapter?.emptyView = View.inflate(mContext, R.layout.activity_not_null, null)
        botGrid?.adapter = botAdapter
        //注册 广播监听
        LiveDataBus.get()
            .with("main_tab",String::class.java)
            .observe(this,object : Observer<String>{
                override fun onChanged(t: String?) {
                    LogTool.e("yyyyyyy","监听到了消息:"+t)
                    if(t?.toInt()!=FathPage){ //判断是否重复点击同一条目
                        FathPage = t?.toInt()!!
                        base?.showProgressDialog()
                        isProEnd = 0
                        topBView?.visibility = View.INVISIBLE  //页面隐藏
                        topCheckLinear?.visibility = View.INVISIBLE
                        topGrid?.visibility = View.INVISIBLE
                        botGrid?.visibility = View.INVISIBLE
                        emptyView?.visibility = View.VISIBLE
                        returnState() //恢复筛选按钮状态
                        getTwoT(FathPage)//网络请求
                        cargoList(1,bottype,FathPage)
                    }
                }
            })

    }
    private var isProEnd:Int = 0
    //网络请求
    // 二级页面 请求
    private fun getTwoT(parent_id:Int){
        HttpManager.getInstance().post(Api.CATEGORY, Parameter.getTabData(parent_id,1),object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    val jsonObj: JSONObject = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(mContext!!, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val bean = Gson().fromJson(str, TabOneBean.TabOneBeans::class.java)
                        val data = bean.data.category_list
                        if(topData == null){
                            topData = ArrayList()
                        }else{
                            topData?.clear()
                        }
                        topData?.addAll(data)
                        topData?.add(addItem())
                        topGrid?.post(object : Runnable{
                            override fun run() {
                                emptyView?.visibility = View.GONE
                                topRA?.notifyDataSetChanged()
                                topGrid?.visibility = View.VISIBLE
                                topBView?.visibility = View.VISIBLE //显示
                            }
                        })
                    }
                }
            }

            override fun onCompleted() {
                LogTool.e("onCompleted", "T - Tab2加载完成!")
                isProEnd ++
                if(isProEnd == 2){
                    base?.dismissProgressDialog()
                }
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onError", "T - Tab2加载失败!"+e)
            }
        })
    }
    //请求底部recycler接口
    private fun cargoList(page: Int, sort_type: Int,category_id: Int) { //keyword: String, page: Int, page_size: Int, sort_type: Int,   参数填默认值
        HttpManager.getInstance()
            .post(Api.SEARCH, Parameter.getSearchMap("", page, 20, sort_type, category_id), object :
                Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        jsonObj = JSONObject(str)
                        if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(mContext!!, jsonObj.optString(JsonParser.JSON_MSG))
                        } else { //fastjson 解析
                            val cargoListBean: CargoListBean.CargoListBeans = Gson().fromJson(str, CargoListBean.CargoListBeans::class.java)
                            val goodsBean = cargoListBean.data.goods_search_response.goods_list  // 商品列表
                            if(page == 1){
                                if(botData==null){
                                    botData = ArrayList()
                                }else{
                                    botData?.clear()
                                }
                            }
                            botData?.addAll(goodsBean)
                            botGrid?.post(object : Runnable{
                                override fun run() {
                                    emptyView?.visibility = View.GONE
                                    botAdapter?.notifyDataSetChanged()
                                    botGrid?.visibility = View.VISIBLE
                                    topCheckLinear?.visibility = View.VISIBLE
                                }
                            })
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "商品请求完成!")
                    isProEnd ++
                    if(isProEnd == 2){
                        base?.dismissProgressDialog()
                    }
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "商品请求错误!" + e)
                }

            })
    }
    //  选择逻辑
    //这是选择框逻辑方法
    private var priceState: Boolean = false //劵后价
    private var earnState: Boolean = false //销量
    private fun goDefState(oldType: Int, NewType: Int) {
        //更改为 默认 状态 专用
        if (oldType != NewType) {
            //设置列表变动
            botData?.clear()
            botAdapter?.notifyDataSetChanged()
            if (oldType != 0 && NewType != 0) {
                if (NewType + 1 == oldType || NewType - 1 == oldType) { //todo 此处取巧...
                    if (oldType == 13 && NewType == 14) {
                        earnImg?.drawable?.level = 2
                    }
                    if (oldType == 14 && NewType == 13) {
                        earnImg?.drawable?.level = 1
                    }
                    if (oldType == 10 && NewType == 9) {
                        priceImg?.drawable?.level = 1
                    }
                    if (oldType == 9 && NewType == 10) {
                        priceImg?.drawable?.level = 2
                    }
                }else if (oldType == 13 || oldType == 14) {
                    earnState = false
                    earnImg?.drawable?.level = 0
                    if(NewType == 9){
                        priceImg?.drawable?.level = 1
                    }
                }else if (oldType == 9 || oldType == 10) {
                    priceState = false
                    priceImg?.drawable?.level = 0
                    if(NewType == 13){
                        earnImg?.drawable?.level = 1
                    }
                }
            } else if (NewType == 0) {
                earnState = false
                earnImg?.drawable?.level = 0
                priceState = false
                priceImg?.drawable?.level = 0
                fenLeiTv?.isEnabled = true
            } else if (oldType == 0) {
                fenLeiTv?.isEnabled = false
                if (NewType == 13) {
                    earnImg?.drawable?.level = 1
                }
                if (NewType == 9) {
                    priceImg?.drawable?.level = 1
                }
            }
            bottype = NewType
            botPage = 1
            if (botData?.size != 0) {
                botData?.clear()
            }
            cargoList(1,bottype,FathPage)
        }
    }
    private fun returnState(){ //恢复初始状态
        earnState = false
        earnImg?.drawable?.level = 0
        priceState = false
        priceImg?.drawable?.level = 0
        fenLeiTv?.isEnabled = true
        bottype = 0
    }
    private fun addItem():TabOneBean.Category{
        val item:TabOneBean.Category = TabOneBean.Category(Int.MAX_VALUE,"all","查看全部")
        return item
    }
}

