package com.example.juanshichang.fragment


import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ExpandableListView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.OnClick
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ShoppingCarAdapter
import com.example.juanshichang.base.*
import com.example.juanshichang.bean.CartBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import com.example.juanshichang.widget.LiveDataBus
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/11/15 14:42
 * @文件作用: 购物车
 */
class ShopListFragment : BaseFragment() {
    private var cargoData: CartBean.CartBeans? = null
    //    private var realRet: View? = null
    private var shopRig: TextView? = null
    private var goodsList: ExpandableListView? = null
    private var llSelectAll: LinearLayout? = null
    private var allCheck: CheckBox? = null
    private var nowPrice: TextView? = null
    private var goPayment: TextView? = null
    private var goodsAdapter: ShoppingCarAdapter? = null
    private var shopBotBar:LinearLayout? = null
    private var emptyIv:View? = null
    private var emptyBut:TextView? = null
    override fun getLayoutId(): Int {
        return R.layout.fragment_shop_list
    }

    override fun initViews(savedInstanceState: Bundle) {
        /*shopRig = mBaseView?.findViewById<TextView>(R.id.shopRig)
        goodsList = mBaseView?.findViewById<ExpandableListView>(R.id.goodsList)
        llSelectAll = mBaseView?.findViewById<LinearLayout>(R.id.llSelectAll)
        allCheck = mBaseView?.findViewById<CheckBox>(R.id.allCheck)
        nowPrice = mBaseView?.findViewById<TextView>(R.id.nowPrice)
        goPayment = mBaseView?.findViewById<TextView>(R.id.goPayment)*/
        mBaseView?.let {
            shopRig = it.findViewById<TextView>(R.id.shopRig)
            goodsList = it.findViewById<ExpandableListView>(R.id.goodsList)
            llSelectAll = it.findViewById<LinearLayout>(R.id.llSelectAll)
            allCheck = it.findViewById<CheckBox>(R.id.allCheck)
            nowPrice = it.findViewById<TextView>(R.id.nowPrice)
            goPayment = it.findViewById<TextView>(R.id.goPayment)
            shopBotBar = it.findViewById<LinearLayout>(R.id.shopBotBar)
            emptyIv = it.findViewById<View>(R.id.emptyIv)
            emptyBut = it.findViewById<TextView>(R.id.goGG)
        }
        goodsAdapter =
            ShoppingCarAdapter(mContext, llSelectAll, allCheck, shopRig, nowPrice, goPayment)
        goodsList?.setAdapter(goodsAdapter)
    }

    override fun initData() {

    }
    @OnClick(R.id.goGG)
    fun onClick(v:View){
        when(v.id){
            R.id.goGG->{
                LiveDataBus.get().with("mainGo").value = 0
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (Util.hasLogin(mContext!!)) {
            getShopData(true) //请求网络
            //删除的回调
            goodsAdapter?.setOnDeleteListener(object : ShoppingCarAdapter.OnDeleteListener {
                override fun onDelete(cart_id: String,num:String){
                    removeShopNum(cart_id,num)
                }
            })
            //修改商品数量的回调
            goodsAdapter?.setOnChangeCountListener(object :
                ShoppingCarAdapter.OnChangeCountListener {
                override fun onChangeCount(cart_id: String,count:Int): Boolean {
                  return  editShopNum(cart_id,"$count")
                }
            })
        }
    }
    //获取购物车列表
    private fun getShopData(needChange:Boolean) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL)
            .post(Api.CART, NewParameter.getBaseMap(), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val t =result?.substring(result?.indexOf("{"),result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                            ToastUtil.showToast(
                                mContext!!,
                                jsonObj!!.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            LogTool.e("shopcar", t)
                            cargoData = Gson().fromJson(t, CartBean.CartBeans::class.java)
                            cargoData?.let {
                                if(it.data.products.size!=0){
                                    emptyIv?.visibility = View.GONE
                                    emptyBut?.visibility = View.GONE
                                    shopBotBar?.visibility = View.VISIBLE
//                                    shopRig?.isEnabled = true
                                    shopRig?.visibility = View.VISIBLE
                                }else{
                                    shopBotBar?.visibility = View.GONE
                                    emptyIv?.visibility = View.VISIBLE
                                    emptyBut?.visibility = View.VISIBLE
//                                    shopRig?.isEnabled = false
                                    shopRig?.visibility = View.INVISIBLE
                                }
                                goodsAdapter?.setData(it,needChange)
                                goodsList?.collapseGroup(0) //https://www.jianshu.com/p/2e5eba2421c4
                                goodsList?.expandGroup(0) //展开                            }
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "购物车请求完成")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "购物车请求失败: ${e.toString()}")
                }
            })
    }
    //更改商品数量
    private fun editShopNum(cart_id: String,count:String):Boolean{
        var resultStatus=false
        JhApiHttpManager.getInstance(Api.NEWBASEURL)
            .post(Api.CARTEDIT, NewParameter.getEditSCMap(cart_id,count,1), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val t =result?.substring(result?.indexOf("{"),result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                            ToastUtil.showToast(
                                mContext!!,
                                jsonObj!!.optString(JsonParser.JSON_MSG)
                            )
                        } else {
//                            getShopData(true) //请求购物车
                            resultStatus=true
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "购物车增减请求完成")
                }

                override fun onError(e: Throwable?) {

                    LogTool.e("onCompleted", "购物车增减请求失败: ${e.toString()}")
                }
            })
        return resultStatus
    }
    //删除商品
    private fun removeShopNum(cart_id: String,count:String){
        JhApiHttpManager.getInstance(Api.NEWBASEURL)
            .post(Api.CARTDELE, NewParameter.getEditSCMap(cart_id,count,2), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val t =result?.substring(result?.indexOf("{"),result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                            ToastUtil.showToast(
                                mContext!!,
                                jsonObj!!.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            getShopData(false) //请求购物车
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "购物车删除请求完成")
                }

                override fun onError(e: Throwable?) {

                    LogTool.e("onCompleted", "购物车删除请求失败: ${e.toString()}")
                }
            })
    }
}
