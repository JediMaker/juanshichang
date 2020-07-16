package com.example.juanshichang.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.*
import com.example.juanshichang.widget.LiveDataBus
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import kotlinx.android.synthetic.main.activity_reg2_log.*
import kotlinx.android.synthetic.main.login_item.*
import kotlinx.android.synthetic.main.regist_item.*
import org.jetbrains.anko.backgroundResource
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/8/10 17:32
 * @文件作用: 新的登录注册页面  通过传入参数 决定 显示 哪个 也可以填 默认显示注册页面
 */
class Reg2LogActivity : BaseActivity(), View.OnClickListener {
    var tag = 1
    var mSmsCode: String? = ""  //返回的 验证码
    override fun getContentView(): Int {
        return R.layout.activity_reg2_log;
    }

    companion object {
        val REGISTERCODE: Int = 0
        val LOGINCODE: Int = 1
    }

    override fun initView() {
//        QMUIStatusBarHelper.translucent(this@Reg2LogActivity)
        StatusBarUtil.addStatusViewWithColor(this@Reg2LogActivity, R.color.colorPrimary)
//        rRL.setPadding(0, QMUIDisplayHelper.getStatusBarHeight(this@Reg2LogActivity), 0, 0)
        SoftHideKeyBoardUtil.assistActivity(this@Reg2LogActivity)
        setOnClick() //注册点击事件
        //传入非0 就显示登录界面
        val type = intent.getIntExtra("type", 0)
        if (type != REGISTERCODE) {
            registerInclude.visibility = View.GONE
            loginInclude.visibility = View.VISIBLE
            log_title.text = "登录"
        } else {
            log_title.text = "注册"
        }
        if (null != intent.getStringExtra("one")) { //从个人中心 页面跳转的处理
            LiveDataBus.get().with("mainGo").value = 0
        }
    }

    override fun initData() {
        //设置用户协议字体  设置点击事件
        val spStr = SpannableString(userAgreements.text)
        spStr.setSpan(
            ForegroundColorSpan(Color.RED),
            4,
            spStr.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        userAgreements.text = spStr
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.log_ret -> {
                /*       if (!Util.hasLogin(this)) {
                           val intent = Intent(this, MainActivity::class.java)
                           intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                           startActivity(intent)
                           LiveDataBus.get().with("mainGo").value = 0 //返回到购物车
                           finish()
                       }*/
                this@Reg2LogActivity.finish()
            }
            //注册页面
            R.id.goLog -> {//登录
                log_title.text = "登录"
                registerInclude.visibility = View.GONE
                loginInclude.visibility = View.VISIBLE
            }
            R.id.goVerifyCode -> {//获取验证码
                val phone = regPhone.text.toString()
                mSmsCode = null
                if (Util.validateMobile(phone)) {
                    //启动注册轮询
                    goVerifyCode.isEnabled = false
                    getSendSMS(phone, this@Reg2LogActivity)
                } else {
                    showRegisterDialog("温馨提示", "请输入正确的手机号", "确定", "", object : DialogCallback {
                        override fun cancle() { //无取消
                        }

                        override fun sure() {
                            regPhone.text.clear()
                        }
                    }, false)
                }
            }
            R.id.registBut -> {//注册按钮
                if (goRegister()) {
                    val phone = regPhone.text.toString().trim()
                    val ps = regPassword.text.toString().trim()
                    val sms = yzCode.text.toString().trim()
                    val invite = regInviteCode.text.toString().trim()
//                    ToastUtil.showToast(this@Reg2LogActivity, "登录...")
                    regGo(phone, ps, invite, sms)
                }
            }
            R.id.mRCheckBoxX -> {//用户协议选中
                if (tag == 1) {
                    mRCheckBoxX.backgroundResource = R.drawable.noselect
                    tag = 0
                } else {
                    tag = 1
                    mRCheckBoxX.backgroundResource = R.drawable.is_sel
                }
            }
            R.id.userAgreements -> {//用户协议字体
                ToastUtil.showToast(this@Reg2LogActivity, "《注册协议》准备中...")
            }
            //登录界面
            R.id.goReg -> {//注册
                log_title.text = "注册"
                loginInclude.visibility = View.GONE
                registerInclude.visibility = View.VISIBLE
            }
            R.id.loginBut -> {//登录按钮
                val phone = logPhone.text.toString()
                val ps = logPW.text.toString()
                if (goLogin(phone, ps)) {
                    logGo(phone, ps)
                }
            }
            R.id.fastLogin -> {//快速登录
                val intent = Intent(this@Reg2LogActivity, FastLoginActivity::class.java)
                intent.putExtra("type", FastLoginActivity.LOGINCODE) // 显示登录
                goStartActivity(this@Reg2LogActivity, intent)
            }
            R.id.lookPW -> {//找回密码
                val intent = Intent(this@Reg2LogActivity, FastLoginActivity::class.java)
                intent.putExtra("type", FastLoginActivity.RESETPASSWORDCODE) // 显示登录
                goStartActivity(this@Reg2LogActivity, intent)
            }
            else -> {
            }
        }
    }

    private fun goLogin(phone: String, ps: String): Boolean {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(ps)) {
            ToastUtil.showToast(this@Reg2LogActivity, "请根据提示框输入正确的信息!")
            return false
        }
        if (!Util.validateMobile(phone)) {
            ToastUtil.showToast(this@Reg2LogActivity, "请输入正确的手机号!")
            return false
        }
        if (ps.length < 6) {
            ToastUtil.showToast(this@Reg2LogActivity, "请设置至少6位登录密码!")
            return false
        }
        return true
    }

    private fun setOnClick() {
        log_ret.setOnClickListener(this)//返回
        //注册页面
        goLog.setOnClickListener(this) //登录
        goVerifyCode.setOnClickListener(this) //获取验证码
        registBut.setOnClickListener(this) //注册按钮
        mRCheckBoxX.setOnClickListener(this) //用户协议选中
        userAgreements.setOnClickListener(this) //用户协议字体
        //注册Linear
        rl1.setOnClickListener(this)
        rl2.setOnClickListener(this)
        rl3.setOnClickListener(this)
        rl4.setOnClickListener(this)
        //登录页面
        goReg.setOnClickListener(this) //注册
        loginBut.setOnClickListener(this) //登录按钮
        fastLogin.setOnClickListener(this) //快速登录
        lookPW.setOnClickListener(this) //找回密码
    }

    private fun goRegister(): Boolean {
        val phone = regPhone.text.toString()
        val ps = regPassword.text.toString()
        val yzCode = yzCode.text.toString()
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(ps) || TextUtils.isEmpty(yzCode)) {
            ToastUtil.showToast(this@Reg2LogActivity, "请根据提示框输入正确的信息!")
            return false
        }
        if (!Util.validateMobile(phone)) {
            ToastUtil.showToast(this@Reg2LogActivity, "请输入正确的手机号!")
            return false
        }
        if (mSmsCode != null && !TextUtils.isEmpty(mSmsCode)) {//todo 验证码 判断 ....
            if (yzCode.length != mSmsCode!!.length || !mSmsCode!!.equals(yzCode)) {
                ToastUtil.showToast(this@Reg2LogActivity, "验证码有误!!!")
                return true
            }
        } else {
//            ToastUtil.showToast(this@Reg2LogActivity, "获取验证码 错误请稍后重试!!!")
            return true
        }
        if (ps.length < 6) {
            ToastUtil.showToast(this@Reg2LogActivity, "请设置至少6位登录密码!")
            return false
        }
//        if (ps != ps2) {
//            ToastUtil.showToast(this@RegisterActivity, "密码不一致!")
//            return false
//        }
        if (tag == 0) {
            ToastUtil.showToast(this@Reg2LogActivity, "同意注册协议才能注册!")
            return false
        }
        return true
    }

    /**
     * 注册
     * @return 返回验证码
     */
    private fun regGo(phone: String, ps: String, invite_code: String, sms_code: String) {
        HttpManager.getInstance().post(
            Api.USER,
            Parameter.getRegisterMap(phone, ps, invite_code, sms_code),
            object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str = result?.substring(result?.indexOf("{"), result.length)

                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(str)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        /* if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                             ToastUtil.showToast(this@Reg2LogActivity, jsonObj!!.optString(JsonParser.JSON_MSG))
                         }*/
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            ToastUtil.showToast(
                                this@Reg2LogActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
//                        val data = jsonObj!!.getJSONObject("data")
//                        val token: String = data.getString("token")  //注册返回Token不做处理
//                        if (token != "") {
                            logGo(phone, ps)  //注册完成 直接登录
                            ToastUtil.showToast(this@Reg2LogActivity, "注册成功,正在登录...")
//                        }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "注册请求完成!")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "注册请求错误!" + e)
                    ToastUtil.showToast(this@Reg2LogActivity, "注册失败,请稍后重试!")
                }
            })
    }

    /**
     * 登录
     */
    private fun logGo(phone: String, ps: String) {
        HttpManager.getInstance()
            .post(Api.LOGIN, Parameter.getLoginMap(phone, ps), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str = result?.substring(result?.indexOf("{"), result.length)

                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(str)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        /*  if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!) {
                              ToastUtil.showToast(this@Reg2LogActivity, jsonObj.optString(JsonParser.JSON_MSG))
                          } */
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            ToastUtil.showToast(
                                this@Reg2LogActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val data = jsonObj.getJSONObject("data")
//                        val token: String = data.getString("token")  //注册返回Token不做处理
                            val uid: Long = data.getLong("uid") //这是用于校验新接口的uid
//                        LogTool.e("LogToken", token)
                            val user = SpUtil.getIstance().user
                            user.apply {
                                useruid = uid
//                            usertoken = token
                                phone_num = phone
//                            password = ps
                            }.let {
                                SpUtil.getIstance().user = it //写入
                            }
                            this@Reg2LogActivity.runOnUiThread(Runnable {
//                            if (token != "" && !TextUtils.isEmpty(token)) {
//                            downUser("login")
                                goStartActivity(this@Reg2LogActivity, MainActivity())
                                this@Reg2LogActivity.finish()
//                            }
                            })

                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "登录请求完成!")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "登录请求错误!" + e)
                }

            })
    }

    fun getSendSMS(mobile: String, context: Context) {
//        var smsCodes: String? = null
        HttpManager.getInstance()
            .post(Api.SMSSEND, Parameter.getVerifyCode(mobile, "1"), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str = result?.substring(result?.indexOf("{"), result.length)

                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(str)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optBoolean(JsonParser.JSON_Status)!!
                        ) {
                            ToastUtil.showToast(context, jsonObj!!.optString(JsonParser.JSON_MSG))
                        } else {
                            val data = jsonObj!!.getJSONObject("data")
                            LogTool.e("zxcv", "Date: " + data.toString())
                            mSmsCode = data.getString("code")  //注册返回Token不做处理
//                        mSmsCode = data.getString("sms_code")  //注册返回Token不做处理
                            LogTool.e("zxcv", "sms_code: " + mSmsCode.toString())
                            if (mSmsCode != "") {
                                timer.start()
                                ToastUtil.showToast(context, "验证码已发送,请注意查收消息")
                                //todo 此处可添加 请求读取通知类消息权限
                            }
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "验证码请求完成!")
                }

                override fun onError(e: Throwable?) {
                    mSmsCode = ""
                    LogTool.e("onError", "验证码请求错误!" + e)
                    ToastUtil.showToast(context, "验证码发送失败,请稍后重试!!!")
                }
            })
    }

    //验证码计时器
//    var curTime = 60
    var timer: CountDownTimer = object : CountDownTimer(60000, 1000) {
        override fun onFinish() {
            goVerifyCode.isEnabled = true
            goVerifyCode.text = "获取验证码"
        }

        override fun onTick(millisUntilFinished: Long) {
            //todo millisUntilFinished为剩余时间，也就是30000 - n*1000
            goVerifyCode.isEnabled = false //设置按钮不可点击
            val s = millisUntilFinished / 1000
            goVerifyCode.text = "$s s"
        }
    }

    //取消倒计时（译者：取消后，再次启动会重新开始倒计时）
//    timer.cancel()
    //这个计时器用于轮询注册是否输入完成
    var timerReg: CountDownTimer = object : CountDownTimer(Long.MAX_VALUE, 500) {
        override fun onFinish() {

        }

        override fun onTick(millisUntilFinished: Long) {
            if (goJudgeReg()) {
                registBut.isEnabled = true
            } else {
                registBut.isEnabled = false
            }
        }
    }

    //这个计时器用于轮询登录是否输入完成
    var timerLogin: CountDownTimer = object : CountDownTimer(Long.MAX_VALUE, 500) {
        override fun onFinish() {

        }

        override fun onTick(millisUntilFinished: Long) {
            if (goJudgeLogin()) {
                loginBut.isEnabled = true
            } else {
                loginBut.isEnabled = false
            }
        }
    }

    //判断 信息输入是否正确 无误
    private fun goJudgeReg(): Boolean {
        val phone = regPhone.text.toString()
        val ps = regPassword.text.toString()
        val yzCode = yzCode.text.toString()
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(ps) || TextUtils.isEmpty(yzCode)) {
            return false
        }
        if (!Util.validateMobile(phone)) {
            return false
        }
        if (mSmsCode == null && TextUtils.isEmpty(mSmsCode)) {//todo 验证码 判断 ....
            return false
        }
        if (!mSmsCode!!.equals(yzCode) || yzCode.length != mSmsCode!!.length) {
            LogTool.e("zxcv", "mSmsCode:$mSmsCode")
            LogTool.e("zxcv", "yzCode:$yzCode")
            return false
        }
        if (ps.length < 6) {
            return false
        }
        if (tag == 0) {
            return false
        }
        return true
    }

    /**
     * @param root
     *            最外层布局，需要调整的布局
     * @param scrollToView
     *            被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
     *            https://blog.csdn.net/HarryWeasley/java/article/details/50266749

     */
    private fun controlKeyboardLayout(root: View, scrollToView: View) {
        // 注册一个回调函数，当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时调用这个回调函数。
        root.getViewTreeObserver().addOnGlobalLayoutListener(
            ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                fun onGlobalLayout() {
                    val rect = Rect();
                    // 获取root在窗体的可视区域
                    root.getWindowVisibleDisplayFrame(rect);
                    // 当前视图最外层的高度减去现在所看到的视图的最底部的y坐标
                    val rootInvisibleHeight = root.getRootView()
                        .getHeight() - rect.bottom;
                    Log.i("tag", "最外层的高度" + root.getRootView().getHeight());
                    // 若rootInvisibleHeight高度大于100，则说明当前视图上移了，说明软键盘弹出了
                    if (rootInvisibleHeight > 100) {
                        val location = IntArray(2)
                        // 获取scrollToView在窗体的坐标
                        scrollToView.getLocationInWindow(location);
                        // 计算root滚动高度，使scrollToView在可见区域的底部
                        val srollHeight = (
                                location[1] + scrollToView
                                    .getHeight()
                                ) - rect.bottom;
                        root.scrollTo(0, srollHeight);
                    } else {
                        // 软键盘没有弹出来的时候
                        root.scrollTo(0, 0);
                    }
                }
            });
    }

    private fun goJudgeLogin(): Boolean {
        val phone = logPhone.text.toString()
        val ps = logPW.text.toString()
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(ps)) {
            return false
        }
        if (!Util.validateMobile(phone)) {
            return false
        }
        if (ps.length < 6) {
            return false
        }
        return true
    }
}
