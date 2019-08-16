package com.example.juanshichang.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.utils.ToastUtil
import com.google.android.material.tabs.TabLayout
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.activity_order_form.*
import kotlinx.android.synthetic.main.activity_order_form.view.*

class OrderFormActivity : BaseActivity(), View.OnClickListener {
    override fun getContentView(): Int {
        return R.layout.activity_order_form
    }

    override fun initView() {
        orderTop.setPadding(0, QMUIStatusBarHelper.getStatusbarHeight(this),0,0)
        setTab()
    }

    override fun initData() {

    }

    override fun onClick(v: View?) {

    }
    private fun setTab() {
        detailTab.addTab(detailTab.newTab().setText("全部"))
        detailTab.addTab(detailTab.newTab().setText("已付款"))
        detailTab.addTab(detailTab.newTab().setText("已结算"))
        detailTab.addTab(detailTab.newTab().setText("已失效"))
        detailTab.addOnTabSelectedListener(mTabLayoutBottom)
    }
    private val mTabLayoutBottom = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {

        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
        }

        override fun onTabSelected(t: TabLayout.Tab?) {
            ToastUtil.showToast(this@OrderFormActivity,"点击到了"+t?.position)
        }
    }
}
