package com.example.juanshichang.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ConOrderAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.ConOrderBean
import com.example.juanshichang.bean.SiteBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.*
import com.example.juanshichang.widget.LiveDataBus
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_con_order.*
import kotlinx.android.synthetic.main.item_txxq.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/12/4 17:56
 * @文件作用: 提交订单页面
 */
class ConOrderActivity : BaseActivity(), View.OnClickListener {
    private var cartList: ArrayList<String>? = null
    private var data: ConOrderBean.ConOrderBeans? = null
    private var adapter: ConOrderAdapter? = null
    private var addresse: ConOrderBean.Addresse? = null
    private var addresseId: String? = null
    private var bundle: Bundle? = null
    override fun getContentView(): Int {
        return R.layout.activity_con_order
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@ConOrderActivity, R.color.colorPrimary)
        if (null != intent.getBundleExtra("bundle")) {
            bundle = intent.getBundleExtra("bundle") //获取 bundle
            cartList = bundle?.getStringArrayList("checkAll")
            cartList?.let {
                showProgressDialog()
                reqOrderFrom(it)
                adapter = ConOrderAdapter()
                coRecycler.adapter = adapter
            }
        } else {
            ToastUtil.showToast(this@ConOrderActivity, "数据异常,请稍后重试。")
            finish()
        }
        //监听地址是否被删除
        LiveDataBus.get().with("delAddress", String::class.java).observe(this, object : Observer<String> {
            override fun onChanged(address_id: String?) {
                if (address_id.equals(addresseId)){//地址被删除
                    cartList?.let {
                        showProgressDialog()
                        reqOrderFrom(it)
                    }
                }
            }
        })
    }

    override fun initData() {
        coRet.setOnClickListener(this) //返回
        coSite.setOnClickListener(this)
        siteRig.setOnClickListener(this) //更改收货地址信息
        coPay.setOnClickListener(this) //确认提交订单
        hintText.setOnClickListener(this) //无收货地址提示
    }

    override fun onClick(v: View?) {
        when (v) {
            coRet -> {
                finish()
            }
            hintText,  //无收货地址提示
            coSite,
            siteRig -> { //更换地址
                val intent = Intent(this@ConOrderActivity, SiteListActivity::class.java)
                intent.putExtra("checkLocal", "true")
                startActivityForResult(intent, 1)
            }
            coPay -> {
                 if(addresseId == null){
                    ToastUtil.showToast(this@ConOrderActivity,"收货地址不能为空")
                    return
                }
                val intent = Intent(this@ConOrderActivity, SettleAccActivity::class.java)
                bundle?.putString("address_id", addresseId) //携带地址id 跳转
                intent.putExtra("bundle", bundle)
                startActivity(intent)
                finish()
                ToastUtil.showToast(this@ConOrderActivity, "确认提交")
            }
        }
    }

    private fun setUI(iData: ConOrderBean.ConOrderBeans?) {
        iData?.let {
            adapter?.setMyData(it)
            val site = it.data.addresses
            val default_address_id = it.data.default_address_id.toString()
            if (site.size > 0) {
                hintText.visibility = View.GONE
                if ("0".equals(default_address_id)) {//没有默认地址取地址列表首个地址
                    addresse = site[0]
                    addresseId = addresse?.address_id
                    ocName.text = "${addresse?.firstname}${addresse?.lastname}"
                    coPhone.text = addresse?.iphone
                    coSite.text = "${addresse?.province} ${addresse?.city} ${addresse?.county} ${addresse?.address_detail}"
                } else {//取默认地址
                    for (addressData in site) {
                        if (addressData.address_id.toString().equals(default_address_id)) {
                            addresseId=default_address_id
                            ocName.text = "${addressData?.firstname}${addressData?.lastname}"
                            coPhone.text = addressData?.iphone
                            coSite.text = "${addressData?.province} ${addressData?.city} ${addressData?.county} ${addressData?.address_detail}"
                        }
                    }
                }

            } else {//无地址 处理
                hintText.visibility = View.VISIBLE
            }


            //设置底部数据
            val totalSum = it.data.total
            val totalSumStr = "￥${UtilsBigDecimal.mul(totalSum, 1.toDouble(), 2)}"
            coPrice.text = Util.getGaudyStr(totalSumStr)
            val list = it.data.products
            if (list.size != 0) {
                val v = View.inflate(this@ConOrderActivity, R.layout.item_sub_oreder_end, null)
                v.findViewById<TextView>(R.id.allPrice).text = totalSumStr
                adapter?.addFooterView(v)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == 2) {
            val bundle = data?.extras?.get("bundle") as Bundle
            LogTool.e("onActivityResult1", bundle.toString())
            bundle.let {
                val ds = bundle.getParcelable<SiteBean.Addresse>("data")
                hintText.visibility = View.GONE
//                addresse=ds
                LogTool.e("onActivityResult2", ds.toString())
                addresseId = ds?.address_id
                ocName.text = ds?.name
                coPhone.text = ds?.iphone
                coSite.text = "${ds?.city} ${ds?.address_detail}"
            }
        }
    }

    //提交订单
    private fun reqOrderFrom(list: ArrayList<String>) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.CHECKOUT,
            NewParameter.getCoMap(list),
            object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val t = result?.substring(result?.indexOf("{"), result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(t)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        /*  if (!jsonObj?.optString(JsonParser.JSON_CODE)!!.equals(JsonParser.JSON_SUCCESS)) {
                              ToastUtil.showToast(
                                  this@ConOrderActivity,
                                  jsonObj.optString(JsonParser.JSON_MSG)
                              )
                          }*/
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            ToastUtil.showToast(
                                this@ConOrderActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val iData = Gson().fromJson(t, ConOrderBean.ConOrderBeans::class.java)
                            data = iData
                            setUI(iData)
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "订单详情请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "订单详情请求失败: ${e.toString()}")
                }
            })
    }

}
