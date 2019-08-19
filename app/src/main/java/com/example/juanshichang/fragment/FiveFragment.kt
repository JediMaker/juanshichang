package com.example.juanshichang.fragment


import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import butterknife.OnClick
import com.example.juanshichang.MainActivity

import com.example.juanshichang.R
import com.example.juanshichang.activity.EarningsActivity
import com.example.juanshichang.activity.OrderFormActivity
import com.example.juanshichang.activity.SettingActivity
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.BaseFragment
import com.example.juanshichang.bean.User
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.utils.glide.GlideUtil
import com.qmuiteam.qmui.widget.QMUIRadiusImageView
import kotlinx.android.synthetic.main.fragment_five.*

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:55
 * @文件作用:  个人中心
 */
class FiveFragment : BaseFragment() {
    private var myUser:User? = null
    override fun getLayoutId(): Int {
        return R.layout.fragment_five
    }

    override fun initViews(savedInstanceState: Bundle) {
        val user = SpUtil.getIstance().user
        if(!TextUtils.isEmpty(user.usertoken) && !TextUtils.isEmpty(user.avatar) && !TextUtils.isEmpty(user.invite_code)){ //通过三个参数判断所有
            setUiData(user)
        }else{
            MainActivity.downUser("login",mContext!!)
            timers.start()
        }
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
    private fun setUiData(user: User?) {
        val fi = mBaseView?.findViewById<QMUIRadiusImageView>(R.id.ffUserImage) //头像
        val fiv = mBaseView?.findViewById<TextView>(R.id.ffUserInvite) //邀请码
        val ft = mBaseView?.findViewById<TextView>(R.id.ffUserName) //昵称
        val top = mBaseView?.findViewById<TextView>(R.id.ffBalance) //余额
        val isMon = mBaseView?.findViewById<TextView>(R.id.ffEstimateMOn) //本月收入
        val isDay = mBaseView?.findViewById<TextView>(R.id.ffEstimateDay) //今日收入
        GlideUtil.loadImage(mContext!!,user?.avatar,fi)
        fiv?.text = user?.invite_code
        ft?.text = user?.nick_name
        top?.text = ""+user?.balance
        isMon?.text = ""+user?.current_month_benefit
        isDay?.text = ""+user?.current_day_benefit
        myUser = user
    }
    var timers: CountDownTimer = object : CountDownTimer(Long.MAX_VALUE,888){
        override fun onFinish() {

        }

        override fun onTick(p0: Long) {
            val user = SpUtil.getIstance().user
            if(!TextUtils.isEmpty(user.usertoken) && !TextUtils.isEmpty(user.avatar) && !TextUtils.isEmpty(user.invite_code)){ //通过三个参数判断所有
                setUiData(user)
                this.cancel()
            }else{
                MainActivity.downUser("login",mContext!!)
            }
        }
    }
}
