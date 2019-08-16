package com.example.juanshichang.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.text.Layout
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.adapter.ShangPinXqAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.*
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.GlideImageLoader
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.glide.GlideUtil
import com.example.juanshichang.widget.MyScrollView
import com.google.gson.Gson
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import kotlinx.android.synthetic.main.activity_shang_pin_contains.*
import org.json.JSONObject
import rx.Subscriber

class ShangPinContains : BaseActivity(), View.OnClickListener {
    var goods_id: Long = 0 //从正常列表进入
    var theme_id: Long = 0  //Banner进入
    //    var mall_name: String? = null //店铺名称
    val goods_id_def: Long = 0
    var goods: SDB.GoodsDetail? = null
    var goodsPromotionUrl: PSP.GoodsPromotionUrl? = null
    var cm: ClipboardManager? = null
    var mClipData: ClipData? = null
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    mClipData =
                        ClipData.newPlainText("Label", goodsPromotionUrl?.mobile_short_url)// 创建URL型ClipData
                    cm?.setPrimaryClip(mClipData!!)  // 将ClipData内容放到系统剪贴板里
                    ToastUtil.showToast(this@ShangPinContains, "已复制到粘贴板ds")
                }
                2 -> {
                    ToastUtil.showToast(this@ShangPinContains, "该商品暂无分享链接")
                }
            }
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_shang_pin_contains
    }

    override fun initView() {
        meBanner.visibility = View.INVISIBLE
        if (goods_id_def != intent.getLongExtra("goods_id", 0)) { //&& null != intent.getStringExtra("mall_name")
            goods_id = intent.getLongExtra("goods_id", 0)
//            mall_name = intent.getStringExtra("mall_name")
            searchDetailList(Api.Pdd, goods_id)
        } else if(goods_id_def != intent.getLongExtra("theme_id", 0)){ //从banner 传入  todo
            theme_id = intent.getLongExtra("theme_id", 0)
//            searchDetailListBanner(theme_id)
        } else {
            ToastUtil.showToast(this@ShangPinContains, "数据传输错误,请稍后重试!!!")
            finish()
        }

    }

    override fun initData() {
        cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager //获取系统粘贴板管理器
        mbackLayouts.setOnClickListener(this) //返回
        go_shop.setOnClickListener(this) //进店
        spHome.setOnClickListener(this) //回首页
        spShouCang.setOnClickListener(this) //加收藏
        spJia.setOnClickListener(this)  //分享
        spGou.setOnClickListener(this) //领劵
        goTop.setOnClickListener(this) //回顶部
    }

    override fun onClick(view: View?) {
        when (view) {
            mbackLayouts -> {
                finish()
            }
            goTop ->{
                nestedScrollView.post(object:kotlinx.coroutines.Runnable {
                    override fun run() {
                        nestedScrollView.fullScroll(ScrollView.FOCUS_UP) //// 滚动至顶部  FOCUS_DOWN 滚动到底部
                    }
                })
            }
            go_shop -> {
                ToastUtil.showToast(this@ShangPinContains, "暂未开放店铺入口")
            }
            spHome -> {
                var intent = Intent(this@ShangPinContains, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                finish()
            }
            spShouCang -> {
                ToastUtil.showToast(this@ShangPinContains, "暂未开放收藏入口")
            }
            spJia -> {
                if (Util.hasLogin()) {
                    if (null == goodsPromotionUrl) {
                        sharePath(1, goods_id, Api.Pdd)
                    } else {
                        if (!TextUtils.isEmpty(goodsPromotionUrl?.mobile_short_url)) {
                            mClipData =
                                ClipData.newPlainText("Label", goodsPromotionUrl?.mobile_short_url)// 创建URL型ClipData
                            cm?.setPrimaryClip(mClipData!!)  // 将ClipData内容放到系统剪贴板里
                            ToastUtil.showToast(this@ShangPinContains, "已复制到粘贴板s")
                        } else {
                            if (!TextUtils.isEmpty(goodsPromotionUrl?.mobile_url)) {
                                mClipData =
                                    ClipData.newPlainText(
                                        "Label",
                                        goodsPromotionUrl?.mobile_url
                                    )// 创建URL型ClipData
                                cm?.setPrimaryClip(mClipData!!)  // 将ClipData内容放到系统剪贴板里
                                ToastUtil.showToast(this@ShangPinContains, "已复制到粘贴板L")
                            } else {
                                ToastUtil.showToast(this@ShangPinContains, "暂无可复制内容")
                            }
                        }
                    }
                } else {
//                    goStartActivity(this@ShangPinContains, LoginActivity())
                    goStartActivity(this@ShangPinContains, Reg2LogActivity())
                }
            }
            spGou -> {
                if (Util.hasLogin()) {
                    var intent = Intent(this@ShangPinContains, WebActivity::class.java)
                    if (!TextUtils.isEmpty(goodsPromotionUrl?.mobile_short_url)) {
                        intent.putExtra("mobile_short_url", goodsPromotionUrl?.mobile_short_url)
                    }
                    if (!TextUtils.isEmpty(goodsPromotionUrl?.mobile_url)) {
                        intent.putExtra("mobile_url", goodsPromotionUrl?.mobile_url)
                    }
                    startActivity(intent)
                } else {
//                    goStartActivity(this@ShangPinContains, LoginActivity())
                    goStartActivity(this@ShangPinContains, Reg2LogActivity())
                }
            }
            else -> {

            }
        }
    }

    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_shang_pin_contains)
//    }
    //正常列表进入获取商品详细信息
    private fun searchDetailList(servicer: String, goods_id: Long) {
        HttpManager.getInstance().post(Api.SEARCHDETAIL, Parameter.getSearchDetailsMap(servicer, goods_id), object :
            Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@ShangPinContains, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        var searchDetailBean = Gson().fromJson(str, SDB.SearchDetailBean::class.java)
                        goods = searchDetailBean.data.goods_detail_response.goods_details.get(0)
                        if (null != goods) {
                            this@ShangPinContains.runOnUiThread(object : Runnable {
                                override fun run() {
                                    setData(goods!!)
                                }
                            })
                        }
                        if (Util.hasLogin()) {
                            sharePath(0, goods_id, Api.Pdd)
                        }
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted", "商品详情加载完成1!")
            }

            override fun onError(e: Throwable?) {
                Log.e("onError", "商品详情加载失败1!" + e)
            }
        })
    }
    /**
     * 获取分享链接 必须登录
     */
    private fun sharePath(isHandler: Int, goods_id: Long, servicer: String) { //不为0 则启动handler刷新
        HttpManager.getInstance()
            .post(Api.SHARE, Parameter.getShareMap("login", goods_id, servicer), object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        jsonObj = JSONObject(str)
                        if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(this@ShangPinContains, jsonObj.optString(JsonParser.JSON_MSG))
                        } else {
//                        val pddSharePath: PddSharePath = Gson().fromJson(str, PddSharePath::class.java)
                            val pddSharePath = Gson().fromJson(str, PSP.PddSharePath::class.java)
                            goodsPromotionUrl =
                                pddSharePath.data.goods_promotion_url_generate_response.goods_promotion_url_list[0]
                            if (null != goodsPromotionUrl && isHandler != 0) {
                                handler.sendEmptyMessage(1)
                            }
                            if (null == goodsPromotionUrl) {
                                handler.sendEmptyMessage(2)
                            }
                        }
                    }

                }

                override fun onCompleted() {
                    Log.e("onCompleted", "分享链接加载完成!")
                }

                override fun onError(e: Throwable?) {
                    Log.e("onError", "分享链接加载失败!" + e)
                }

            })
    }

    private fun setData(goods: SDB.GoodsDetail) {
        setBanner(goods.goods_gallery_urls as MutableList<String>) // 初始化bannner
        spName.text = goods.goods_name //名称
        spJinEr.text = Util.getFloatPrice(goods.min_group_price.toLong()) //最低价sku的拼团价，单位为分
        original_cost_view.text = Util.getFloatPrice(goods.min_normal_price.toLong()) //最低价sku的单买价，单位为分
        getTags(goods.service_tags)//服务
        spjian.text = goods.sales_tip //销量
        desc_txt.text = goods.desc_txt //描述分
        serv_txt.text = goods.serv_txt //服务分
        lgst_txt.text = goods.lgst_txt //物流分
        shop_name.text = goods.mall_name //店铺名
        shangPinJs.text = goods.goods_desc //介绍
        if(!goods.has_coupon){ //是否有优惠劵
            spYhj.visibility = View.GONE
            spGou.text = "立即购买"
        }else{
            //这里可以判断 用户优惠劵 是否过期....
            if((goods.coupon_total_quantity - goods.coupon_remain_quantity) < goods.coupon_total_quantity){ //总数 和 剩余
                val juan = Util.getIntPrice(goods.coupon_discount.toLong())
                sPYHme.text = "$juan 元优惠劵"
                sPYHDate.text = "有效日期:" + Util.getDateToString(goods.coupon_start_time.toLong())+"到"+Util.getDateToString(goods.coupon_end_time.toLong())
                spGou.text = "立即购买\n(省$juan 元)"
            }else{ //这里进行优惠劵数量为0的处理
                //暂时设置为隐藏吧!!!
                spYhj.visibility = View.GONE
                spGou.text = "立即购买"
            }
        }
        val promotionRate = Util.getFloatPrice(goods.promotion_rate.toLong())
        sPSY.text = "收益:$promotionRate"
        spJia.text = "分享\n(收益$promotionRate 元)"
        GlideUtil.loadImage(this,goods.goods_thumbnail_url,shop_img)

        //获取SocllView的高度  ScrollView组件只允许一个子View，可以利用这一个特性，获取子View的高度即所要的ScrollView的整体高度
        nestedScrollView.setOnScrollListener(object : MyScrollView.OnScrollListener{
            override fun onScroll(scrollY: Int) {
                if(scrollY > 450){
                    goTop.visibility = View.VISIBLE
                }else{
                    goTop.visibility = View.GONE
                }
            }
        })
    }
    private fun getTags(serviceTags: List<Int>){  //返回支持的服务
        if (serviceTags.size != 0) {
            var sbs: ArrayList<String> = ArrayList()
            for (index in 1 until serviceTags.size) {
                when (serviceTags[index]) {
                    4 -> {
                        sbs.add("送货入户并安装;")
                    }
                    5 -> {
                        sbs.add("送货入户;")
                    }
                    6 -> {
                        sbs.add("电子发票;")
                    }
                    9 -> {
                        sbs.add("坏果包赔;")
                    }
                    11 -> {
                        sbs.add("闪电退款;")
                    }
                    12 -> {
                        sbs.add("24小时发货;")
                    }
                    13 -> {
                        sbs.add("48小时发货;")
                    }
                    17 -> {
                        sbs.add("顺丰包邮;")
                    }
                    18 -> {
                        sbs.add("只换不修;")
                    }
                    19 -> {
                        sbs.add("全国联保;")
                    }
                    20 -> {
                        sbs.add("分期付款;")
                    }
                    24 -> {
                        sbs.add("极速退款;")
                    }
                    25 -> {
                        sbs.add("品质保障;")
                    }
                    26 -> {
                        sbs.add("缺重包退;")
                    }
                    27 -> {
                        sbs.add("当日发货;")
                    }
                    28 -> {
                        sbs.add("可定制化;")
                    }
                    29 -> {
                        sbs.add("预约配送;")
                    }
                    1000001 -> {
                        sbs.add("正品发票;")
                    }
                    1000002 -> {
                        sbs.add("送货入户并安装;")
                    }
                    else -> {

                    }
                }

            }
            if(sbs.size >= 4){
                spShouyi.text = sbs[0]
                spShouyi2.text = sbs[1]
                spShouyi3.text = sbs[2]
                spShouyi4.text = sbs[3]
            }
            if(sbs.size >= 3){
                spShouyi.text = sbs[0]
                spShouyi2.text = sbs[1]
                spShouyi3.text = sbs[2]
                spShouyi4.visibility = View.INVISIBLE
            }
            if(sbs.size >= 2){
                spShouyi.text = sbs[0]
                spShouyi2.text = sbs[1]
                spShouyi3.visibility = View.INVISIBLE
                spShouyi4.visibility = View.INVISIBLE
            }
            if(sbs.size >= 1){
                spShouyi.text = sbs[0]
                spShouyi2.visibility = View.INVISIBLE
                spShouyi3.visibility = View.INVISIBLE
                spShouyi4.visibility = View.INVISIBLE
            }
        }else{
            shangPinFw.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        //开始轮播
        meBanner.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        //结束轮播
        meBanner.stopAutoPlay()
    }

    private fun setBanner(imgUrls: MutableList<String>) {
        meBanner.visibility = View.VISIBLE
        meBanner.setBannerStyle(BannerConfig.NUM_INDICATOR) //显示数字指示器
        //设置指示器位置（当banner模式中有指示器时）
        meBanner.setIndicatorGravity(BannerConfig.RIGHT)//指示器居右
        //设置图片加载器
        meBanner.setImageLoader(GlideImageLoader(1))
        //设置图片集合
        meBanner.setImages(imgUrls)
        //设置动画效果
        meBanner.setBannerAnimation(Transformer.Default)
        //设置轮播图片间隔时间（不设置默认为2000）
        meBanner.setDelayTime(4500)
        //设置是否自动轮播（不设置则默认自动）
        meBanner.isAutoPlay(true)
        //设置轮播要显示的标题和图片对应（如果不传默认不显示标题）
        //meBanner.setBannerTitles(images);
        //banner设置方法全部调用完毕时最后调用
        meBanner.start()
        //这里初始化商品详情
        shangpinList.layoutManager = LinearLayoutManager(this@ShangPinContains,RecyclerView.VERTICAL,false)
        val adapterSp = ShangPinXqAdapter(R.layout.item_shangpin_xiangqing,imgUrls)
        shangpinList.adapter = adapterSp
        shangpinList.setPadding(0,0,0,botShangpin.height+3)
        adapterSp.notifyDataSetChanged()
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
