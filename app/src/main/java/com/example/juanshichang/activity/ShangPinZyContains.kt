package com.example.juanshichang.activity

import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ShopDetailsAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.ZyProduct

import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.GlideImageLoader
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.glide.GlideUtil
import com.google.gson.Gson
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import kotlinx.android.synthetic.main.activity_shang_pin_zy_contains.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

class ShangPinZyContains : BaseActivity(), View.OnClickListener {
    private var product_id: Long = 0
    private var dialog: Dialog? = null
    //弹窗布局
    private var dShopImg:ImageView? = null  //小图
    private var dFinish:View? = null  //关闭
    private var dShopPrice:TextView? = null //价格
    private var dShopTit:TextView? = null //标题
    private var dList:RecyclerView? = null //列表
    private var dAdapter:ShopDetailsAdapter? = null
    private var dMinusAmount:TextView? = null //-
    private var dAmount:TextView? = null   //数量
    private var dAddAmount:TextView? = null //+
    private var dLeaveWord:EditText? = null //留言
    private var dConfirm:TextView? = null //确定

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

            }
            R.id.spGoGou -> {

            }
            R.id.goZyTop -> {
                nestedZyScrollView.post {
                    nestedZyScrollView.fullScroll(ScrollView.FOCUS_UP) // 滚动至顶部  FOCUS_DOWN 滚动到底部
                }
            }
        }
    }

    override fun initView() {
        if (0.toLong() != intent.getLongExtra("product_id", 0)) {
            product_id = intent.getLongExtra("product_id", 0)
            getZyDetails(product_id) //请求网络数据
        } else {
            ToastUtil.showToast(this@ShangPinZyContains, "数据传输错误,请稍后重试!!!")
            finish()
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
    }

    private fun setUiData(data: ZyProduct.Data, type: Int) {
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
        //todo 加入购物车 弹窗待定
    }

    private fun setBannerData(images: List<ZyProduct.Image>) {
        val bannerList = ArrayList<String>()
        for (i in 0..images.size) {
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

    }

    override fun onStart() {
        super.onStart()
        mBZy?.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        mBZy?.stopAutoPlay()
    }

    //规格弹窗
    private fun PopDialog(dData: ZyProduct.Data, tag: String) { //tag 用于标识 是否已加入购物车等状态
        dialog = Dialog(this@ShangPinZyContains, R.style.Dialog)
        dialog?.setContentView(R.layout.shopdetails_diaog)
        dialog?.apply {
            window?.setGravity(Gravity.BOTTOM)
            window?.setWindowAnimations(R.style.mystyle)//添加动画
        }
        val m = windowManager
        val d = m.defaultDisplay
        val lp = window.attributes
        //设置dialog 横向满屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = (d.height*0.65) as Int
        window.attributes = lp
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
            GlideUtil.loadImage(this@ShangPinZyContains,dData.images[0]?.thumb,dShopImg)
            dShopPrice?.text = dData.special
            dShopTit?.text = dData.model
            dAdapter = ShopDetailsAdapter()
            dList?.adapter = dAdapter
            dAdapter?.setNewData(dData.options)
            //设置按键监听
            dFinish?.setOnClickListener { //结束
                //网络请求

                dialog?.dismiss()
            }
            dMinusAmount?.setOnClickListener { //减少--

            }
            dAddAmount?.setOnClickListener { //增加数量

            }
            dConfirm?.setOnClickListener { //确定

                dialog?.dismiss()
            }
        }
    }

    //--- 网络请求 ------
    //获取商品详情
    private fun getZyDetails(productId: Long) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.PRODUCT,
            NewParameter.getProductMap(productId),
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
                }
            })
    }

    private fun addShopCar() {

    }
}
