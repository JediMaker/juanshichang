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
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.*
import kotlinx.android.synthetic.main.activity_topup.*
import kotlinx.android.synthetic.main.activity_topup_plus.*
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
    private var pubAdapter: TopUpAdapter? = null
    private val llPrice = arrayListOf<TopUpBean>() //流量充值数据源
    private val ykPrice = arrayListOf<TopUpBean>() //油卡充值数据源
    private var mStubView:View ? = null
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
                        if(topBarSelect == 2){
                            mStubView?.visibility = View.GONE
                            czXx.visibility = View.VISIBLE
                        }
                        topBarSelect = index
                        //进行页面变更
                        addData(index)
                        //对油卡充值页面的处理
                        if(index == 2){
                            czXx.visibility = View.GONE
                            if(mStubView == null){
                                mStubView = oilPayLayout.inflate()
                            }else{
                                mStubView?.visibility = View.VISIBLE
                            }
                            addLayoutPlus() //添加选择监听
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
                if(topBarSelect == 2){//油卡充值
                    val oilNum = ykAccount.text.toString().trim()
                    val phoneNum = phoneAccount.text.toString().trim()
                    if(comparisonCardNum(OIlType,oilNum) && comparisonPhoneNum(phoneNum)){
                        //这里 进行 充值相关操作
                        DisplayToast("开始油卡充值")
                    }
                }else{
                    val phoneTopNum = phoneId.text.toString().trim()
                    if(comparisonPhoneNum(phoneTopNum)){
                        //这里 进行 充值相关操作
                        DisplayToast("开始充值...")
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        pubAdapter?.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                if (!pubAdapter!!.data[position].botPrice.isEmpty()) { //判断是否是 油卡充值
                    czYj.text = pubAdapter!!.data[position].topPrice //原价
                    czYh.text = pubAdapter!!.data[position].botPrice //优惠
                }
                pubAdapter?.setNewSelect(position)
            }
        })
        //添加手机号Edit监听
        phoneId.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) { //文本改变后
                if (s.toString().length < 7){ //清空数据源
                    phoneLocalId.text = ""
                }
                if(s.toString().length == 7){
                    getPhoneLoca(s.toString()) //网络请求 拿到归属地
                }

                if(s?.length == 10){ //这个 用于切换状态
                    setPhoneLocal(isPNLocal)
                }
                LogTool.e("Edit","执行了afterTextChanged  当前s：${s.toString()}")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {//文本改变前
                LogTool.e("Edit","执行了beforeTextChanged  当前s：${s.toString()}")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { //文本改变
                if(s?.toString()!!.length >= 8 && isPNLocal.isEmpty()){ //防止查询走空
                    getPhoneLoca(s.toString()) //网络请求 拿到归属地
                }
                if(s.toString().length == 11&&!isPNLocal.isEmpty()){
                    setPhoneLocal(isPNLocal)
                }
            }
        })
        // 输入 对话框默认数据 即 本机号码
        if(phoneId.text.toString().isEmpty()){
            phoneId.setText("17601619521")
            phoneId.setSelection(phoneId.text.length) //将光标移到最后
        }
    }

    //添加 并 更新数据
    private fun addData(key: Int) {
        if (key == 0) {
            if (hfPrice.size == 0) {
                hfPrice.add(TopUpBean("20元", "售价20元")) //话费选项
                hfPrice.add(TopUpBean("30元", "售价30元"))
                hfPrice.add(TopUpBean("50元", "售价50元"))
                hfPrice.add(TopUpBean("100元", "售价100元"))
                hfPrice.add(TopUpBean("300元", "售价299.40元"))
                hfPrice.add(TopUpBean("500元", "售价498.50元"))
            }
            pubAdapter?.setMeNewData(hfPrice)
            czYj.text = hfPrice[0].topPrice //原价
            czYh.text = hfPrice[0].botPrice //优惠
        }
        if (key == 1) {
            if (llPrice.size == 0) {
                llPrice.add(TopUpBean("20M", "售价2.88元")) //流量选项
                llPrice.add(TopUpBean("50M", "售价5.76元"))
                llPrice.add(TopUpBean("100M", "售价4.80元"))
                llPrice.add(TopUpBean("200M", "售价7.68元"))
                llPrice.add(TopUpBean("500M", "售价14.40元"))
                llPrice.add(TopUpBean("1G", "售价19.20元"))
            }
            pubAdapter?.setMeNewData(llPrice)
            czYj.text = llPrice[0].topPrice //原价
            czYh.text = llPrice[0].botPrice //优惠
        }
        if (key == 2) {
            if (ykPrice.size == 0) {
                ykPrice.add(TopUpBean("100元", "")) //油卡选项
                ykPrice.add(TopUpBean("200元", ""))
                ykPrice.add(TopUpBean("500元", ""))
                ykPrice.add(TopUpBean("1000元", ""))
//                ykPrice.add(TopUpBean("2000元", ""))
//                ykPrice.add(TopUpBean("5000元", ""))
            }
            pubAdapter?.setMeNewData(ykPrice)
        }
    }
    //设置方法
    private var isPNLocal:String = ""
    private fun setPhoneLocal(local:String?){
        val str = phoneId.text.toString() //获取输入框信息
        if(str.equals("17601619521")){ //todo 这里需要设置为当前登录的手机号
            val botTit = "默认号码($local)"
            phoneLocalId.text = botTit
        }else{
            phoneLocalId.text = local
        }
    }
    //这里对加油卡的校验
    /**
     * @param type  加油卡类型 1:中石化、2:中石油 //	加油卡卡号，中石化：以100011开头的19位卡号、中石油：以90开头的16位卡号
     * @param content 输入信息判断
     */
    private fun comparisonCardNum(type:Int,content:String):Boolean{
        if(type == 1){ //中石化  以100011开头的19位卡号
            if(!content.startsWith("100011") || content.toString().length != 19){ //如果开头没有包含100011 就不是中石化
                DisplayToast("中石化卡号格式错误")
                return false
            }
        }else if (type == 2){
            if(!content.startsWith("90") || content.toString().length != 16){ //如果开头没有包含90 就不是中石油
                DisplayToast("中石油卡号格式错误")
                return false
            }
        }
        return true
    }
    //手机号校验
    private fun comparisonPhoneNum(content: String):Boolean{
        if(content.length!=11 || !Util.validateMobile(content)){
            DisplayToast("手机号码格式有误")
            return false
        }
        return true
    }
    //在这里添加对加油卡页面监听
    private var OIlType:Int = 2 //油卡类型 默认中石油
    private fun addLayoutPlus(){
        radioAll.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if(checkedId == R.id.chinaSy && OIlType != 2){ //中石油
                    OIlType = 2
                    ykAccount.setText("")
                    ykAccount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
                }
                if(checkedId == R.id.chinaSh && OIlType != 1){ //中石化
                    OIlType = 1
                    ykAccount.setText("")
                    ykAccount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(19))
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
    private fun getPhoneLoca(phoneId: String) {
        var map = hashMapOf<String, String>()
        map.put("phone", phoneId)
        map.put("dtype", "json")
        map.put("key", Api.PhoneKey)
        var phoneMes:String = ""
        JhApiHttpManager.getInstance().post(Api.TELCHECK, map, object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    val jsonObj: JSONObject = JSONObject(str)
                    if (!jsonObj.optString("resultcode").equals("200")) {
                        ToastUtil.showToast(
                            this@TopupActivity,
                            jsonObj.optString("reason")
                        )
                    } else {
                        val result = jsonObj.getJSONObject("result")
                        val province = result.getString("province") //省
                        val city = result.getString("city") //市
                        val company = result.getString("company") //运营商
                        if(province.equals("$city")){
                            phoneMes = "$city$company"
                        }else{
                            phoneMes = "$province$city$company"
                        }
                        LogTool.e("Edit1","onNext  拿到了地址如下：${phoneMes}")
                        this@TopupActivity.runOnUiThread {
                            setPhoneLocal(phoneMes)
                            isPNLocal = phoneMes
                        }
                    }
                }
            }

            override fun onCompleted() {
                LogTool.e("onCompleted","号码归属地查询完成")
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onError","号码归属地查询Error")
            }
        })
    }
}
