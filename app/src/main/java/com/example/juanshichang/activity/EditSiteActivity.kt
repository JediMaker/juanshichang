package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_edit_site.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

class EditSiteActivity : BaseActivity(), View.OnClickListener{
    private val ADDSITE:Int = 1
    private val EDITSITE:Int = 2
    private var siteType:Int = 0
    override fun getContentView(): Int {
        return R.layout.activity_edit_site
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@EditSiteActivity, R.color.white)
        if (ADDSITE == intent.getIntExtra("type",0) || EDITSITE == intent.getIntExtra("type",0)){
            siteType = intent.getIntExtra("type",0)
            if(ADDSITE == siteType){ //添加地址
                eSTitle.text = "添加收货地址"
                llCheck.visibility = View.VISIBLE
            }
            if (EDITSITE == siteType){//修改地址
                eSTitle.text = "编辑收货地址"
                removeIt.visibility = View.VISIBLE

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

            }
        }
    }
    private fun getDataCheckOut(){
        val name = sName.text.toString().trim() //名字
        val phone = sPhone.text.toString().trim() //电话号
        val area = sAreas.text.toString().trim()  //地区
        val address_detaill = sAdDet.text.toString().trim()  //详细地址
        var ischeck:Boolean? = null
        if(ADDSITE == siteType){
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
        if(area.length < 9){
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
        }else if(EDITSITE == siteType){//修改地址

        }

    }
    //获取地址列表
    private fun addAddress(name:String,phone:String,address_detaill:String,city:String,zone_id:String) {
        JhApiHttpManager.getInstance(Api.NEWBASEURL).post(
            Api.ADDADDRESS,
            NewParameter.getEditAdMap(name,phone,address_detaill,city,zone_id,ADDSITE),
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
                    LogTool.e("onCompleted", "Zy商品详情请求完成")
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onCompleted", "Zy商品详情请求失败: ${e.toString()}")
                }
            })
    }

}
