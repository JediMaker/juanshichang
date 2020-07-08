package com.example.juanshichang.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.juanshichang.MainActivity
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
import com.example.juanshichang.widget.LiveDataBus
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
    private var product_id: String? = null
    private var dialog: Dialog? = null
    private var data: ZyProduct.Data? = null
    private var adapterSp: ShangPinXqAdapter? = null
    private var checkMap: ConcurrentHashMap<String, ArrayList<String>>? = null //这个是选择的数据集合
    private var quantity: Int = 1 // 数量

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
    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    if (Util.ifCurrentActivityTopStack(this@ShangPinZyContains)) {
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
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                finish()
            }
            R.id.goShopCar -> {
                if (Util.hasLogin(this)) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    startActivity(intent)
                    LiveDataBus.get().with("mainGo").value = 3 //返回到购物车
                    finish()
                }
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
        if (data.options.size > 0) {//多规格商品价格展示设置
            countPriceAndStock(data)
            spZyJinEr.text = Util.getGaudyStr("¥${realPrice.toString()}")
            try {
                val discountPrice = data.special.toDouble()
                originalZy_cost_view.text = "¥${originalPrice}" //获取原价
            } catch (e: Exception) {
                originalZy_cost_view.text = realPrice.toString() //获取原价
            }

        } else {
            if (!data.price.contains("¥")) { //设置现价
                val xPrice = "¥${data.price}"
                spZyJinEr.text = Util.getGaudyStr(xPrice)
            } else {
                spZyJinEr.text = Util.getGaudyStr(data.price)
            }
            originalZy_cost_view.text = data.price //获取原价
        }

        /*       if (!data.price.contains("¥")) { //设置现价
       //            val xPrice = data.price.substring(1,data.special.length)
       //            val xPrice = data.price.drop(1)// 舍弃前1个
                   val xPrice = "¥${data.price}"
                   spZyJinEr.text = Util.getGaudyStr(xPrice)
               } else {
                   spZyJinEr.text = Util.getGaudyStr(data.price)
               }*/

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

    var realPrice: Double = 0.0;//多规格商品实际价格
    var originalPrice: Double = 0.0;//多规格商品实际价格
    var minStockNum: Int = 999999999;//多规格库存数量展示最小库存

    //规格弹窗
    private fun PopDialog(dData: ZyProduct.Data, tag: String) { //tag 用于标识 是否已加入购物车等状态
        dialog = Dialog(this@ShangPinZyContains, R.style.Dialog)
//        dialog?.setContentView(R.layout.shopdetails_diaog)
        /*val window = dialog?.window
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.mystyle)//添加动画
        val m = windowManager
        val d = m.defaultDisplay
        val lp = window?.attributes
        //设置dialog 横向满屏
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        lp?.y = 0 //设置Dialog距离底部的距离
//        lp?.height = (d.height*0.7).toInt()
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = lp
        dialog?.show() //弹出dialog*/
        val inflate =
            LayoutInflater.from(this@ShangPinZyContains).inflate(R.layout.shopdetails_diaog, null)
        //初始化控件
        inflate?.let {
            dShopImg = it.findViewById(R.id.dShopImg)
            dFinish = it.findViewById(R.id.dFinish)
            dShopPrice = it.findViewById(R.id.dShopPrice)
            dShopTit = it.findViewById(R.id.dShopTit)
            dList = it.findViewById(R.id.dList)
            dMinusAmount = it.findViewById(R.id.dMinusAmount)
            dAmount = it.findViewById(R.id.dAmount)
            dAddAmount = it.findViewById(R.id.dAddAmount)
            dLeaveWord = it.findViewById(R.id.dLeaveWord) //备注
            dConfirm = it.findViewById(R.id.dConfirm)
            dialog?.setContentView(inflate)
            var dLeaveWord_product_option_id: String = ""
            val dLeaveWordList: ArrayList<String> = arrayListOf() //初始化留言集合
            if (dData.options.size != 0) { //这里动态显示 留言框
                val dt = dData.options
                for (i in 0 until dt.size) {
                    if (dt[i].type.contentEquals("textarea")) {
                        dLeaveWord?.visibility = View.VISIBLE
                        dLeaveWord?.hint = dt[i].value
                        dLeaveWord_product_option_id = dt[i].product_option_id
                        dLeaveWordList.add(dt[i].value) //todo 暂时先这么写
                        break
                    }
                }
            }
            val dialogWindow = dialog?.window
            dialogWindow?.setGravity(Gravity.BOTTOM)
            dialogWindow?.setWindowAnimations(R.style.mystyle)//添加动画
            val lp = dialogWindow?.attributes
            lp?.width = WindowManager.LayoutParams.MATCH_PARENT
            lp?.y = 0
            dialogWindow?.attributes = lp
            //设置数据等
            if (dData.images.size != 0) {
                GlideUtil.loadImage(this@ShangPinZyContains, dData.images[0].thumb, dShopImg)
            } else {//设置空数据
                GlideUtil.loadImage(this@ShangPinZyContains, "", dShopImg)
            }

            val stockNum: Int = dData.quantity.toString().toInt();//多规格库存数量展示最小库存
            minStockNum = stockNum
            if (dData.options.size > 0) {//多规格商品价格展示设置
                var isMultiOption: Boolean = false
                for (op in dData?.options) {
                    if (op.product_option_value.size > 0) {
                        isMultiOption = true
                    }
                }
                if (isMultiOption) {
                    countPriceAndStock(dData)
                    dShopTit?.text = "库存${minStockNum}件"

                } else {
                    dShopTit?.text = "库存${dData.quantity}件"
                }
                dShopPrice?.text = Util.getGaudyStr("¥${realPrice.toString()}")
            } else {
                if (!dData.price.contains("¥")) { //设置现价
                    val xPrice = "¥${dData.price}"
                    dShopPrice?.text = Util.getGaudyStr(xPrice)
                } else {
                    dShopPrice?.text = Util.getGaudyStr(dData.price)
                }
                dShopTit?.text = "库存${dData.quantity}件"
            }

            dAdapter = ShopDetailsAdapter()
            dList?.adapter = dAdapter
            dAdapter?.setTheData(dData)
            dAdapter?.setOnChangeOptionValueListener(object :
                ShopDetailsAdapter.OnChangeOptionValueListener {
                override fun onChangeOptionValue() {
                    //根据选中的规格属性重新设置价格和库存数量
                    realPrice = 0.0;//多规格商品实际价格
                    minStockNum = 999999999
                    checkMap = dAdapter?.getAllCheck() as ConcurrentHashMap//把选中的信息返回
                    if (dData.options.size > 0) {//多规格商品价格展示设置
                        if (dData.price.contains("¥")) { //
                            dData.price = dData.price.substring(1, dData.price.length)
                            dData.price = dData.price.replace(",", "")
                        }
                        if (dData.special.toString().contains("¥")) { //促销价格为实际商品售价
                            dData.special = dData.special.substring(1, dData.special.length)
                            dData.special = dData.special.replace(",", "")
                        }
                        var priceNomal: Double = 0.0
                        try {
                            priceNomal = dData.special.toDouble()
                        } catch (e: Exception) {
                            priceNomal = dData.price.toDouble()
                        }
                        realPrice = UtilsBigDecimal.add(priceNomal, realPrice)
                        for (option in dData.options) {//初始多规格商品金额展示
                            var increPrice: Double = 0.0
                            for ((k, v) in checkMap!!) {
                                if (k == option.product_option_id) {
                                    if (option.product_option_value.size > 0) {
                                        for (opValue in option.product_option_value) {
                                            if (opValue.product_option_value_id.toString()
                                                    .equals(v[0].toString())
                                            ) {
                                                if (opValue.quantity.toInt() < minStockNum) {//取最小库存
                                                    minStockNum = opValue.quantity.toInt()
                                                }
                                                if (opValue.price.contains("¥")) {
                                                    opValue.price =
                                                        opValue.price.substring(
                                                            1,
                                                            opValue.price.length
                                                        )
                                                }
                                                opValue.price =
                                                    opValue.price.replace(",", "")
                                                try {
                                                    increPrice = opValue.price.toDouble()
                                                } catch (e: Exception) {
                                                    increPrice = 0.0
                                                }

                                                if ("+".equals(opValue.price_prefix)) {
                                                    realPrice =
                                                        UtilsBigDecimal.add(
                                                            realPrice,
                                                            increPrice
                                                        )
                                                } else if ("-".equals(opValue.price_prefix)) {
                                                    realPrice =
                                                        UtilsBigDecimal.sub(
                                                            realPrice,
                                                            increPrice
                                                        )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                        dShopTit?.text = "库存${minStockNum}件"
                        dShopPrice?.text = Util.getGaudyStr("¥${realPrice.toString()}")
                    }


                }

            });
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
                if (quantity < 2) {
                    return@setOnClickListener
                } else if (quantity <= minStockNum) {
                    dAddAmount?.isEnabled = true
                    quantity--
                    if (quantity == 1) {
                        dMinusAmount?.isEnabled = false
                    }
                }
                dAmount?.text = "$quantity"
            }
            dAddAmount?.setOnClickListener {
                //增加数量
                if (quantity >= minStockNum) {
                    dAddAmount?.isEnabled = false
                    ToastUtil.showToast(this@ShangPinZyContains!!, "该商品不能购买更多了")
                    return@setOnClickListener
                } else {
                    quantity++
                }
                if (quantity == 2) { //设置 - 可点击
                    dMinusAmount?.isEnabled = true
                }
                dAmount?.text = "$quantity"
            }
            dConfirm?.setOnClickListener {
                if (!Util.hasLogin()) {
                    ToastTool.showToast(this@ShangPinZyContains, "尚未登录 请先登录")
                    val intent = Intent(this@ShangPinZyContains, Reg2LogActivity::class.java)
                    intent.putExtra("type", Reg2LogActivity.LOGINCODE) // 显示登录
                    intent.putExtra("one", "1")
                    goStartActivity(this@ShangPinZyContains, intent)
                } else {
                    //确定
                    checkMap = dAdapter?.getAllCheck() as ConcurrentHashMap//把选中的信息返回
                    checkMap?.put(dLeaveWord_product_option_id, dLeaveWordList)
                    showProgressDialog()
                    if (typeDialog == 1) { //加入购物车
                        addShopCar(product_id!!, quantity, checkMap!!, 1)
                    } else if (typeDialog == 2) { //立即购买
                        addShopCar(product_id!!, quantity, checkMap!!, 2)
                        showProgressDialog()
                    }
                }
                dialog?.dismiss()
            }
            if (quantity > 1) {
                dMinusAmount?.isEnabled = true
            }
            dialog?.show()
        }
    }

    /**
     *计算多规格商品初始价格和库存
     */
    private fun countPriceAndStock(dData: ZyProduct.Data) {
        //根据选中的规格属性重新设置价格和库存数量
        realPrice = 0.0;//多规格商品实际价格
        originalPrice = 0.0;//多规格商品原始价格
        minStockNum = 999999999
        if (dData.price.contains("¥")) { //
            dData.price = dData.price.substring(1, dData.price.length)
            dData.price = dData.price.replace(",", "")
        }
        if (dData.special.toString().contains("¥")) { //促销价格为实际商品售价
            dData.special = dData.special.substring(1, dData.special.length)
            dData.special = dData.special.replace(",", "")
        }
        var priceNomal: Double = 0.0
        try {
            priceNomal = dData.special.toDouble()
        } catch (e: Exception) {
            priceNomal = dData.price.toDouble()
        }
        realPrice = UtilsBigDecimal.add(priceNomal, realPrice)
        originalPrice = UtilsBigDecimal.add(dData.price.toDouble(), originalPrice)
        for (option in dData.options) {//初始多规格商品金额展示
            var increPrice: Double = 0.0
            if (option.product_option_value.size > 0) {
                if (option.product_option_value[0].price.contains("¥")) {
                    option.product_option_value[0].price =
                        option.product_option_value[0].price.substring(
                            1,
                            option.product_option_value[0].price.length
                        )
                }
                option.product_option_value[0].price =
                    option.product_option_value[0].price.replace(",", "")
                try {
                    increPrice = option.product_option_value[0].price.toDouble()
                } catch (e: Exception) {
                    increPrice = 0.0
                }

                if ("+".equals(option.product_option_value[0].price_prefix)) {
                    realPrice =
                        UtilsBigDecimal.add(
                            realPrice,
                            increPrice
                        )
                    originalPrice = UtilsBigDecimal.add(
                        originalPrice,
                        increPrice
                    )
                } else if ("-".equals(option.product_option_value[0].price_prefix)) {
                    realPrice =
                        UtilsBigDecimal.sub(
                            realPrice,
                            increPrice
                        )
                    originalPrice =
                        UtilsBigDecimal.sub(
                            originalPrice,
                            increPrice
                        )
                }
                //计算多规格商品最小库存
                for (productOptionValue in option.product_option_value) {
                    if (productOptionValue.quantity.toInt() < minStockNum) {
                        minStockNum = productOptionValue.quantity.toInt()
                    }
                }
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
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
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
                    ToastTool.showToast(
                        this@ShangPinZyContains,
                        "返回数据error:${e.toString()}"
                    )
                    finish()

                }
            })
    }

    /**
     * @param type  参数判定 1:接入购物车  2立即购买
     */
    private fun addShopCar(
        productId: String,
        quantity: Int,
        checkMap: ConcurrentHashMap<String, ArrayList<String>>?,
        type: Int = 1
    ) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.CARTADD,
            NewParameter.getAddSCMap(productId, quantity, checkMap!!),
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
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                            ToastUtil.showToast(
                                this@ShangPinZyContains,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            if (type == 1) {
                                showMyLoadD(QMUITipDialog.Builder.ICON_TYPE_SUCCESS, "加购成功", true)
                                handler.sendEmptyMessageDelayed(1, 1500)
                            }
                            if (typeDialog == 2) { //立即购买  加入购物车 并提交订单
                                dismissProgressDialog()
                                val data = jsonObj.getJSONObject("data")
                                data.let {
                                    val cartId: String = it.getString("cart_id")
                                    val intent = Intent(
                                        this@ShangPinZyContains,
                                        ConOrderActivity::class.java
                                    )
                                    val bundle = Bundle()
                                    bundle.putStringArrayList(
                                        "checkAll",
                                        arrayListOf<String>(cartId)
                                    )
                                    intent.putExtra("bundle", bundle)
                                    startActivity(intent)
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
                        ToastUtil.showToast(this@ShangPinZyContains, "网络异常,请稍后重试...")
                    }
                }
            })
    }
}
