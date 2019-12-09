package com.example.juanshichang.activity

import android.app.Dialog
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ShangPinXqAdapter
import com.example.juanshichang.adapter.ShopDetailsAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.ZyProduct

import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.*
import com.example.juanshichang.utils.glide.GlideUtil
import com.google.gson.Gson
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import kotlinx.android.synthetic.main.activity_shang_pin_zy_contains.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber
import java.util.concurrent.ConcurrentHashMap

class ShangPinZyContains : BaseActivity(), View.OnClickListener {
    private var product_id:String? = null
    private var dialog: Dialog? = null
    private var data: ZyProduct.Data? = null
    private var adapterSp: ShangPinXqAdapter? = null
    private var checkMap: ConcurrentHashMap<String, ArrayList<String>>? = null //这个是选择的数据集合
    private var quantity:Int = 1 // 数量
    //弹窗布局
    private var dShopImg: ImageView? = null  //小图
    private var dFinish: View? = null  //关闭
    private var dShopPrice: TextView? = null //价格
    private var dShopTit: TextView? = null //标题
    private var dList: RecyclerView? = null //列表
    private var dAdapter: ShopDetailsAdapter? = null
    private var dMinusAmount: TextView? = null //-
    private var dAmount: TextView? = null   //数量
    private var dAddAmount: TextView? = null //+
    private var dLeaveWord: EditText? = null //留言
    private var dConfirm: TextView? = null //确定
    private var typeDialog: Int? = 0 //确定弹窗是 加购 1 还是 直接去购买 2
    private var handler = object: Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                1 ->{
                    if(Util.ifCurrentActivityTopStack(this@ShangPinZyContains)){
                        myLoading?.dismiss()
                        removeMessages(1)
                    }
                }
            }
        }
    }
    override fun getContentView(): Int {
        return R.layout.activity_shang_pin_zy_contains
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mZyBL -> {
                finish()
            }
            R.id.goZyshop -> {

            }
            R.id.spZyHome -> {

            }
            R.id.goShopCar -> {

            }
            R.id.spZySC -> {

            }
            R.id.spAddShopCar -> {
                typeDialog = 1
                if (ButtonSubmit.isFastDoubleClick()) {
                    return
                } else {
                    data?.let {
                        PopDialog(it, "")
                    }
                }
            }
            R.id.spGoGou -> {
                typeDialog = 2
                if (ButtonSubmit.isFastDoubleClick()) {
                    return
                } else {
                    data?.let {
                        PopDialog(it, "")
                    }
                }
            }
            R.id.goZyTop -> {
                nestedZyScrollView.post {
                    nestedZyScrollView.fullScroll(ScrollView.FOCUS_UP) // 滚动至顶部  FOCUS_DOWN 滚动到底部
                }
            }
        }
    }

    override fun initView() {
        if (null != intent.getStringExtra("product_id")) {
            product_id = intent.getStringExtra("product_id")
            getZyDetails(product_id) //请求网络数据
        } else {
//            ToastUtil.showToast(this@ShangPinZyContains, "数据传输错误,请稍后重试!!!")
//            finish()
            product_id = "30"
            getZyDetails(product_id) //请求网络数据
        }
    }

    override fun initData() {
        mZyBL.setOnClickListener(this) //返回
        goZyshop.setOnClickListener(this) //进店
        spZyHome.setOnClickListener(this) //回首页
        goShopCar.setOnClickListener(this) //去购物车
        spZySC.setOnClickListener(this) //加收藏
        spAddShopCar.setOnClickListener(this) //加入购物车
        spGoGou.setOnClickListener(this) //立即购买
        goZyTop.setOnClickListener(this) //回顶部
        //清空
    }

    private fun setUiData(data: ZyProduct.Data, type: Int) {
        this.data = data
        if (type == 1) { //第一次刷新页面 更新banner
            setBannerData(data.images)
        }
        spZyName.text = data.model  // 商品名称 todo 待定
        if (data.special.contains("¥")) { //设置现价
//            val xSpecial = data.special.substring(1,data.special.length)
            val xSpecial = data.special.drop(1)// 舍弃前1个
            spZyJinEr.text = xSpecial
        } else {
            spZyJinEr.text = data.special
        }
        originalZy_cost_view.text = data.price //获取原价
        shangPinJs.text = data.description  //描述
        //todo 加入购物车 弹窗待定

    }

    private fun setBannerData(images: List<ZyProduct.Image>) {
        val bannerList = ArrayList<String>()
        for (i in 0 until images.size) {
            bannerList.add(images[i].popup)
        }
        mBZy.setBannerStyle(BannerConfig.NUM_INDICATOR) //显示数字指示器
        mBZy.setIndicatorGravity(BannerConfig.RIGHT)//指示器居右
        //设置图片加载器
        mBZy.setImageLoader(GlideImageLoader(1))
        //设置图片集合
        mBZy.setImages(bannerList)
        //设置动画效果
        mBZy.setBannerAnimation(Transformer.Default)
        //设置轮播图片间隔时间（不设置默认为2000）
        mBZy.setDelayTime(3500)
        //设置是否自动轮播（不设置则默认自动）
        mBZy.isAutoPlay(true)
        //设置轮播要显示的标题和图片对应（如果不传默认不显示标题）
        //meBanner.setBannerTitles(images);
        //banner设置方法全部调用完毕时最后调用
        mBZy.start()
        mBZy.visibility = View.VISIBLE
        //这里初始化商品详情...
        setRecycler(bannerList)
    }

    private fun setRecycler(imgUrls: MutableList<String>) {
        zySpList.post(object : Runnable {
            override fun run() {
                zySpList.layoutManager =
                    LinearLayoutManager(this@ShangPinZyContains, RecyclerView.VERTICAL, false)
                //瀑布流加载图片
//        shangpinList.layoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
                adapterSp = ShangPinXqAdapter()
                zySpList.adapter = adapterSp
                zySpList.setHasFixedSize(false)
                zySpList.setPadding(0, 0, 0, botSp.height + 3)
                adapterSp?.setNewData(imgUrls)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        mBZy?.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        mBZy?.stopAutoPlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
    //规格弹窗
    private fun PopDialog(dData: ZyProduct.Data, tag: String) { //tag 用于标识 是否已加入购物车等状态
        dialog = Dialog(this@ShangPinZyContains, R.style.Dialog)
        dialog?.setContentView(R.layout.shopdetails_diaog)
        val window = dialog?.window
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.mystyle)//添加动画
        val m = windowManager
        val d = m.defaultDisplay
        val lp = window?.attributes
        //设置dialog 横向满屏
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        lp?.y = 0 //设置Dialog距离底部的距离
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = lp
        dialog?.show() //弹出dialog
        //初始化控件
        dialog?.let {
            dShopImg = it.findViewById(R.id.dShopImg)
            dFinish = it.findViewById(R.id.dFinish)
            dShopPrice = it.findViewById(R.id.dShopPrice)
            dShopTit = it.findViewById(R.id.dShopTit)
            dList = it.findViewById(R.id.dList)
            dMinusAmount = it.findViewById(R.id.dMinusAmount)
            dAmount = it.findViewById(R.id.dAmount)
            dAddAmount = it.findViewById(R.id.dAddAmount)
            dLeaveWord = it.findViewById(R.id.dLeaveWord)
            dConfirm = it.findViewById(R.id.dConfirm)
            //设置数据等
            GlideUtil.loadImage(this@ShangPinZyContains, dData.images[0]?.thumb, dShopImg)
            dShopPrice?.text = dData.special
            dShopTit?.text = dData.model
            dAdapter = ShopDetailsAdapter()
            dList?.adapter = dAdapter
            dAdapter?.setTheData(dData.options)
            checkMap?.let {
                dAdapter?.setAllCheck(it)
            }
            dAmount?.text = "$quantity"
            //设置按键监听
            dFinish?.setOnClickListener {
                //结束
                dialog?.dismiss()
            }
            dMinusAmount?.setOnClickListener {
                //减少--
                if(quantity < 2){
                    return@setOnClickListener
                }else{
                    quantity --
                    if(quantity == 1){
                        dMinusAmount?.isEnabled = false
                    }
                }
                dAmount?.text = "$quantity"
            }
            dAddAmount?.setOnClickListener {
                //增加数量
                if(quantity >= 99){
                    dAddAmount?.isEnabled = false
                    return@setOnClickListener
                }else{
                    quantity ++
                }
                if(quantity == 2){ //设置 - 可点击
                    dMinusAmount?.isEnabled = true
                }
                dAmount?.text = "$quantity"
            }
            dConfirm?.setOnClickListener {
                //确定
                checkMap= dAdapter?.getAllCheck() as  ConcurrentHashMap//把选中的信息返回
                showProgressDialog()
                if(typeDialog == 1){ //加入购物车
                    addShopCar(product_id!!,quantity,checkMap!!)
                }else if(typeDialog == 2){ //立即购买
//                    addShopCar(product_id!!,quantity,checkMap!!) //先加入购物车

                    dismissProgressDialog()
                }
                dialog?.dismiss()
            }
        }
    }

    //--- 网络请求 ------
    //获取商品详情
    private fun getZyDetails(productId: String?) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.PRODUCT,
            NewParameter.getProductMap(productId!!),
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
                                this@ShangPinZyContains,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val dataAll = Gson().fromJson(t, ZyProduct.ZyProducts::class.java)
                            val data = dataAll.data
                            this@ShangPinZyContains.runOnUiThread {
                                setUiData(data, 1)
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "Zy商品详情请求完成")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "Zy商品详情请求失败: ${e.toString()}")
                    this@ShangPinZyContains.runOnUiThread {
                        finish()
                    }
                }
            })
    }

    private fun addShopCar(productId: String,quantity:Int,checkMap:ConcurrentHashMap<String,ArrayList<String>>?) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.CARTADD,
            NewParameter.getAddSCMap(productId,quantity,checkMap!!),
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
                                this@ShangPinZyContains,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            this@ShangPinZyContains.runOnUiThread {
                                if(typeDialog == 1){
                                    showMyLoadD(QMUITipDialog.Builder.ICON_TYPE_SUCCESS,"加购成功",true)
                                    handler.sendEmptyMessageDelayed(1,1500)
                                }
                                if(typeDialog == 2){

                                }
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "商品加购物车请求完成")
                    this@ShangPinZyContains.runOnUiThread {
                        dismissProgressDialog()
                    }
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "商品加购物车请求失败: ${e.toString()}")
                    this@ShangPinZyContains.runOnUiThread {
                        ToastUtil.showToast(this@ShangPinZyContains,"网络异常,请稍后重试...")
                    }
                }
            })
    }
}
