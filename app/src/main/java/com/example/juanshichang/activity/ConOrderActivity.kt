package com.example.juanshichang.activity

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ConOrderAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.ConOrderBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.UtilsBigDecimal
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_con_order.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/12/4 17:56
 * @文件作用: 提交订单页面
 */
class ConOrderActivity : BaseActivity(),View.OnClickListener{
    private var cartList:ArrayList<String>? = null
    private var data:ConOrderBean.ConOrderBeans? = null
    private var adapter:ConOrderAdapter? = null
    private var addresse:ConOrderBean.Addresse? = null
    override fun getContentView(): Int {
        return R.layout.activity_con_order
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@ConOrderActivity, R.color.white)
        if(null != intent.getBundleExtra("bundle")){
            cartList = intent.getBundleExtra("bundle").getStringArrayList("checkAll")
            cartList?.let {
                showProgressDialog()
                reqOrderFrom(it)
                adapter = ConOrderAdapter()
                coRecycler.adapter = adapter
            }
        }else{
            ToastUtil.showToast(this@ConOrderActivity,"数据异常,请稍后重试。")
            finish()
        }
    }

    override fun initData() {
        coRet.setOnClickListener(this) //返回
        siteRig.setOnClickListener(this) //更改收货地址信息
        coPay.setOnClickListener(this) //确认提交订单
    }

    override fun onClick(v: View?) {
        when(v){
            coRet ->{
                finish()
            }
            siteRig ->{
                ToastUtil.showToast(this@ConOrderActivity,"更改收货地址....")
            }
            coPay ->{
                if(addresse == null){
                    ToastUtil.showToast(this@ConOrderActivity,"收货地址不能为空")
                    return
                }
                ToastUtil.showToast(this@ConOrderActivity,"确认提交")
            }
        }
    }
    private fun setUI(iData: ConOrderBean.ConOrderBeans?) {
        iData?.let {
            adapter?.setMyData(it)
            val site = it.data.addresses
            if(site.size > 0){
                addresse = site[0]
                ocName.text = addresse?.address_id
                coPhone.text = "0371-110"
                coSite.text = "${addresse?.city} ${addresse?.address_detail}"
            }else{//无地址 处理

            }
            //设置底部数据
            val totalSum = it.data.total
            val totalSumStr = "￥${UtilsBigDecimal.mul(totalSum,1.toDouble(),2)}"
            coPrice.text = getGaudyStr(totalSumStr)
            val list = it.data.products
            if(list.size != 0){
                val v = View.inflate(this@ConOrderActivity,R.layout.item_sub_oreder_end,null)
                v.findViewById<TextView>(R.id.allPrice).text = totalSumStr
                adapter?.addFooterView(v)
            }
        }
    }
    //花样设置 合计金额  return 设置后的数据
    private fun getGaudyStr(str: String): SpannableString {
        val spannableString = SpannableString(str)
        val textColor = ForegroundColorSpan(Color.parseColor("#F93736")) //文字颜色
//        StyleSpan : 字体样式：粗体、斜体等
        val dotInd = str.indexOf(".") //获取小数点的下标
        val rmbInd = str.indexOf("¥") //获取人民币符号的下标
        val frontSize = AbsoluteSizeSpan(56) //56px
        val behindSize = AbsoluteSizeSpan(72) //72px
        spannableString.setSpan(textColor, 0, str.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE) //设置颜色
        if (rmbInd != -1) {
            spannableString.setSpan(
                frontSize,
                rmbInd,
                rmbInd + 1,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            ) //设置人民币符号大小 前面包括，后面不包括
        }
        if (dotInd != -1) {
            spannableString.setSpan(
                behindSize,
                1,
                dotInd,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            ) //设置元符号大小   前面包括，后面包括
            spannableString.setSpan(
                frontSize,
                dotInd,
                str.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            ) //设置人民币符号颜色  前面不包括，后面不包括
        }
        //Spannable. SPAN_EXCLUSIVE_INCLUSIVE：前面不包括，后面包括
        return spannableString
    }
    //提交订单
    private fun reqOrderFrom(list: ArrayList<String>){
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.CHECKOUT,
            NewParameter.getCoMap(list),
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
                                this@ConOrderActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val iData = Gson().fromJson(t,ConOrderBean.ConOrderBeans::class.java)
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
