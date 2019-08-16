package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.utils.StatusBarUtil
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.activity_earnings.*

/**
 * @作者: yzq
 * @创建日期: 2019/8/16 15:03
 * @文件作用: 我的收益页面
 */
class EarningsActivity : BaseActivity(), View.OnClickListener{

    override fun getContentView(): Int {
        return R.layout.activity_earnings;
    }

    override fun initView() {
//        StatusBarUtil.addStatusViewWithColor(this, R.color.colorPrimary)
        isOne.setPadding(0,QMUIStatusBarHelper.getStatusbarHeight(this),0,0)
    }

    override fun initData() {

    }
    override fun onClick(v: View?) {

    }
}
