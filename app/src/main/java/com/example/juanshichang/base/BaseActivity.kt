package com.example.juanshichang.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.annotation.NonNull
import butterknife.ButterKnife
import butterknife.Unbinder
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.dialog.HandyDialog
import com.example.juanshichang.dialog.LoadingProgressDialog
import com.example.juanshichang.dialog.RegisterDialog
import com.example.juanshichang.dialog.ToastDialog
import com.example.juanshichang.utils.ActivityManager
import com.example.juanshichang.utils.AutoLayoutActivity
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.Util
import com.example.juanshichang.widget.IsInternet
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.RxLifecycleAndroid
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*


abstract class BaseActivity : AutoLayoutActivity(), LifecycleProvider<ActivityEvent> {
    private val lifecycleSubject = BehaviorSubject.create<ActivityEvent>()
    var myApp : MyApp? = null
    var myLoading:QMUITipDialog? = null
    protected abstract fun getContentView(): Int
    protected abstract fun initView()
    protected abstract fun initData()
    //绑定控件id
     var unbinder : Unbinder? = null
    //没有网络添加的
    private var mNotIntent: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QMUIStatusBarHelper.translucent(this)
        QMUIStatusBarHelper.setStatusBarLightMode(this) //黑色
//    QMUIStatusBarHelper.setStatusBarDarkMode(this@MainActivity)   //白色
        //-----------------网络-------------------------
        //网络连接
        val conn = IsInternet.isNetworkAvalible(this@BaseActivity)
        if (!conn) {
            setContentView(R.layout.activity_not_car)
            //调用网络工具类中的方法，跳转到设置网络的界面
            mNotIntent = findViewById(R.id.mGoGuangguangTV) as TextView
            mNotIntent!!.setOnClickListener {
                if (!IsInternet.isNetworkAvalible(this@BaseActivity)) {
                    Toast.makeText(this@BaseActivity, "请检查网络连接!", Toast.LENGTH_SHORT).show()
                    //startActivityForResult(new Intent( Settings.ACTION_WIRELESS_SETTINGS), 0);
                } else {
                    setContentView(getContentView())
                    myApp = getApplication() as MyApp
                    unbinder = ButterKnife.bind(this@BaseActivity)
                    lifecycleSubject.onNext(ActivityEvent.CREATE)
                    ActivityManager.getInstance().addActivity(this@BaseActivity)
                    initView()
                    initData()
                }
            }
        } else {
            //原始
            setContentView(getContentView())
            myApp = getApplication() as MyApp
            unbinder = ButterKnife.bind(this)
            lifecycleSubject.onNext(ActivityEvent.CREATE)
            ActivityManager.getInstance().addActivity(this)
            initView()
            initData()
        }
        //------------------网络-------------------
    }
    /*protected fun onCreate(@Nullable savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)

        //-----------------网络-------------------------
        //网络连接
        val conn = IsInternet.isNetworkAvalible(this@BaseActivity)
        if (!conn) {
            setContentView(R.layout.activity_not_car)
            //调用网络工具类中的方法，跳转到设置网络的界面
            mNotIntent = findViewById(R.id.mGoGuangguangTV) as TextView
            mNotIntent!!.setOnClickListener {
                if (!IsInternet.isNetworkAvalible(this@BaseActivity)) {
                    Toast.makeText(this@BaseActivity, "请检查网络连接!", Toast.LENGTH_SHORT).show()
                    //startActivityForResult(new Intent( Settings.ACTION_WIRELESS_SETTINGS), 0);
                } else {
                    setContentView(getContentView())
                    myApp = getApplication() as MyApp
                    unbinder = ButterKnife.bind(this@BaseActivity)
                    lifecycleSubject.onNext(IsInternet.CREATE)
                    ActivityManager.getInstance().addActivity(this@BaseActivity)
                    initView()
                    initData()
                }
            }
        } else {
            //原始
            setContentView(getContentView())
            myApp = getApplication() as MyApp
            unbinder = ButterKnife.bind(this)
            lifecycleSubject.onNext(IsInternet.CREATE)
            ActivityManager.getInstance().addActivity(this)
            initView()
            initData()
        }
        //------------------网络-------------------
    }*/

    @NonNull
    @CheckResult
    override fun lifecycle(): Observable<ActivityEvent> { //<ActivityEvent>
        return lifecycleSubject.hide()
    }

    @NonNull
    @CheckResult
    override fun <T> bindUntilEvent(@NonNull event: ActivityEvent): LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

    @NonNull
    @CheckResult
    override fun <T> bindToLifecycle(): LifecycleTransformer<T> {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(ActivityEvent.START)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(ActivityEvent.RESUME)
//        MobclickAgent.onResume(this)  // todo Mob 相关
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        lifecycleSubject.onNext(ActivityEvent.PAUSE)
//        MobclickAgent.onPause(this)   // todo Mob 相关
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        lifecycleSubject.onNext(ActivityEvent.STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder?.unbind()
        lifecycleSubject.onNext(ActivityEvent.DESTROY)
    }
    // My New Add
    /**
     * @param icon  图片
     * @param title 标题
     * @param iscancelable 是否可点击取消  该对象为 myLoading
     */
    fun showMyLoadD(icon:Int,title:String,iscancelable:Boolean){
        if(title=="" || TextUtils.isEmpty(title)){
            myLoading = QMUITipDialog.Builder(this).setIconType(icon).create(iscancelable)
        }else{
            myLoading = QMUITipDialog.Builder(this).setIconType(icon).setTipWord(title).create(iscancelable)
        }
        myLoading?.show()
    }
    companion object{
        /**失败：对应叉号 */
        val TOAST_FAILURE = 0
        /**成功：对应对号 */
        val TOAST_SUCCESS = 1
        /**警告：对应叹号 */
        val TOAST_WARN = 2

        /**
         * @Context   传入执行跳转页面上下文
         * @Bundle    传入要传递的参数
         * @Class<BaseActivity>   传入跳向A界面 A界面
         */
        fun goStartActivity(context: Context, bundle: Bundle, activity:BaseActivity) {
            var intent = Intent()
            intent.setClass(context, activity::class.java)
            if (!bundle.isEmpty) {
                intent.putExtra("BUNDLE", bundle)
            }
            context.startActivity(intent)
        }
        fun goStartActivity(context: Context,activity:BaseActivity) {
            var intent = Intent()
            intent.setClass(context, activity::class.java)
            context.startActivity(intent)
        }
        fun goStartActivity(context: Context,intent:Intent) {
            context.startActivity(intent)
        }
    }
    /**
     * 代替原来Toast
     * @param type：TOAST_FAILURE=失败，对应叉号；
     * TOAST_SUCCESS=成功：对应对号；
     * TOAST_WARN=警告：对应叹号；
     * @param text：提示内容
     * @param mCallback：回调函数 ，不需要的话传null
     */
    fun showCustomToast(type: Int, text: String, mCallback: ToastCallback?) {
        try {
            val fragmentYes = ToastDialog(type, text)
            fragmentYes.show(this.getSupportFragmentManager(), null)
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    mCallback?.next()
                    try {
                        fragmentYes.dismiss()
                    } catch (e: Exception) {
                    }

                }
            }, 1000)
        } catch (e: Exception) {
        }

    }

    /**
     * Toast回调
     * @author yl
     */
    interface ToastCallback {
        operator fun next()
    }

    /**
     * 对话框提示
     * @param type：TOAST_FAILURE=失败，对应叉号；
     * TOAST_SUCCESS=成功：对应对号；
     * TOAST_WARN=警告：对应叹号；
     * @param text：提示内容
     * @param sureBtnText：确定按钮文字，穿""或者null 则默认为确定
     * @param cancleBtnText：取消按钮文字，穿""或者null 则默认为取消
     * @param mCallback：对话框回调
     */
    fun showDialog(type: Int, text: String, sureBtnText: String, cancleBtnText: String, mCallback: DialogCallback) {
        val fragmentYes = HandyDialog(type, text, sureBtnText, cancleBtnText, mCallback)
        fragmentYes.show(this.getSupportFragmentManager(), null)
    }

    /**
     * 这是一个高仿的....
     */
    fun showRegisterDialog(title:String,content:String,sureBtnText:String,cancleBtnText:String,callback: BaseActivity.DialogCallback,isCanceled:Boolean) {
        val fragments = RegisterDialog(title,content,sureBtnText,cancleBtnText,callback,isCanceled)
        fragments.show(this.getSupportFragmentManager(), null)
    }
    /**
     * 对话框回调
     * @author yl
     */
    interface DialogCallback {
        fun sure()
        fun cancle()
    }

    //加载
    protected var progressdialog: LoadingProgressDialog? = null

    fun showProgressDialog() {
        try {
            if (progressdialog != null && progressdialog!!.isShowing()) {

            } else if (progressdialog != null && !progressdialog!!.isShowing()) {
                if (Util.ifCurrentActivityTopStack(this)) {
                    progressdialog!!.setCancelable(true)
                    progressdialog!!.show()
                }
            } else {
                if (Util.ifCurrentActivityTopStack(this)) {
                    progressdialog = LoadingProgressDialog(this, R.style.LoadingProgressDialog)
                    // dialog.setContentView(R.layout.custom_progress_dialog);
                    progressdialog!!.getWindow()?.getAttributes()?.gravity = Gravity.CENTER
                    // dialog.setCancelable(false);
                    progressdialog!!.setCanceledOnTouchOutside(false)
                    progressdialog!!.show()
                }
            }
        } catch (e: Exception) {
            // TODO: handle exception
        }

    }

    fun dismissProgressDialog() {
        try {
            if (progressdialog != null && progressdialog!!.isShowing())
                progressdialog!!.dismiss()
            if (progressdialog != null)
                progressdialog = null
        } catch (e: Exception) {
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!IsInternet.isNetworkAvalible(this@BaseActivity)) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}