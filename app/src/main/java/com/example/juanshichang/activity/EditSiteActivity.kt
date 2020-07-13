package com.example.juanshichang.activity

import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import chihane.jdaddressselector.AddressProvider
import chihane.jdaddressselector.AddressSelector
import chihane.jdaddressselector.OnAddressSelectedListener
import chihane.jdaddressselector.model.City
import chihane.jdaddressselector.model.County
import chihane.jdaddressselector.model.Province
import chihane.jdaddressselector.model.Street
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.bean.AreaZoneBean
import com.example.juanshichang.bean.SiteBean
import com.example.juanshichang.bean.ZoneBean
import com.example.juanshichang.dialog.BottomAreaDialog
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.qmuiteam.qmui.widget.popup.QMUIPopup
import com.qmuiteam.qmui.widget.popup.QMUIPopups
import kotlinx.android.synthetic.main.activity_edit_site.*
import org.jetbrains.anko.textColorResource
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

class EditSiteActivity : BaseActivity(), View.OnClickListener, OnAddressSelectedListener {
    private val ADDSITE: Int = 1
    private val EDITSITE: Int = 2
    private var siteType: Int = 0
    private var address_id: String = ""
    private var zoneList: List<ZoneBean.Zone>? = null
    private var zoneProvinceList: List<AreaZoneBean.AreaData>? = null
    private var zoneCityList: List<AreaZoneBean.AreaData>? = null
    private var zoneStreetList: List<AreaZoneBean.AreaData>? = null
    private var provinces: List<Province>? = null
    private var cities: List<City>? = null
    private var counties: List<County>? = null
    private var cityName: String = ""
    private var city_id: String = ""
    private var provinceName: String = ""
    private var province_id: String = ""
    private var countyName: String = ""
    private var county_id: String = ""
    private var zone: String? = null //用于处理编辑数据问题
    private var zoneId: String? = null //用于处理编辑数据问题
    private var selector: AddressSelector? = null
    private var dialog: BottomAreaDialog? = null
    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    myLoading?.dismiss()
                    finish()
                    removeMessages(1)
                }
            }
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_edit_site
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@EditSiteActivity, R.color.colorPrimary)
        if (ADDSITE == intent.getIntExtra("type", 0) || EDITSITE == intent.getIntExtra("type", 0)) {
            siteType = intent.getIntExtra("type", 0)
            if (ADDSITE == siteType) { //添加地址
                eSTitle.text = "添加收货地址"
                llCheck.visibility = View.VISIBLE  //根据相关参数 新建不具备设为默认能力
            }
            if (EDITSITE == siteType) {//修改地址
                eSTitle.text = "编辑收货地址"
                llCheck.visibility = View.VISIBLE
                removeIt.visibility = View.VISIBLE
                //设置内容
                val data = intent.getParcelableExtra<SiteBean.Addresse>("data")
                val defId = intent.getStringExtra("defid")  //获取默认地址参数
                data?.let {
                    address_id = it.address_id
                    cityName = "${if (it.city != null) it.city else ""}"
                    city_id = "${if (it.city_id != null) it.city_id else ""}"
                    provinceName = "${if (it.province != null) it.province else ""}"
                    province_id = "${if (it.province_id != null) it.province_id else ""}"
                    countyName = "${if (it.county != null) it.county else ""}"
                    county_id = "${if (it.county_id != null) it.county_id else ""}"
                    sName.setText(it.name)
                    sPhone.setText(it.iphone)
                    zone = it.zone
                    sArea.textColorResource = R.color.main_text
                    sArea.text =
                        "${if (it.province != null) it.province else ""} ${if (it.city != null) it.city else ""} ${if (it.county != null) it.county else ""}"
                    sAdDet.setText(it.address_detail)
                    if (defId?.contentEquals(it.address_id)!!) {
                        defCheck.isChecked = true //默认地址设为选中
                        removeIt.visibility = View.VISIBLE
                    }
                }
            }
            selector = AddressSelector(this)
            selector?.setOnAddressSelectedListener(this)
            selector!!.setAddressProvider(object : AddressProvider {
                override fun provideProvinces(addressReceiver: AddressProvider.AddressReceiver<Province?>) {
                    getAddressZoneList("", "1", addressReceiver, null, null)
                }

                override fun provideCitiesWith(
                    provinceId: Int,
                    addressReceiver: AddressProvider.AddressReceiver<City?>
                ) {

                    getAddressZoneList(provinceId.toString(), "2", null, addressReceiver, null)
                }

                override fun provideCountiesWith(
                    cityId: Int,
                    addressReceiver: AddressProvider.AddressReceiver<County?>
                ) {
                    getAddressZoneList(cityId.toString(), "3", null, null, addressReceiver)
                }

                override fun provideStreetsWith(
                    countyId: Int,
                    addressReceiver: AddressProvider.AddressReceiver<Street?>?
                ) {
                    // blahblahblah
                    addressReceiver?.send(null)
                }
            })

        } else {
            finish()
        }

    }

    override fun initData() {
        eSRet.setOnClickListener(this) //返回
        saveSite.setOnClickListener(this) //保存
        llCheck.setOnClickListener(this) //设为默认
        removeIt.setOnClickListener(this) //删除收货地址
        sArea.setOnClickListener(this) //设置所在地区
    }

    override fun onResume() {
        super.onResume()
//        getAddressList(0)//请求地址
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onClick(v: View?) {
        when (v) {
            eSRet -> {
                finish()
            }
            saveSite -> { //保存地址
                getDataCheckOut()
            }
            llCheck -> { //设为默认地址
                val checked = !defCheck.isChecked  //返回下一种状态
                defCheck.isChecked = checked
            }
            removeIt -> { //删除地址
                if (address_id.isNotEmpty()) {
                    deleAddress(address_id)
                }
            }
            sArea -> {
//                showPop(v!!)

//                BottomDialog.show(MainActivity.this, MainActivity.this);
                dialog = BottomAreaDialog(this, selector?.view)
//                dialog.setOnAddressSelectedListener(this)
                dialog?.show()
            }
        }
    }

    private fun getDataCheckOut() {
        val name = sName.text.toString().trim() //名字
        val phone = sPhone.text.toString().trim() //电话号
//        val area = sAreas.text.toString().trim()  //地区
        val area = sArea.text.toString().trim()  //地区
        val address_detaill = sAdDet.text.toString().trim()  //详细地址
        var ischeck: Boolean = false
        ischeck = defCheck.isChecked
        if (name.length < 2) {
            ToastUtil.showToast(this@EditSiteActivity, "请输入真实的收货人姓名")
            return
        }
        if (phone.length < 11) {
            ToastUtil.showToast(this@EditSiteActivity, "请检查输入的手机号码")
            return
        }
        if ("所在地区".equals(area) && area.length < 4) { //!area.contains("省") || !area.contains("市")
            ToastUtil.showToast(this@EditSiteActivity, "请输入省、市、区-县信息")
//            getAddressList(1, sArea)
            return
        }
        if (address_detaill.length < 6) {
            ToastUtil.showToast(this@EditSiteActivity, "详细地址应精确到住址门牌号等")
            return
        }
        /*  if (!address_detaill.contains("市") && !address_detaill.contains("县") && !address_detaill.contains(
                  "区"
              )
          ) {
              ToastUtil.showToast(this@EditSiteActivity, "请确认详细地址是否包含地市！！！")
              return
          }*/
        showProgressDialog()
        val newAddress_detaill = address_detaill.replace(" ", "") //去掉详细地址中的空格
        if (ADDSITE == siteType) { //新建地址
            addAddress(
                name,
                phone,
                newAddress_detaill,
                cityName,
                city_id,
                provinceName,
                province_id,
                countyName,
                county_id,
                "${if (ischeck) {
                    "1"
                } else {
                    ""
                }}"
            )
        } else if (EDITSITE == siteType && address_id.isNotEmpty()) {//修改地址
            defAddress(
                name,
                phone,
                newAddress_detaill,
                cityName,
                city_id,
                address_id,
                provinceName,
                province_id,
                countyName,
                county_id,
                "${if (ischeck) {
                    "1"
                } else {
                    ""
                }}"
            )
        }
    }

    private var mNormalPopup: QMUIPopup? = null
    private fun showPop(view: View?) {
        if (zoneList != null) {
            zoneList?.let {
                val list: ArrayList<String> = arrayListOf()
                for (value in it) { //创建数据集合
                    list.add(value.name)
                }
                //初始化 pop
                val adpater: ArrayAdapter<String> =
                    ArrayAdapter(this@EditSiteActivity, R.layout.simple_llist_item, list)
                val onItemClickListener = object : AdapterView.OnItemClickListener {
                    override fun onItemClick(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        sArea.text = "中国 ${it[position].name}"
                        zoneId = it[position].zone_id
                        mNormalPopup?.dismiss()
                    }
                }
                if (mNormalPopup == null) {
                    mNormalPopup = QMUIPopups.listPopup(
                        this@EditSiteActivity,
                        QMUIDisplayHelper.dp2px(this, 250),
                        QMUIDisplayHelper.dp2px(this, 300), adpater, onItemClickListener
                    )
                        .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                        .preferredDirection(QMUIPopup.DIRECTION_TOP)
                        .shadow(true)
                        .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                        .onDismiss { //用于处理选中逻辑

                        }
                        .show(view!!)
                } else {
                    mNormalPopup?.show(view!!)
                }
            }
        } else {
            getAddressList(1, view!!)
        }
    }

    // --------------------------------------- 网络请求 -------------------------------------------------
    //新建地址列表

    private fun addAddress(
        name: String,
        phone: String,
        address_detail: String,
        city: String,
        city_id: String,
        province: String,
        province_id: String,
        county: String,
        county_id: String,
        default: String
    ) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.ADDADDRESS,
            NewParameter.getNewAdMap(
                name,
                phone,
                address_detail,
                city,
                city_id,
                province,
                province_id,
                county,
                county_id,
                default
            ),
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
                        /*       if (!jsonObj?.optString(JsonParser.JSON_CODE)!!.equals(JsonParser.JSON_SUCCESS)) {
                                    ToastUtil.showToast(
                                    this@EditSiteActivity,
                                    jsonObj.optString(JsonParser.JSON_MSG)
                                )
                            }
                               */
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            ToastUtil.showToast(
                                this@EditSiteActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            finish()
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "新建地址请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "新建地址请求失败: ${e.toString()}")
                }
            })
    }

    //修改默认地址
    private fun defAddress(
        name: String,
        phone: String,
        address_detail: String,
        city: String,
        city_id: String,
        address_id: String,
        province: String,
        province_id: String,
        county: String,
        county_id: String,
        default: String
    ) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.EDITADDRESS,
            NewParameter.getEditAdMap(
                name,
                phone,
                address_detail,
                city,
                city_id,
                address_id,
                province,
                province_id,
                county,
                county_id,
                default
            ),
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
                                this@EditSiteActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            if ("1".equals(default)) {
                                showMyLoadD(QMUITipDialog.Builder.ICON_TYPE_SUCCESS, "设置成功", true)
                                handler.sendEmptyMessageDelayed(1, 1200)
                            }
                            finish()
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "修改地址请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "修改地址请求失败: ${e.toString()}")
                }
            })
    }

    //删除地址
    private fun deleAddress(address_id: String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.DELEADDRESS,
            NewParameter.getDeleAdMap(address_id),
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
                                this@EditSiteActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            finish()
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "删除地址请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "删除地址请求失败: ${e.toString()}")
                }
            })
    }

    //获取地址列表
    private fun getAddressList(type: Int, vararg v: View) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.ADDRESSZONES,
            NewParameter.getBaseZMap(),
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
                                this@EditSiteActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val data = Gson().fromJson(t, ZoneBean.ZoneBeans::class.java)
                            zoneList = data.data.zone
                            if (type != 0) {//0 为 首次请求 保存数据  其他要 直接显示
                                showPop(v as View)
                            } else {
                                //设置默认 参数
                                if (zoneList?.size != 0 && EDITSITE != siteType) { //新建处理
                                    sArea.textColorResource = R.color.main_text
                                    sArea.text = "${data.data.name} ${zoneList!![0].name}"
                                    zoneId = zoneList!![0].zone_id
                                }
                                if (zoneList?.size != 0 && EDITSITE == siteType) { //编辑处理
                                    zoneList?.let {
                                        if (zone != null) {
                                            var index: Int = 0
                                            for (i in 0 until it.size) {
                                                if (it[i].name.contains(zone!!)) {
                                                    index = i
                                                    break
                                                }
                                            }
                                            this@EditSiteActivity.runOnUiThread(object : Runnable{
                                                override fun run() {
                                                    sArea.textColorResource = R.color.main_text
                                                    sArea.text = "${data.data.name} ${it[index].name}"
                                                }
                                            })

                                            zoneId = it[index].zone_id
                                        } else {
                                            finish()
                                        }
                                    }

                                }
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "默认地址列表请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "默认地址列表请求失败: ${e.toString()}")
                }
            })
    }

    //获取省市区三级地址列表，每次请求获取一级数据
    private fun getAddressZoneList(
        pid: String,
        status: String,
        addressReceiver: AddressProvider.AddressReceiver<Province?>?,
        addressReceiver2: AddressProvider.AddressReceiver<City?>?,
        addressReceiver3: AddressProvider.AddressReceiver<County?>?
    ) {
        var areaDataList: MutableList<Any> = ArrayList()
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.GET_REGIONS,
            NewParameter.getRegionsMap(pid, status),
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
                            when (status) {
                                "1" -> {//省级数据
                                    addressReceiver?.send(provinces)
                                }
                                "2" -> {//市级数据
                                    addressReceiver2?.send(cities)
                                }
                                "3" -> {//县（区）级数据
                                    addressReceiver3?.send(counties)
                                }
                            }
                            ToastUtil.showToast(
                                this@EditSiteActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            try {
                                val data = Gson().fromJson(t, AreaZoneBean.AreaZoneBean::class.java)
                                when (status) {
                                    "1" -> {//省级数据
                                        zoneProvinceList = data.data!!
                                        provinces = listOf()
                                        for (provinceData in zoneProvinceList!!) {
                                            val province = Province();
                                            province.id = provinceData.pid.toInt()
                                            province.name = provinceData.name
                                            provinces = provinces?.plus(province)
                                        }
                                        addressReceiver?.send(provinces)
                                    }
                                    "2" -> {//市级数据
                                        zoneCityList = data.data!!
                                        cities = listOf()
                                        for (provinceData in zoneCityList!!) {
                                            val city = City();
                                            city.id = provinceData.pid.toInt()
                                            city.name = provinceData.name
                                            city.province_id = pid.toInt()
                                            cities = cities?.plus(city)
                                        }
                                        addressReceiver2?.send(cities)
                                    }
                                    "3" -> {//县（区）级数据
                                        zoneStreetList = data.data!!
                                        counties = listOf()
                                        for (provinceData in zoneStreetList!!) {
                                            val country = County();
                                            country.id = provinceData.pid.toInt()
                                            country.name = provinceData.name
                                            country.city_id = pid.toInt()
                                            counties = counties?.plus(country)
                                        }
                                        addressReceiver3?.send(counties)
                                    }
                                }
                            } catch (e: Exception) {
                                when (status) {
                                    "1" -> {//省级数据
                                        addressReceiver?.send(provinces)
                                    }
                                    "2" -> {//市级数据
                                        addressReceiver2?.send(cities)
                                    }
                                    "3" -> {//县（区）级数据
                                        addressReceiver3?.send(counties)
                                    }
                                }
                            }


                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "默认地址列表请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "默认地址列表请求失败: ${e.toString()}")
                }
            })
    }

    override fun onAddressSelected(
        province: Province?,
        city: City?,
        county: County?,
        street: Street?
    ) {
        dialog?.dismiss()
        sArea.textColorResource = R.color.main_text
        sArea.text =
            "${if (province?.name != null) province?.name else ""} ${if (city?.name != null) city?.name else ""} ${if (county?.name != null) county?.name else ""}"
        provinceName = province?.name!!
        cityName = city?.name!!
        countyName = county?.name!!
        for (pData in zoneProvinceList!!) {
            if (pData.pid.toString().equals(province?.id.toString())) {
                province_id = pData.id
                break
            }
        }
        for (pData in zoneCityList!!) {
            if (pData.pid.toString().equals(city?.id.toString())) {
                city_id = pData.id
            }
        }
        for (pData in zoneStreetList!!) {
            if (pData.pid.toString().equals(county?.id.toString())) {
                county_id = pData.id
            }
        }
    }
}
