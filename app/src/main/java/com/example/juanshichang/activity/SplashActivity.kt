package com.example.juanshichang.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 11:31
 * @文件作用:
 */
class SplashActivity : BaseActivity() {

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }*/

    override fun getContentView(): Int {
        return R.layout.activity_splash
    }

    override fun initView() {
        text.onClick {
            goStartActivity(this@SplashActivity,MainActivity())
            text.text = "点击就完事儿了"
            Log.e("tag","点击事件！！！！")
        }
    }

    override fun initData() {

    }
}
