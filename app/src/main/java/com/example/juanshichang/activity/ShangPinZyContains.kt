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
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.activity.Defaultcontent.musicurl
import com.example.juanshichang.activity.Defaultcontent.videourl
import com.example.juanshichang.adapter.GoodsImageAdapter
import com.example.juanshichang.adapter.ImageBannerNetAdapter
import com.example.juanshichang.adapter.ShangPinXqAdapter
import com.example.juanshichang.adapter.ShopDetailsAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.HomeBean
import com.example.juanshichang.bean.ZyProduct
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.indicator.NumIndicator
import com.example.juanshichang.utils.*
import com.example.juanshichang.utils.glide.GlideUtil
import com.example.juanshichang.widget.LiveDataBus
import com.google.gson.Gson
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.*
import com.youth.banner.Banner
import com.youth.banner.config.IndicatorConfig
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
    private var checkMap: ConcurrentHashMap<String, ArrayList<String>>? = null //??????????????????????????????
    private var quantity: Int = 1 // ??????

    @field:JvmField
    @BindView(R.id.mBZy)
    var mBZy: Banner<ZyProduct.Image?, GoodsImageAdapter>? = null

    //????????????
    private var dShopImg: ImageView? = null  //??????
    private var dFinish: View? = null  //??????
    private var dShopPrice: TextView? = null //??????
    private var dShopTit: TextView? = null //??????
    private var dList: RecyclerView? = null //??????
    private var dAdapter: ShopDetailsAdapter? = null
    private var dMinusAmount: TextView? = null //-
    private var dAmount: TextView? = null   //??????
    private var dAddAmount: TextView? = null //+
    private var dLeaveWord: EditText? = null //??????
    private var dConfirm: TextView? = null //??????
    private var typeDialog: Int? = 0 //??????????????? ?????? 1 ?????? ??????????????? 2
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
    private var umShareListener = object : UMShareListener {
        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         */
        override fun onResult(p0: SHARE_MEDIA?) {
            Toast.makeText(
                this@ShangPinZyContains,
                "??????                                        ???",
                Toast.LENGTH_LONG
            ).show()
        }

        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         */
        override fun onCancel(p0: SHARE_MEDIA?) {
            val makeText = Toast.makeText(
                this@ShangPinZyContains,
                "?????????", Toast.LENGTH_LONG
            )
            makeText.show();
        }

        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         * @param t ????????????
         */
        override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
            Toast.makeText(
                this@ShangPinZyContains,
                "??? ???" + p1?.message,
                Toast.LENGTH_LONG
            ).show();
        }

        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         */
        override fun onStart(p0: SHARE_MEDIA?) {
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
                LiveDataBus.get().with("mainGo").value = 0
                finish()
            }
            R.id.goShopCar -> {
                if (Util.hasLogin(this)) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    startActivity(intent)
                    LiveDataBus.get().with("mainGo").value = 3 //??????????????????
                    finish()
                }
            }
            R.id.spZySC -> {

                //?????????????????????
                ShareAction(this).withText("hello")
                    .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                    .setCallback(umShareListener).open();
           /*     //????????????????????????
                ShareAction(this)
                    .setPlatform(SHARE_MEDIA.QQ)//????????????
                    .withText("hello")//????????????
                    .setCallback(umShareListener)//???????????????
                    .share();*/
/*
//                   * ??????
//   ?????????ShareAction??????????????????withMedia??????????????????UMImage?????????????????????UMImage??????????????????????????????
//                   *
                var image = UMImage(this, "imageurl");//????????????
//               image = UMImage(this, file);//????????????
                image = UMImage(this, R.drawable.defaultt);//????????????
//               image = UMImage(this, bitmap);//bitmap??????
//               image = UMImage(this, byte[]);//?????????
//                ????????????????????????
//                ShareAction(this).withText("hello").withMedia(image).share();
                ShareAction(this).withText("hello").withMedia(image)
                    .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                    .setCallback(umShareListener).open();*/
//                ????????????
                val web = UMWeb(Defaultcontent.url)
                web.setTitle("This is music title") //??????

                web.setThumb(null) //?????????

                web.setDescription("my description") //??????
               /* ShareAction(this)
                    .withMedia(web)
                    .share();*/
                ShareAction(this).withText("hello").withMedia(web)
                    .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                    .setCallback(umShareListener).open();
              /*  //------------------??????
                val video = UMVideo(videourl)
                video.setTitle("This is music title") //???????????????
                video.setThumb(
                    UMImage(
                        this,
                        "http://www.umeng.com/images/pic/social/chart_1.png"
                    )
                ) //??????????????????

                video.setDescription("my description") //???????????????
                ShareAction(this).withText("hello").withMedia(video).share();*/
              /*  ShareAction(this).withText("hello").withMedia(video)
                    .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                    .setCallback(umShareListener).open();*/
/*//                ??????
                val music = UMusic(musicurl) //?????????????????????

                music.setTitle("This is music title") //???????????????

                music.setThumb(
                    UMImage(
                        this, "http://www.umeng.com/images/pic/social/chart_1.png"
                    )
                ) //??????????????????

                music.setDescription("my description") //???????????????

                music.setmTargetUrl(Defaultcontent.url) //?????????????????????
                ShareAction(this).withMedia(music).share();*/
//                ShareAction(this).withMedia(music)
//                    .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
//                    .setCallback(umShareListener).open();
//                gif
          /*      val emoji = UMEmoji(
                    this,
                    "http://img5.imgtn.bdimg.com/it/u=2749190246,3857616763&fm=21&gp=0.jpg"
                )
                emoji.setThumb(UMImage(this, R.drawable.defaultt))
*//*                ShareAction(this)
                    .withMedia(emoji).share()*//*
                ShareAction(this).withMedia(emoji)
                    .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                    .setCallback(umShareListener).open();*/

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
                    nestedZyScrollView.fullScroll(ScrollView.FOCUS_UP) // ???????????????  FOCUS_DOWN ???????????????
                }
            }
        }
    }

    override fun initView() {
        if (null != intent.getStringExtra("product_id")) {
            product_id = intent.getStringExtra("product_id")
            getZyDetails(product_id) //??????????????????
        } else {
//            ToastUtil.showToast(this@ShangPinZyContains, "??????????????????,???????????????!!!")
//            finish()
            product_id = "30"
            getZyDetails(product_id) //??????????????????
        }
        mBZy
    }

    override fun initData() {
        mZyBL.setOnClickListener(this) //??????
        goZyshop.setOnClickListener(this) //??????
        spZyHome.setOnClickListener(this) //?????????
        goShopCar.setOnClickListener(this) //????????????
        spZySC.setOnClickListener(this) //?????????
        spAddShopCar.setOnClickListener(this) //???????????????
        spGoGou.setOnClickListener(this) //????????????
        goZyTop.setOnClickListener(this) //?????????
        //??????
    }

    private fun setUiData(data: ZyProduct.Data, type: Int) {
        this.data = data
        if (type == 1) { //????????????????????? ??????banner
            setBannerData(data.images)
        }
        spZyName.text = data.model  // ???????????? todo ??????
        if (data.options.size > 0) {//?????????????????????????????????
            countPriceAndStock(data)
            spZyJinEr.text = Util.getGaudyStr("??${realPrice.toString()}")
            try {
                val discountPrice = data.special.toDouble()
                originalZy_cost_view.text = "??${originalPrice}" //????????????
            } catch (e: Exception) {
                originalZy_cost_view.text = realPrice.toString() //????????????
            }

        } else {
            if (!data.price.contains("??")) { //????????????
                val xPrice = "??${data.price}"
                spZyJinEr.text = Util.getGaudyStr(xPrice)
            } else {
                spZyJinEr.text = Util.getGaudyStr(data.price)
            }
            originalZy_cost_view.text = data.price //????????????
        }

        /*       if (!data.price.contains("??")) { //????????????
       //            val xPrice = data.price.substring(1,data.special.length)
       //            val xPrice = data.price.drop(1)// ?????????1???
                   val xPrice = "??${data.price}"
                   spZyJinEr.text = Util.getGaudyStr(xPrice)
               } else {
                   spZyJinEr.text = Util.getGaudyStr(data.price)
               }*/

        shangPinJs.text = data.description  //??????
        //todo ??????????????? ????????????


    }

    private fun setBannerData(images: List<ZyProduct.Image>) {
        val bannerList = ArrayList<String>()
        for (i in 0 until images.size) {
            bannerList.add(images[i].popup)
        }
//        mBZy.setBannerStyle(BannerConfig.NUM_INDICATOR) //?????????????????????
//        mBZy.setIndicatorGravity(BannerConfig.RIGHT)//???????????????
        //?????????????????????
//        mBZy.setImageLoader(GlideImageLoader(1))
        //??????????????????
//        mBZy.setImages(bannerList)
        //??????????????????
//        mBZy.setBannerAnimation(Transformer.Default)
        //???????????????????????????????????????????????????2000???
        mBZy?.setAdapter(GoodsImageAdapter(images))
            ?.setDelayTime(3500)
            ?.setIndicator(NumIndicator(this))
            ?.setIndicatorGravity(IndicatorConfig.Direction.RIGHT)
        //??????????????????????????????????????????????????????
        //????????????????????????????????????????????????????????????????????????????????????
        //meBanner.setBannerTitles(images);
        //banner?????????????????????????????????????????????
        mBZy?.start()
        mBZy?.visibility = View.VISIBLE
        //???????????????????????????...
        setRecycler(bannerList)
    }

    private fun setRecycler(imgUrls: MutableList<String>) {
        zySpList.post(object : Runnable {
            override fun run() {
                zySpList.layoutManager =
                    LinearLayoutManager(this@ShangPinZyContains, RecyclerView.VERTICAL, false)
                //?????????????????????
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
        mBZy?.start()
    }

    override fun onStop() {
        super.onStop()
        mBZy?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    var realPrice: Double = 0.0;//???????????????????????????
    var originalPrice: Double = 0.0;//???????????????????????????
    var minStockNum: Int = 999999999;//???????????????????????????????????????

    //????????????
    private fun PopDialog(dData: ZyProduct.Data, tag: String) { //tag ???????????? ?????????????????????????????????
        dialog = Dialog(this@ShangPinZyContains, R.style.Dialog)
//        dialog?.setContentView(R.layout.shopdetails_diaog)
        /*val window = dialog?.window
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.mystyle)//????????????
        val m = windowManager
        val d = m.defaultDisplay
        val lp = window?.attributes
        //??????dialog ????????????
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        lp?.y = 0 //??????Dialog?????????????????????
//        lp?.height = (d.height*0.7).toInt()
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = lp
        dialog?.show() //??????dialog*/
        val inflate =
            LayoutInflater.from(this@ShangPinZyContains).inflate(R.layout.shopdetails_diaog, null)
        //???????????????
        inflate?.let {
            dShopImg = it.findViewById(R.id.dShopImg)
            dFinish = it.findViewById(R.id.dFinish)
            dShopPrice = it.findViewById(R.id.dShopPrice)
            dShopTit = it.findViewById(R.id.dShopTit)
            dList = it.findViewById(R.id.dList)
            dMinusAmount = it.findViewById(R.id.dMinusAmount)
            dAmount = it.findViewById(R.id.dAmount)
            dAddAmount = it.findViewById(R.id.dAddAmount)
            dLeaveWord = it.findViewById(R.id.dLeaveWord) //??????
            dConfirm = it.findViewById(R.id.dConfirm)
            dialog?.setContentView(inflate)
            var dLeaveWord_product_option_id: String = ""
            val dLeaveWordList: ArrayList<String> = arrayListOf() //?????????????????????
            if (dData.options.size != 0) { //?????????????????? ?????????
                val dt = dData.options
                for (i in 0 until dt.size) {
                    if (dt[i].type.contentEquals("textarea")) {
                        dLeaveWord?.visibility = View.VISIBLE
                        dLeaveWord?.hint = dt[i].value
                        dLeaveWord_product_option_id = dt[i].product_option_id
                        dLeaveWordList.add(dt[i].value) //todo ??????????????????
                        break
                    }
                }
            }
            val dialogWindow = dialog?.window
            dialogWindow?.setGravity(Gravity.BOTTOM)
            dialogWindow?.setWindowAnimations(R.style.mystyle)//????????????
            val lp = dialogWindow?.attributes
            lp?.width = WindowManager.LayoutParams.MATCH_PARENT
            lp?.y = 0
            dialogWindow?.attributes = lp
            //???????????????
            if (dData.images.size != 0) {
                GlideUtil.loadImage(this@ShangPinZyContains, dData.images[0].thumb, dShopImg)
            } else {//???????????????
                GlideUtil.loadImage(this@ShangPinZyContains, "", dShopImg)
            }

            val stockNum: Int = dData.quantity.toString().toInt();//???????????????????????????????????????
            minStockNum = stockNum
            if (dData.options.size > 0) {//?????????????????????????????????
                var isMultiOption: Boolean = false
                for (op in dData?.options) {
                    if (op.product_option_value.size > 0) {
                        isMultiOption = true
                    }
                }
                if (isMultiOption) {
                    countPriceAndStock(dData)
                    dShopTit?.text = "??????${minStockNum}???"

                } else {
                    dShopTit?.text = "??????${dData.quantity}???"
                }
                dShopPrice?.text = Util.getGaudyStr("??${realPrice.toString()}")
            } else {
                if (!dData.price.contains("??")) { //????????????
                    val xPrice = "??${dData.price}"
                    dShopPrice?.text = Util.getGaudyStr(xPrice)
                } else {
                    dShopPrice?.text = Util.getGaudyStr(dData.price)
                }
                dShopTit?.text = "??????${dData.quantity}???"
            }

            dAdapter = ShopDetailsAdapter()
            dList?.adapter = dAdapter
            dAdapter?.setTheData(dData)
            dAdapter?.setOnChangeOptionValueListener(object :
                ShopDetailsAdapter.OnChangeOptionValueListener {
                override fun onChangeOptionValue() {
                    //????????????????????????????????????????????????????????????
                    realPrice = 0.0;//???????????????????????????
                    minStockNum = 999999999
                    checkMap = dAdapter?.getAllCheck() as ConcurrentHashMap//????????????????????????
                    if (dData.options.size > 0) {//?????????????????????????????????
                        if (dData.price.contains("??")) { //
                            dData.price = dData.price.substring(1, dData.price.length)
                            dData.price = dData.price.replace(",", "")
                        }
                        if (dData.special.toString().contains("??")) { //?????????????????????????????????
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
                        for (option in dData.options) {//?????????????????????????????????
                            var increPrice: Double = 0.0
                            for ((k, v) in checkMap!!) {
                                if (k == option.product_option_id) {
                                    if (option.product_option_value.size > 0) {
                                        for (opValue in option.product_option_value) {
                                            if (opValue.product_option_value_id.toString()
                                                    .equals(v[0].toString())
                                            ) {
                                                if (opValue.quantity.toInt() < minStockNum) {//???????????????
                                                    minStockNum = opValue.quantity.toInt()
                                                }
                                                if (opValue.price.contains("??")) {
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
                        dShopTit?.text = "??????${minStockNum}???"
                        dShopPrice?.text = Util.getGaudyStr("??${realPrice.toString()}")
                    }
                    if (minStockNum > 0) {
                        dConfirm?.isEnabled = true
                    } else {
                        dConfirm?.isEnabled = false
                    }
                }

            });
            if (minStockNum > 0) {
                dConfirm?.isEnabled = true
            } else {
                dConfirm?.isEnabled = false
            }
            checkMap?.let {
                dAdapter?.setAllCheck(it)
            }
            dAmount?.text = "$quantity"
            //??????????????????
            dFinish?.setOnClickListener {
                //??????
                dialog?.dismiss()
            }
            dMinusAmount?.setOnClickListener {
                //??????--
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
                //????????????
                if (quantity >= minStockNum) {
                    dAddAmount?.isEnabled = false
                    ToastUtil.showToast(this@ShangPinZyContains!!, "??????????????????????????????")
                    return@setOnClickListener
                } else {
                    quantity++
                }
                if (quantity == 2) { //?????? - ?????????
                    dMinusAmount?.isEnabled = true
                }
                dAmount?.text = "$quantity"
            }
            dConfirm?.setOnClickListener {
                if (!Util.hasLogin()) {
                    ToastTool.showToast(this@ShangPinZyContains, "???????????? ????????????")
                    val intent = Intent(this@ShangPinZyContains, Reg2LogActivity::class.java)
                    intent.putExtra("type", Reg2LogActivity.LOGINCODE) // ????????????
                    goStartActivity(this@ShangPinZyContains, intent)
                } else {
                    //??????
                    checkMap = dAdapter?.getAllCheck() as ConcurrentHashMap//????????????????????????
                    checkMap?.put(dLeaveWord_product_option_id, dLeaveWordList)
                    showProgressDialog()
                    if (typeDialog == 1) { //???????????????
                        addShopCar(product_id!!, quantity, checkMap!!, 1)
                    } else if (typeDialog == 2) { //????????????
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
     *??????????????????????????????????????????
     */
    private fun countPriceAndStock(dData: ZyProduct.Data) {
        //????????????????????????????????????????????????????????????
        realPrice = 0.0;//???????????????????????????
        originalPrice = 0.0;//???????????????????????????
        minStockNum = 999999999
        if (dData.price.contains("??")) { //
            dData.price = dData.price.substring(1, dData.price.length)
            dData.price = dData.price.replace(",", "")
        }
        if (dData.special.toString().contains("??")) { //?????????????????????????????????
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
        for (option in dData.options) {//?????????????????????????????????
            var increPrice: Double = 0.0
            if (option.product_option_value.size > 0) {
                if (option.product_option_value[0].price.contains("??")) {
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
                //?????????????????????????????????
                for (productOptionValue in option.product_option_value) {
                    if (productOptionValue.quantity.toInt() < minStockNum) {
                        minStockNum = productOptionValue.quantity.toInt()
                    }
                }
            }
        }
    }

    //--- ???????????? ------
    //??????????????????
    private fun getZyDetails(productId: String?) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.PRODUCT,
            NewParameter.getProductMap(productId!!),
            object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo???????????????????????????????????????????????????
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
                    LogTool.e("onCompleted", "Zy????????????????????????")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "Zy????????????????????????: ${e.toString()}")
                    ToastTool.showToast(
                        this@ShangPinZyContains,
                        "????????????error:${e.toString()}"
                    )
                    finish()

                }
            })
    }

    /**
     * @param type  ???????????? 1:???????????????  2????????????
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
                    //todo???????????????????????????????????????????????????
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
                                showMyLoadD(QMUITipDialog.Builder.ICON_TYPE_SUCCESS, "????????????", true)
                                handler.sendEmptyMessageDelayed(1, 1500)
                            }
                            if (typeDialog == 2) { //????????????  ??????????????? ???????????????
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
                    LogTool.e("onCompleted", "??????????????????????????????")
                    this@ShangPinZyContains.runOnUiThread {
                        dismissProgressDialog()
                    }
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "??????????????????????????????: ${e.toString()}")
                    this@ShangPinZyContains.runOnUiThread {
                        ToastUtil.showToast(this@ShangPinZyContains, "????????????,???????????????...")
                    }
                }
            })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
    }
}
