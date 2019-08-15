package com.example.juanshichang.activity

import android.app.ActivityManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.juanshichang.MainActivity
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(),View.OnClickListener {


    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@SettingActivity, R.color.white)
        unlogin.setOnClickListener(this)
        setRet.setOnClickListener(this)
    }

    override fun initData() {

    }
    override fun onClick(v: View?) {
        when(v){
            unlogin ->{
                this.showDialog(
                    BaseActivity.TOAST_WARN,
                    "确认退出吗？",
                    "确定",
                    "取消",
                    object : BaseActivity.DialogCallback {
                        override fun sure() {
                            Thread {
                                kotlin.run {
                                    Glide.get(this@SettingActivity).clearDiskCache()//磁盘缓存清理
                                }
                            }.start()
                            Log.e("uuid","执行清理程序")
                            MyApp.sp.edit().remove("uu").commit()
                            SpUtil.getIstance().getDelete()
                            Util.removeCookie(this@SettingActivity)
                            ToastUtil.showToast(this@SettingActivity,"清理完成")
                            BaseActivity.goStartActivity(this@SettingActivity, MainActivity())
                            finish()
                        }

                        override fun cancle() {

                        }

                    })
            }
            setRet ->{
                this@SettingActivity.finish()
            }
        }
    }
    override fun getContentView(): Int {
        return R.layout.activity_setting
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_setting)
//    }
}
