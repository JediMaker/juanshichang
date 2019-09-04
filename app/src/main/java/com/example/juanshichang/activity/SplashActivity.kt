package com.example.juanshichang.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.example.juanshichang.MainActivity
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.utils.*
import com.yanzhenjie.permission.AndPermission
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.home_banner_item.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 11:31
 * @文件作用: 首页面
 */
class SplashActivity : FragmentActivity(),View.OnClickListener {
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        StatusBarUtil.addStatusViewWithColor(this, R.color.white)
        val GxmQ:Boolean = MyApp.sp.getBoolean("FIRST", true)
        val edit = MyApp.sp.edit()
        edit.putString("appkey","0371.ml.appkey")
        edit.apply()
        setContentView(R.layout.activity_splash)
        if(GxmQ){ //第一次登录
//            setContentView(R.layout.activity_splash)
            BaseActivity.goStartActivity(this@SplashActivity,GuideActivity())
            finish()
            //首次登录 拿取appKey
        }else{
            splash_img.visibility = View.VISIBLE
            tv.visibility = View.VISIBLE
            //各个第三方的初始化 以及获取版本信息
            Handler().postDelayed(Runnable{
                goActivity(this@SplashActivity)
            },3000)
        }
        tv.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv ->{
                goActivity(this@SplashActivity)
                finish()
            }
        }
    }
    private fun goActivity(context: Context) {
        if(Util.hasLogin()){
            BaseActivity.goStartActivity(context,MainActivity())
        }else{
//            BaseActivity.goStartActivity(context,LoginActivity())
            BaseActivity.goStartActivity(context,MainActivity())
            ToastTool.showToast(this@SplashActivity,"大侠 尚未登录 如何畅游江湖")
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Handler().removeCallbacksAndMessages(0)
    }
}
