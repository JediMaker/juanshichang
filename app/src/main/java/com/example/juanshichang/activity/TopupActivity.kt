package com.example.juanshichang.activity

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.R
import com.example.juanshichang.adapter.TopUpAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.bean.TopUpBarBean
import com.example.juanshichang.bean.TopUpBean
import com.example.juanshichang.bean.TopUpllBean
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_topup.*
import kotlinx.android.synthetic.main.activity_topup_plus.*
import kotlinx.android.synthetic.main.regist_item.*
import org.json.JSONObject
import rx.Subscriber


/**
 * @作者：yzq
 * @创建时间：2019/11/5 16:35
 * @文件作用: 充值页面
 */
class TopupActivity : BaseActivity(), View.OnClickListener {
    private var topBarSelect = 0 //topbar的选中
    //初始化数据源
    private val topBarData = arrayListOf<TopUpBarBean>(
        TopUpBarBean(R.drawable.tel, "话费充值"),
        TopUpBarBean(R.drawable.flow, "流量充值"),
        TopUpBarBean(R.drawable.oil, "油卡充值")
    )
    private val hfPrice = arrayListOf<TopUpBean>() //话费充值数据源
    private var hfMon: String = ""  //话费金额
    private var pubAdapter: TopUpAdapter? = null
    private var llPrice = arrayListOf<TopUpBean>() //流量充值数据源
    private var llMon: String = ""  //流量金额
    private val ykPrice = arrayListOf<TopUpBean>() //油卡充值数据源
    private var ykId: String = "10008"  //油卡id
    private var ykMon: String = ""  //流量金额
    private var mStubView: View? = null
    override fun getContentView(): Int {
        return R.layout.activity_topup
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this, R.color.white)
        //初始化
        TabCreateUtils.setTopUpTab(
            this@TopupActivity,
            topupTab,
            topBarData,
            object : TabCreateUtils.onTitleClickListener {
                override fun onTitleClick(index: Int) {
                    if (index != topBarSelect) {
                        if (topBarSelect == 2) { //解除隐藏
                            mStubView?.visibility = View.GONE
                            czXx.visibility = View.VISIBLE
                        }
                        topBarSelect = index
                        //进行页面变更
                        addData(index)
                        //对油卡充值页面的处理
                        if (index == 2) {
                            czXx.visibility = View.GONE
                            if (mStubView == null) { //https://www.jianshu.com/p/a0423a5eb715
                                mStubView = oilPayLayout.inflate()
                            } else {
                                mStubView?.visibility = View.VISIBLE
                            }
                            addLayoutPlus() //添加选择监听
                            goTopUp.isEnabled = true //设置可用
                        } else {
                            goTopUp.isEnabled = false //动态设置可用
                        }
                    }
                }
            })
        val gl = GridLayoutManager(this, 3)
        pubAdapter = TopUpAdapter()
        priceRecycler.layoutManager = gl
        priceRecycler.adapter = pubAdapter
    }

    override fun initData() {
        addData(0)
        //注册监听
        LaRet.setOnClickListener(this)
        goTopUp.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.LaRet -> {//返回
                finish()
            }
            R.id.goTopUp -> { //立即充值
                if (topBarSelect == 2) {//油卡充值
                    val oilNum = ykAccount.text.toString().trim()
                    val phoneNum = phoneAccount.text.toString().trim()
                    if (comparisonCardNum(OIlType, oilNum) && comparisonPhoneNum(phoneNum)) {
                        //这里 进行 充值相关操作
                        DisplayToast("开始油卡充值")
                        LogTool.e("topup","1:中石化、2:中石油  油卡 --- $OIlType  充值金额： $ykMon  ID:$ykId")
                    }
                } else {
                    val phoneTopNum = phoneId.text.toString().trim()
                    if (comparisonPhoneNum(phoneTopNum)) {
                        //这里 进行 充值相关操作
                        DisplayToast("开始充值...")
                        LogTool.e("topup","话费：金额 --- $hfMon  流量: 金额--- $llMon")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pubAdapter?.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                if (!pubAdapter!!.data[position].priPrice.isEmpty()) { //判断是否是 油卡充值
                    czYj.text = pubAdapter!!.data[position].topPrice //原价
                    czYh.text = "售价${pubAdapter!!.data[position].priPrice}元" //优惠
                }
                val phone = phoneId.text.toString().trim()
                when (topBarSelect) {
                    0 -> { //话费
                        if(phone.length == 11){
                            goTopUp.isEnabled = false
                            getHFTopUp(phone,pubAdapter!!.data[position].botPrice)
                        }
                        hfMon = pubAdapter!!.data[position].priPrice
                    }
                    1 -> {//流量
                        if(phone.length == 11){
                            goTopUp.isEnabled = false
                        }
                        llMon = pubAdapter!!.data[position].topPrice
                    }
                    2 -> { //油卡
                        if (OIlType == 2) {//中石油  中石油的充值id 为定值 10008
                            ykId = "10008"
                        } else {
                            ykId = ykPrice[position].id
                        }
                        ykMon = pubAdapter!!.data[position].priPrice
                    }
                }
                pubAdapter?.setNewSelect(position)
            }
        })
        //添加手机号Edit监听
        phoneId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { //文本改变后
                s?.let {
                    val str = it.toString().trim()
                    if (str.length < 7) { //清空数据源
                        phoneLocalId.text = ""
                    }
                    if (str.length == 7) {
                        getPhoneLoca(s.toString()) //网络请求 拿到归属地
                    }

                    if (str.length == 10) { //这个 用于设置号码切换状态
                        setPhoneLocal(isPNLocal)
                    }
                    //设置充值按钮不可点击条件
                    if (str.length > 11) {
                        goTopUp.isEnabled = false
                    }
                    if (str.length == 11) { //请求网络
                        if (topBarSelect == 0) { //话费充值
                            getHFTopUp(str, hfMon)
                        }
                        if (topBarSelect == 1) { //流量充值 获取流量套餐
                            getHFTopUpLl(str)
                        }
                    }
                }

                LogTool.e("Edit", "执行了afterTextChanged  当前s：${s.toString()}")
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {//文本改变前
                LogTool.e("Edit", "执行了beforeTextChanged  当前s：${s.toString()}")
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) { //文本改变
                if (s?.toString()!!.length >= 8 && isPNLocal.isEmpty()) { //防止查询走空
                    getPhoneLoca(s.toString()) //网络请求 拿到归属地
                }
                if (s.toString().length == 11 && !isPNLocal.isEmpty()) {
                    setPhoneLocal(isPNLocal)
                }
                LogTool.e("Edit", "执行了onTextChanged  当前s：${s.toString()}")
            }
        })
        // 输入 对话框默认数据 即 本机号码
        if (phoneId.text.toString().isEmpty()) {
            phoneId.setText("17601619521")
            phoneId.setSelection(phoneId.text.length) //将光标移到最后
        }
    }

    //内置默认数据 并 更新数据
    private fun addData(key: Int) {
        if (key == 0) {
            if (hfPrice.size == 0) {
                hfPrice.add(TopUpBean("20元", "20","","20")) //话费选项
                hfPrice.add(TopUpBean("30元", "30","","30"))
                hfPrice.add(TopUpBean("50元", "50","","50"))
                hfPrice.add(TopUpBean("100元", "100","","100"))
                hfPrice.add(TopUpBean("300元", "300","","300"))
                hfPrice.add(TopUpBean("500元", "500","","500"))
//                hfPrice.add(TopUpBean("500元", "售价498.50元"))
            }
            pubAdapter?.setMeNewData(hfPrice)
            czYj.text = hfPrice[0].topPrice //原价
//            czYh.text = hfPrice[0].botPrice //优惠
            czYh.text = "售价${hfPrice[0].priPrice}元"
            hfMon = hfPrice[0].priPrice
        }
        if (key == 1) {
            if (llPrice.size == 0) {
                llPrice.add(TopUpBean("20M","2")) //流量选项
                llPrice.add(TopUpBean("50M","3"))
                llPrice.add(TopUpBean("100M","5"))
                llPrice.add(TopUpBean("200M","9"))
                llPrice.add(TopUpBean("500M","15"))
                llPrice.add(TopUpBean("1G","20"))
            }
            pubAdapter?.setMeNewData(llPrice)
            czYj.text = llPrice[0].topPrice //原价
            czYh.text = "售价${llPrice[0].priPrice}元" //优惠
            llMon = llPrice[0].priPrice
        }
        if (key == 2) {
            if (ykPrice.size == 0) {
                //todo  id 只对 中石化 有用 ---- 中石油统一id &&&  10008
                ykPrice.add(TopUpBean("100元", "100", "10001")) //油卡选项
                ykPrice.add(TopUpBean("200元", "200", "10002"))
                ykPrice.add(TopUpBean("500元", "500", "10003"))
                ykPrice.add(TopUpBean("1000元", "1000", "10004"))
//                ykPrice.add(TopUpBean("2000元", ""))
//                ykPrice.add(TopUpBean("5000元", ""))
            }
            pubAdapter?.setMeNewData(ykPrice)
            ykMon = ykPrice[0].priPrice
        }
    }

    //设置方法
    private var isPNLocal: String = ""

    private fun setPhoneLocal(local: String?) {
        val str = phoneId.text.toString() //获取输入框信息
        if (str.equals("17601619521")) { //todo 这里需要设置为当前登录的手机号
            val botTit = "默认号码($local)"
            phoneLocalId.text = botTit
        } else {
            phoneLocalId.text = local
        }
    }
    //这里对加油卡的校验
    /**
     * @param type  加油卡类型 1:中石化、2:中石油 //	加油卡卡号，中石化：以100011开头的19位卡号、中石油：以90开头的16位卡号
     * @param content 输入信息判断
     */
    private fun comparisonCardNum(type: Int, content: String): Boolean {
        if (type == 1) { //中石化  以100011开头的19位卡号
            if (!content.startsWith("100011") || content.toString().length != 19) { //如果开头没有包含100011 就不是中石化
                DisplayToast("中石化卡号格式错误")
                return false
            }
        } else if (type == 2) {
            if (!content.startsWith("90") || content.toString().length != 16) { //如果开头没有包含90 就不是中石油
                DisplayToast("中石油卡号格式错误")
                return false
            }
        }
        return true
    }

    //手机号校验
    private fun comparisonPhoneNum(content: String): Boolean {
        if (content.length != 11 || !Util.validateMobile(content)) {
            DisplayToast("手机号码格式有误")
            return false
        }
        return true
    }

    //在这里添加对加油卡页面监听
    private var OIlType: Int = 2 //油卡类型 默认中石油

    private fun addLayoutPlus() {
        radioAll.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if (checkedId == R.id.chinaSy && OIlType != 2) { //中石油
                    OIlType = 2
                    ykId="10008"
                    ykAccount.setText("")
                    ykAccount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
                }
                if (checkedId == R.id.chinaSh && OIlType != 1) { //中石化
                    OIlType = 1
                    ykAccount.setText("")
                    ykAccount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(19))
                    //重新设置
                    pubAdapter?.setMeNewData(ykPrice)
                    ykMon = ykPrice[0].priPrice
                    ykId = ykPrice[0].id
                }
            }
        })
    }

    private fun DisplayToast(str: String) {
        val toast = Toast.makeText(this@TopupActivity, str, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 220)
        toast.show()
    }

    //以下为网络请求
    //获取手机号码归属地
    private fun getPhoneLoca(phoneId: String) {
        val map = hashMapOf<String, String>()
        map.put("phone", phoneId)
        map.put("dtype", "json")
        map.put("key", Api.PhoneKey)
        var phoneMes: String = ""
        JhApiHttpManager.getInstance(Api.JUHEAPi)
            .post(Api.TELCHECK, map, object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str =result?.substring(result?.indexOf("{"),result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        val jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj.optString("resultcode").equals("200")) {
//                           todo 这里提示异常信息注释之后要放开，demo演示先跳过，原因后续排查

                           /* ToastUtil.showToast(
                                this@TopupActivity,
                                jsonObj.optString("reason")*/
                        } else {
                            val result = jsonObj.getJSONObject("result")
                            val province = result.getString("province") //省
                            val city = result.getString("city") //市
                            val company = result.getString("company") //运营商
                            if (province.equals("$city")) {
                                phoneMes = "$city$company"
                            } else {
                                phoneMes = "$province$city$company"
                            }
                            LogTool.e("Edit1", "onNext  拿到了地址如下：${phoneMes}")
                            this@TopupActivity.runOnUiThread {
                                setPhoneLocal(phoneMes)
                                isPNLocal = phoneMes
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "号码归属地查询完成")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "号码归属地查询Error")
                }
            })
    }

    //检测手机号码及金额是否能充值
    private fun getHFTopUp(phone: String, cardnum: String) {
        val map = hashMapOf<String, String>()
        map.put("phoneno", phone)
        map.put("cardnum", cardnum)
        map.put("key", Api.TeleKey)
        JhApiHttpManager.getInstance(Api.JUHEAPi2)
            .post(Api.PNHFSELECT, map, object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str =result?.substring(result?.indexOf("{"),result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        val jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj.optString("error_code").equals("0")) {
                            ToastUtil.showToast(
                                this@TopupActivity,
                                jsonObj.optString("reason")
                            )
                        } else {
                            val result = jsonObj.optString("result")
                            if (result.contains("允许")) {
                                getHFTopUpMz(phone, cardnum)
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "检测手机号码及金额是否能充值 完成")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "检测手机号码及金额是否能充值 Error")
                }
            })
    }

    //根据手机号和面值查询商品信息
    private fun getHFTopUpMz(phone: String, cardnum: String) {
        val map = hashMapOf<String, String>()
        map.put("phoneno", phone)
        map.put("cardnum", cardnum)
        map.put("key", Api.TeleKey)
        JhApiHttpManager.getInstance(Api.JUHEAPi2)
            .post(Api.MZSELECT, map, object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str =result?.substring(result?.indexOf("{"),result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        val jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj.optString("error_code").equals("0")) {
                            ToastUtil.showToast(
                                this@TopupActivity,
                                jsonObj.optString("reason")
                            )
                        } else {
                            val reason = jsonObj.optString("reason")
                            val result = jsonObj.getJSONObject("result")
                            if (reason.contains("成功")) {
                                val inprice = result.optDouble("inprice")
                                czYh.text = "售价$inprice 元"
                                goTopUp.isEnabled = true //设置可点击可用
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "根据手机号和面值查询商品信息 完成")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "根据手机号和面值查询商品信息 Error")
                }
            })
    }

    //根据手机号码获取支持的流量套餐
    private fun getHFTopUpLl(phone: String) {
        val map = hashMapOf<String, String>()
        map.put("phone", phone)
        map.put("key", Api.PHONELLKey)
        JhApiHttpManager.getInstance(Api.JUHEAPi3)
            .post(Api.JCLLTC, map, object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str =result?.substring(result?.indexOf("{"),result.length)
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        val jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj.optString("error_code").equals("0")) {
                            ToastUtil.showToast(
                                this@TopupActivity,
                                jsonObj.optString("reason")
                            )
                        } else {
                            val data = Gson().fromJson(str, TopUpllBean.TopUpllBeans::class.java)
                            llPrice = convType(data)
                            goTopUp.isEnabled = true //设置可点击可用
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "手机号码获取支持的流量套餐 完成")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "手机号码获取支持的流量套餐 Error")
                }
            })
    }

    private fun convType(data: TopUpllBean.TopUpllBeans?): ArrayList<TopUpBean> {
        val result = data?.result
        val list = result?.get(0)?.flows
        list?.let {
            val topList = arrayListOf<TopUpBean>()
            for (v in it) {
                topList.add(TopUpBean(v.p, v.inprice, v.id))
            }
            return topList
        }
        return arrayListOf<TopUpBean>()
    }
}
