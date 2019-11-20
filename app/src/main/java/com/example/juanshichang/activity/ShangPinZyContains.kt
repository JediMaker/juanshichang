package com.example.juanshichang.activity

import android.view.View
import android.widget.ScrollView
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.ZyProduct

import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.GlideImageLoader
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import kotlinx.android.synthetic.main.activity_shang_pin_zy_contains.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

class ShangPinZyContains : BaseActivity(), View.OnClickListener {
    private var product_id:Long = 0
    override fun getContentView(): Int {
        return R.layout.activity_shang_pin_zy_contains
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.mZyBL ->{
                finish()
            }
            R.id.goZyshop ->{

            }
            R.id.spZyHome ->{

            }
            R.id.goShopCar ->{

            }
            R.id.spZySC ->{

            }
            R.id.spAddShopCar ->{

            }
            R.id.spGoGou ->{

            }
            R.id.goZyTop ->{
                nestedZyScrollView.post {
                    nestedZyScrollView.fullScroll(ScrollView.FOCUS_UP) // 滚动至顶部  FOCUS_DOWN 滚动到底部
                }
            }
        }
    }
    override fun initView() {
        if(0.toLong() != intent.getLongExtra("product_id",0)){
            product_id = intent.getLongExtra("product_id",0)

        }else{
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
    private fun setUiData(data: ZyProduct.Data,type:Int) {
        if(type == 1){ //第一次刷新页面 更新banner
            setBannerData(data.images)
        }
        spZyName.text = data.model  // 商品名称 todo 待定
        if(data.special.contains("¥")){ //设置现价
//            val xSpecial = data.special.substring(1,data.special.length)
            val xSpecial = data.special.drop(1)// 舍弃前1个
            spZyJinEr.text = xSpecial
        }else{
            spZyJinEr.text = data.special
        }
        originalZy_cost_view.text = data.price //获取原价

    }

    private fun setBannerData(images: List<ZyProduct.Image>) {
        val bannerList = ArrayList<String>()
        for(i in 0..images.size){
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
    private fun getZyDetails(productId:Long){
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(Api.PRODUCT, NewParameter.getProductMap(productId),object : Subscriber<String>(){
            override fun onNext(t: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(t!!)) {
                    var jsonObj: JSONObject? = null
                    try {
                        jsonObj = JSONObject(t)
                    } catch (e: JSONException) {
                        e.printStackTrace();
                    }
                    if (!jsonObj?.optString(JsonParser.JSON_CODE)!!.equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@ShangPinZyContains, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val dataAll = Gson().fromJson(t,ZyProduct.ZyProducts::class.java)
                        val data = dataAll.data
                        this@ShangPinZyContains.runOnUiThread {
                            setUiData(data,1)
                        }
                    }
                }
            }

            override fun onCompleted() {
                LogTool.e("onCompleted","Zy商品详情请求完成")
            }

            override fun onError(e: Throwable?) {
                LogTool.e("onCompleted","Zy商品详情请求失败: ${e.toString()}")
            }
        })
    }
}
