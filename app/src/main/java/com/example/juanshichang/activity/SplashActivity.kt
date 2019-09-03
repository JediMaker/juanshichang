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
 * @文件作用:
 */
class SplashActivity : FragmentActivity() {

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
            setBanner()
            //首次登录 拿取appKey
            MyApp.requestPermission(this@SplashActivity)
        }else{
            splash_img.visibility = View.VISIBLE
            tv.visibility = View.VISIBLE
            //各个第三方的初始化 以及获取版本信息
            Handler().postDelayed(Runnable{
                goActivity(this@SplashActivity)
//                BaseActivity.goStartActivity(this@SplashActivity,MainActivity())
//                finish()
            },3000)
        }
        tv.setOnClickListener {
//            BaseActivity.goStartActivity(this@SplashActivity,MainActivity())
//            finish()
            if(GxmQ){
                MyApp.sp.edit().putBoolean("FIRST", false).apply()
            }
            goActivity(this@SplashActivity)
            finish()
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
    private fun setBanner(){
        sBanner.visibility = View.VISIBLE
        val dataList:List<Int> = mutableListOf(R.drawable.spone,R.drawable.sptwo,R.drawable.spthree)
        sBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR) //显示数字指示器
        //设置指示器位置（当banner模式中有指示器时）
        sBanner.setIndicatorGravity(BannerConfig.CENTER)//指示器居右
        //设置图片加载器
        sBanner.setImageLoader(GlideImageLoader(1))
        //设置动画效果
        sBanner.setBannerAnimation(Transformer.ZoomOutSlide)
        sBanner.setImages(dataList)
        sBanner.setOnBannerListener(object : OnBannerListener{
            override fun OnBannerClick(position: Int) { //点击事件监听

            }
        })
        var oldP:Int = 0
        sBanner.setOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                LogTool.e("eeeeee1","11:$state")
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                LogTool.e("eeeeee","1:$position  2:$positionOffset  3:$positionOffsetPixels")
            }

            override fun onPageSelected(position: Int) {
                if(dataList.size-1 == position){
                    tv.visibility = View.VISIBLE
                    tv.text = "进入应用"
                }else{
                    tv.visibility = View.INVISIBLE
                }
                oldP = position
            }

        })
        sBanner.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        Handler().removeCallbacksAndMessages(0)
    }
}
