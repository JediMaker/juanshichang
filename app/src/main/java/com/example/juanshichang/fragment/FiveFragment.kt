package com.example.juanshichang.fragment


import android.os.Bundle
import android.view.View
import butterknife.OnClick

import com.example.juanshichang.R
import com.example.juanshichang.activity.EarningsActivity
import com.example.juanshichang.activity.OrderFormActivity
import com.example.juanshichang.activity.SettingActivity
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.BaseFragment

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
//        QMUIStatusBarHelper.translucent(mContext)
//        StatusBarUtil.addStatusViewWithColor(mContext, R.color.white)
//        StatusBarUtil.fullScreen(mContext)
    }

    override fun initData() {
    }

    @OnClick(R.id.ffUserSet,R.id.ffCopy,R.id.earnings,R.id.orderForm,R.id.myFans,R.id.myInvite)
    fun onViewClicked(v: View) {
        when (v.id) {
            R.id.ffUserSet -> {
                BaseActivity.goStartActivity(this.mContext!!, SettingActivity())
            }
            R.id.ffCopy ->{ //复制

            }
            R.id.earnings ->{//收益
                BaseActivity.goStartActivity(this.mContext!!, EarningsActivity())
            }
            R.id.orderForm ->{//订单
                BaseActivity.goStartActivity(this.mContext!!, OrderFormActivity())
            }
            R.id.myFans ->{//粉丝

            }
            R.id.myInvite ->{//邀请

            }
        }
    }
}
