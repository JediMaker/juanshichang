package com.example.juanshichang.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import com.example.juanshichang.MainActivity
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.utils.Util
import kotlinx.android.synthetic.main.activity_splash.*
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
        var GxmQ:Boolean = MyApp.sp?.getBoolean("FIRST", true)!!
        val edit = MyApp.sp.edit()
        edit.putString("appkey","0371.ml.appkey")
        edit.commit()
        setContentView(R.layout.activity_splash)
        if(!GxmQ){
//            setContentView(R.layout.activity_splash)
            frame.visibility = View.VISIBLE
            //首次登录 拿取appKey

        }else{
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
            goActivity(this@SplashActivity)
            Handler().removeCallbacksAndMessages(0)
        }
    }

    private fun goActivity(context: Context) {
        if(Util.hasLogin()){
            BaseActivity.goStartActivity(this@SplashActivity,MainActivity())
        }else{
            BaseActivity.goStartActivity(this@SplashActivity,LoginActivity())
        }
        finish()
    }
}
