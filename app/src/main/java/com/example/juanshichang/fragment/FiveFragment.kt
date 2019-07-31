package com.example.juanshichang.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import butterknife.internal.Utils
import com.bumptech.glide.Glide
import com.example.juanshichang.MainActivity
import com.example.juanshichang.MyApp

import com.example.juanshichang.R
import com.example.juanshichang.activity.SettingActivity
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.BaseFragment
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import kotlinx.android.synthetic.main.fragment_five.*

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:55
 * @文件作用:  个人中心
 */
class FiveFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_five
    }

    override fun initViews(savedInstanceState: Bundle) {
//        if (Util.hasLogin()) {
//            unlogin.visibility = View.VISIBLE
//        }
    }

    override fun initData() {
    }

    @OnClick(R.id.setting)
    fun onViewClicked(v: View) {
        when (v.id) {
            R.id.setting -> {
                BaseActivity.goStartActivity(this.mContext!!, SettingActivity())
            }
        }
    }
}
