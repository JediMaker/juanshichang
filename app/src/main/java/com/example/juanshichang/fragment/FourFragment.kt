package com.example.juanshichang.fragment


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import butterknife.OnClick
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.activity.*
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.BaseFragment
import com.example.juanshichang.bean.User
import com.example.juanshichang.utils.SpUtil
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.glide.GlideUtil
import com.example.juanshichang.widget.LiveDataBus
import com.qmuiteam.qmui.widget.QMUIRadiusImageView

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:55
 * @文件作用:  个人中心
 */
class FourFragment : BaseFragment() {
    private var myUser: User? = null
    private var cm: ClipboardManager? = null
    private var mClipData: ClipData? = null
    private var goNet: Int = 0
    override fun getLayoutId(): Int {
//        fragment_five
        return R.layout.fragment_five_two
    }

    override fun initViews(savedInstanceState: Bundle) {
        setUiData(SpUtil.getIstance().user)
    }

    override fun initData() {

        cm = mContext?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //当接收到该 广播 并且 没有登录 就表示 用户 退出了登录
        LiveDataBus.get().with("mainGo",Int::class.java).observe(this,object : Observer<Int> {
            override fun onChanged(t: Int?) {
                if (!Util.hasLogin()) {
                    goNet = 0
                }
            }
        })
    }

    @OnClick(R.id.ffUserSet, R.id.ffCopy, R.id.goTx,R.id.earnings, R.id.orderForm, R.id.myFans, R.id.myInvite,R.id.tastCz,R.id.tastGwc,R.id.tastLb)
    fun onViewClicked(v: View) {
        when (v.id) {
            R.id.ffUserSet -> {
                goNet = 2
                BaseActivity.goStartActivity(this.mContext!!, SettingActivity())
            }
            R.id.ffCopy -> { //复制
                if (myUser != null && !TextUtils.isEmpty(myUser?.invite_code)) {
                    mClipData = ClipData.newPlainText("邀请码:", myUser?.invite_code)
                    cm?.setPrimaryClip(mClipData!!)
                    ToastUtil.showToast(this.mContext!!, "已复制到粘贴板")
                } else {
                    myUser = SpUtil.getIstance().user
                    ToastUtil.showToast(this.mContext!!, "内容有误,请重新复制")
                }
            }
            R.id.goTx ->{//提现
                val intent = Intent(mContext,TakeOutActivity::class.java)
                intent.putExtra("type",1)
                BaseActivity.goStartActivity(mContext!!, intent)
            }
            R.id.earnings -> {//收益
                goNet = 2
                BaseActivity.goStartActivity(this.mContext!!, EarningsActivity())
            }
            R.id.orderForm -> {//订单
                BaseActivity.goStartActivity(this.mContext!!, OrderFormActivity())
            }
            R.id.myFans -> {//粉丝
                BaseActivity.goStartActivity(this.mContext!!, FansActivity())
            }
            R.id.myInvite -> {//邀请

            }
            R.id.tastCz ->{ //充值缴费
                BaseActivity.goStartActivity(this.mContext!!, TopupActivity())
            }
            R.id.tastGwc ->{ //自营商品详情
                BaseActivity.goStartActivity(this.mContext!!, ShangPinZyContains())
            }
            R.id.tastLb ->{ //自营商品列表

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
        GlideUtil.loadHeadImage(mContext!!, user?.avatar, fi)
        fiv?.text = user?.invite_code
        ft?.text = user?.nick_name
        top?.text = "" + Util.getFloatPrice(user?.balance!!)
        isMon?.text = "" + Util.getFloatPrice(user?.current_month_benefit!!.toLong())
        isDay?.text = "" + Util.getFloatPrice(user?.current_day_benefit!!.toLong())
        myUser = user
    }

    var timers: CountDownTimer = object : CountDownTimer(Long.MAX_VALUE, 888) {
        override fun onFinish() {
            goNet = 1
        }

        override fun onTick(p0: Long) {
            val user = SpUtil.getIstance().user
            if (!TextUtils.isEmpty(user.usertoken) && !TextUtils.isEmpty(user.avatar) && !TextUtils.isEmpty(
                    user.invite_code
                )
            ) { //通过三个参数判断所有
                setUiData(user)
                this.cancel()
            } else {
                MainActivity.downUser(mContext!!)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.hasLogin()) {
            if (goNet == 0) {
                val user = SpUtil.getIstance().user
                if (!TextUtils.isEmpty(user.usertoken) && !TextUtils.isEmpty(user.avatar) && !TextUtils.isEmpty(
                        user.invite_code
                    )
                ) { //通过三个参数判断所有
                    setUiData(user)
                    goNet = 1
                } else {
                    MainActivity.downUser(mContext!!)
                    timers.start()
                }
            }
            if(goNet == 2){
                //重新获取用户信息
                MainActivity.downUser(mContext!!)
                timers.start()
            }
        }else{
            if(goNet != 0){
                ToastUtil.showToast(mContext!!,"登录态丢失  请重新登录")
                val intent  = Intent(mContext!!, Reg2LogActivity::class.java)
                intent.putExtra("type", Reg2LogActivity.LOGINCODE) // 显示登录
                intent.putExtra("one","1")
                BaseActivity.goStartActivity(mContext!!,intent)
            }
        }
    }

    //这个方法过时了 不好使
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            val user = SpUtil.getIstance().user
            if (Util.hasLogin()) {
                val fi = mBaseView?.findViewById<QMUIRadiusImageView>(R.id.ffUserImage) //头像
                val ft = mBaseView?.findViewById<TextView>(R.id.ffUserName) //昵称
                GlideUtil.loadHeadImage(mContext!!, user?.avatar, fi)
                ft?.text = user?.nick_name
                ToastUtil.showToast(mContext!!, "更新")
            }
        }
    }

}
