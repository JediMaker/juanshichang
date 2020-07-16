package com.example.juanshichang.fragment


import android.content.*
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
import com.example.juanshichang.base.*
import com.example.juanshichang.bean.GridItemBean
import com.example.juanshichang.bean.User
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.*
import com.example.juanshichang.utils.glide.GlideUtil
import com.example.juanshichang.widget.LiveDataBus
import com.google.gson.Gson
import com.qmuiteam.qmui.widget.QMUIRadiusImageView
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import kotlinx.android.synthetic.main.activity_setting.*
import org.json.JSONObject
import rx.Subscriber

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
    private var userName: String? = null
    private var uNDialog: QMUIDialog.EditTextDialogBuilder? = null
    override fun getLayoutId(): Int {
//        fragment_five
        return R.layout.fragment_five_two
    }

    override fun initViews(savedInstanceState: Bundle) {
//        setUiData(SpUtil.getIstance().user)
//        setUND()//创建 用户昵称对话框
    }

    override fun initData() {
        cm = mContext?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //当接收到该 广播 并且 没有登录 就表示 用户 退出了登录
        LiveDataBus.get().with("mainGo", Int::class.java).observe(this, object : Observer<Int> {
            override fun onChanged(t: Int?) {
                if (!Util.hasLogin()) {
                    goNet = 0
                }
            }
        })
    }

    @OnClick(
        R.id.ffUserSet,
        R.id.ffCopy,
        R.id.goTx,
        R.id.orderAll,
        R.id.orderWaitPay,
        R.id.orderWaitSend,
        R.id.orderWaitReceive,
        R.id.setUserName,
        R.id.setFaq,
        R.id.mSite,
        R.id.settings,
        R.id.tastCz,
        R.id.tastGwc,
        R.id.tastLb
    )
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
            R.id.goTx -> {//提现
                val intent = Intent(mContext, TakeOutActivity::class.java)
                intent.putExtra("type", 1)
                BaseActivity.goStartActivity(mContext!!, intent)
            }
            R.id.orderAll -> {//收益-->全部订单
//                ToastUtil.showToast(this.mContext!!,"暂未开放入口")
//                BaseActivity.goStartActivity(this.mContext!!, EarningsActivity())
                val intent = Intent(mContext, OrderFormActivity::class.java)
                intent.putExtra("type", 0)
                BaseActivity.goStartActivity(this.mContext!!, intent)

            }
            R.id.orderWaitPay -> {//待付款订单
                val intent = Intent(mContext, OrderFormActivity::class.java)
                intent.putExtra("type", 1)
                BaseActivity.goStartActivity(this.mContext!!, intent)            }
            R.id.orderWaitSend -> {//待发货订单
//                ToastUtil.showToast(this.mContext!!,"暂未开放入口")
//                BaseActivity.goStartActivity(this.mContext!!, FansActivity())
                val intent = Intent(mContext, OrderFormActivity::class.java)
                intent.putExtra("type", 2)
                BaseActivity.goStartActivity(this.mContext!!, intent)
            }
            R.id.orderWaitReceive -> {//待收货订单
//                ToastUtil.showToast(this.mContext!!,"暂未开放入口")
                val intent = Intent(mContext, OrderFormActivity::class.java)
                intent.putExtra("type", 3)
                BaseActivity.goStartActivity(this.mContext!!, intent)
            }
            R.id.tastCz -> { //充值缴费
                BaseActivity.goStartActivity(this.mContext!!, TopupActivity())
            }
            R.id.tastGwc -> { //自营商品详情
                BaseActivity.goStartActivity(this.mContext!!, ShangPinZyContains())
            }

            R.id.setUserName -> { //用户昵称修改
                /*if (userName != null && !TextUtils.isEmpty(userName)) {
                    uNDialog?.setDefaultText(userName)
                }
                uNDialog?.show()*/
            }
            R.id.setFaq -> { //跳转到使用帮助
                BaseActivity.goStartActivity(this.mContext!!, FaqActivity())
            }
            R.id.mSite -> { //收货地址-->跳转到收货地址列表
                BaseActivity.goStartActivity(this.mContext!!, SiteListActivity())
            }
            R.id.settings -> { //跳转设置
                goNet = 2
                BaseActivity.goStartActivity(this.mContext!!, SettingActivity())
            }
            R.id.tastLb -> { //自营商品列表
                getGrid()
            }
        }
    }

    private fun setUi(user: User?) {
        setUserReg.text = user!!.date_added //日期
        nickName.text = user!!.nick_name //昵称
        userName = user!!.nick_name  //昵称
    }

    private fun setUND() {
        //修改昵称 Dialog
        uNDialog = QMUIDialog.EditTextDialogBuilder(mContext)
        uNDialog?.setTitle("昵称")
        uNDialog?.setPlaceholder("在此输入您的昵称") //Hint
        uNDialog?.addAction("取消", QMUIDialogAction.ActionListener { dialog, index ->
            ToastTool.showToast(context!!, "取消修改")
            dialog.dismiss()
        })
        uNDialog?.addAction("确定", QMUIDialogAction.ActionListener { dialog, index ->
            val newName = uNDialog?.editText?.text.toString()
            if (!newName.equals(userName.toString())) {
                if (!TextUtils.isEmpty(newName) && newName != "") {
                    if (newName.length > 10) {
                        ToastUtil.showToast(context!!, "昵称过长 请重新设置")
                        uNDialog?.editText?.setText("")
                    } else {
                        mContext?.showProgressDialog()  //
                        setNewName(newName.toString().trim())
                        dialog.dismiss()
                    }
                } else {
                    ToastUtil.showToast(context!!, "昵称不能为空")
                }
            } else {
                ToastUtil.showToast(context!!, "昵称未修改")
                dialog.dismiss()
            }
        })
        uNDialog?.create()

    }

    //修改用户昵称
    private fun setNewName(nickname: String) {
        HttpManager.getInstance()
            .post(Api.SETINFO, NewParameter.getUpdInfo(nickname), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str = result?.substring(result?.indexOf("{"), result.length)

                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        jsonObj = JSONObject(str)
                        if (!jsonObj.optBoolean(JsonParser.JSON_Status)
                        ) {
                            ToastUtil.showToast(
                                context!!,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val user = SpUtil.getIstance().user
                            user.nick_name = nickname
                            nickName.text = nickname //昵称
                            SpUtil.getIstance().user = user
                            mContext?.runOnUiThread(object : Runnable {
                                override fun run() {
                                    ToastTool.showToast(context!!, "昵称修改成功")
                                    userName = nickname
                                    mContext?.dismissProgressDialog()
                                }
                            })
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "昵称修改加载完成!")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "昵称修改加载失败!" + e)
                    mContext?.runOnUiThread(object : Runnable {
                        override fun run() {
                            ToastUtil.showToast(context!!, "昵称修改失败,请稍后重试")
                            mContext?.dismissProgressDialog()
                        }
                    })
                }

            })
    }

    private fun getGrid() {
        LogTool.e("okgo", "启动Grid请求")
        HttpManager.getInstance()
            .post(Api.CHANNELLIST, Parameter.getMainBannerMap(), object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        val jsonObj: JSONObject? = JSONObject(str)
                        if (!jsonObj?.optString(JsonParser.JSON_CODE)
                                .equals(JsonParser.JSON_SUCCESS)
                        ) {
                            ToastUtil.showToast(context!!, jsonObj!!.optString(JsonParser.JSON_MSG))
                        } else {
                            val data = Gson().fromJson(str, GridItemBean.GridItemBeans::class.java)
                            val gridList = data.data.channel_list
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "Grid加载完成!")
                }

                override fun onError(e: Throwable?) {
//                getGrid()
                    LogTool.e("onError", "Grid加载失败!" + e)
                }

            })
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
        ft?.text = user?.phone_num
        top?.text = "" + Util.getFloatPrice(user?.balance!!)
        isMon?.text = "" + Util.getFloatPrice(user?.current_month_benefit!!.toLong())
        isDay?.text = "" + Util.getFloatPrice(user?.current_day_benefit!!.toLong())
        setUi(user)
        myUser = user
    }

    var timers: CountDownTimer = object : CountDownTimer(Long.MAX_VALUE, 888) {
        override fun onFinish() {
            goNet = 1
        }

        override fun onTick(p0: Long) {
            val user = SpUtil.getIstance().user
            if (!TextUtils.isEmpty(user.avatar) && !TextUtils.isEmpty(
                    user.invite_code
                )
            ) { //通过三个参数判断所有
                setUiData(user)
                this.cancel()
            } else {
                MainActivity.downUser(mContext!!)
            }
            /*    if (!TextUtils.isEmpty(user.usertoken) && !TextUtils.isEmpty(user.avatar) && !TextUtils.isEmpty(
                        user.invite_code
                    )
                ) { //通过三个参数判断所有
                    setUiData(user)
                } else {
                    MainActivity.downUser(mContext!!)
                }*/

        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.hasLogin()) {
            if (goNet == 0) {
                val user = SpUtil.getIstance().user
                if (!TextUtils.isEmpty(user.avatar) && !TextUtils.isEmpty(
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
            if (goNet == 2) {
                //重新获取用户信息
                MainActivity.downUser(mContext!!)
                timers.start()
            }
        } else {
            if (goNet != 0) {
                ToastUtil.showToast(mContext!!, "登录态丢失  请重新登录")
                val intent = Intent(mContext!!, Reg2LogActivity::class.java)
                intent.putExtra("type", Reg2LogActivity.LOGINCODE) // 显示登录
                intent.putExtra("one", "1")
                BaseActivity.goStartActivity(mContext!!, intent)
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
