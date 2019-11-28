package com.example.juanshichang.fragment


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.ExpandableListView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ShoppingCarAdapter
import com.example.juanshichang.base.*
import com.example.juanshichang.base.BaseActivity.Companion.goStartActivity
import com.example.juanshichang.bean.CartBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
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
        }
        goodsAdapter =
            ShoppingCarAdapter(mContext, llSelectAll, allCheck, shopRig, nowPrice, goPayment)
        goodsList?.setAdapter(goodsAdapter)
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        if (Util.hasLogin(mContext!!)) {
            getShopData() //请求网络
            //删除的回调
            goodsAdapter?.setOnDeleteListener(object : ShoppingCarAdapter.OnDeleteListener {
                override fun onDelete() {

                }
            })
            //修改商品数量的回调
            goodsAdapter?.setOnChangeCountListener(object :
                ShoppingCarAdapter.OnChangeCountListener {
                override fun onChangeCount(cart_id: String) {

                }
            })
        }
    }

    private fun getShopData() {
        JhApiHttpManager.getInstance(Api.NEWBASEURL)
            .post(Api.CART, NewParameter.getBaseMap(), object : Subscriber<String>() {
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
                                mContext!!,
                                jsonObj!!.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            LogTool.e("shopcar", t)
                            cargoData = Gson().fromJson(t, CartBean.CartBeans::class.java)
                            cargoData.let {
                                goodsAdapter?.setData(it)
//                                goodsList?.collapseGroup(0) //https://www.jianshu.com/p/2e5eba2421c4
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
}
