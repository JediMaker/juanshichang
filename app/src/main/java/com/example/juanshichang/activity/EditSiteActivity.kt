package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.NewParameter
import com.example.juanshichang.http.JhApiHttpManager
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import kotlinx.android.synthetic.main.activity_edit_site.*
import kotlinx.coroutines.delay
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

class EditSiteActivity : BaseActivity(), View.OnClickListener{
    private val ADDSITE:Int = 1
    private val EDITSITE:Int = 2
    private var siteType:Int = 0
    private var address_id:String = ""
    private var handler = object : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                1 ->{
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
        StatusBarUtil.addStatusViewWithColor(this@EditSiteActivity, R.color.white)
        if (ADDSITE == intent.getIntExtra("type",0) || EDITSITE == intent.getIntExtra("type",0)){
            siteType = intent.getIntExtra("type",0)
            if(ADDSITE == siteType){ //添加地址
                eSTitle.text = "添加收货地址"
//                llCheck.visibility = View.VISIBLE  //根据相关参数 新建不具备设为默认能力

            }
            if (EDITSITE == siteType){//修改地址
                eSTitle.text = "编辑收货地址"
                llCheck.visibility = View.VISIBLE
                removeIt.visibility = View.VISIBLE
                address_id = intent.getStringExtra("address_id")
            }
        }else{
            finish()
        }
    }

    override fun initData() {
        eSRet.setOnClickListener(this) //返回
        saveSite.setOnClickListener(this) //保存
        llCheck.setOnClickListener(this) //设为默认
        removeIt.setOnClickListener(this) //删除收货地址
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
    override fun onClick(v: View?) {
        when(v){
            eSRet ->{
                finish()
            }
            saveSite ->{
                getDataCheckOut()
            }
            llCheck -> {
                val checked = !defCheck.isChecked  //返回下一种状态
                defCheck.isChecked = checked
            }
            removeIt ->{
                if(address_id.isNotEmpty()){
                    deleAddress(address_id)
                }
            }
        }
    }
    private fun getDataCheckOut(){
        val name = sName.text.toString().trim() //名字
        val phone = sPhone.text.toString().trim() //电话号
        val area = sAreas.text.toString().trim()  //地区
        val address_detaill = sAdDet.text.toString().trim()  //详细地址
        var ischeck:Boolean = false
        if(EDITSITE == siteType){
            ischeck = defCheck.isChecked
        }
        if(name.length < 2){
            ToastUtil.showToast(this@EditSiteActivity,"请输入真实的收货人姓名")
            return
        }
        if(phone.length < 11){
            ToastUtil.showToast(this@EditSiteActivity,"请检查输入的手机号码")
            return
        }
        if(area.length < 9 || !area.contains("省") || !area.contains("市")){
            ToastUtil.showToast(this@EditSiteActivity,"请输入省、市、区-县信息")
            return
        }
        if(address_detaill.length<4){
            ToastUtil.showToast(this@EditSiteActivity,"详细地址应精确到住址门牌号等")
            return
        }
        showProgressDialog()
        if(ADDSITE == siteType){ //新建地址
            addAddress(name,phone,address_detaill,area,"44")
        }else if(EDITSITE == siteType && address_id.isNotEmpty() ){//修改地址
            defAddress(name,phone,address_detaill,area,"45",address_id,ischeck)
        }
    }
    //新建地址列表
    private fun addAddress(name:String,phone:String,address_detail:String,city:String,zone_id:String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.ADDADDRESS,
            NewParameter.getNewAdMap(name,phone,address_detail,city,zone_id),
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
    private fun defAddress(name:String,phone:String,address_detail:String,city:String,zone_id:String,address_id:String,default: Boolean){
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.EDITADDRESS,
            NewParameter.getEditAdMap(name,phone,address_detail,city,zone_id,address_id,default),
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
                                this@EditSiteActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            if(default){
                                showMyLoadD(QMUITipDialog.Builder.ICON_TYPE_SUCCESS,"设置成功",true)
                                handler.sendEmptyMessageDelayed(1,1200)
                            }
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
    private fun deleAddress(address_id:String){
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.DELEADDRESS,
            NewParameter.getDeleAdMap(address_id),
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
}
