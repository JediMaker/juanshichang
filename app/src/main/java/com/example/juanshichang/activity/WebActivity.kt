package com.example.juanshichang.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.ZoomButtonsController
import androidx.annotation.RequiresApi
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.utils.LogTool
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.widget.IsInternet
import kotlinx.android.synthetic.main.activity_not_car.*
import kotlinx.android.synthetic.main.activity_web.*
import java.lang.reflect.Method

class WebActivity : BaseActivity(), View.OnClickListener {
    var mobile_short_url: String? = null
    var mobile_url: String? = null
    var zoom_controll: ZoomButtonsController? = null
    override fun initData() {
        StatusBarUtil.addStatusViewWithColor(this, R.color.colorPrimary)
        mGoGuangguangTV.setOnClickListener(this)
        mReturnView.setOnClickListener(this)
        mCloseView.setOnClickListener(this)
        val conn = IsInternet.isNetworkAvalible(this@WebActivity)
        if (!conn) {
            mNotWebLayout.visibility = View.VISIBLE
            //调用网络工具类中的方法，跳转到设置网络的界面
            mRWebLayout.visibility = View.GONE
        } else {
            mNotWebLayout.visibility = View.GONE
            mRWebLayout.visibility = View.VISIBLE
        }
    }

    override fun initView() {
        if (null != intent.getStringExtra("mobile_short_url")) {
            mobile_short_url = intent.getStringExtra("mobile_short_url")
            if (null != intent.getStringExtra("title")){
                refreshTV.visibility=View.GONE
                mTitleTV.text=intent.getStringExtra("title")
            }else{
                refreshTV.visibility=View.VISIBLE
            }
            setWebView(mobile_short_url)
        } else {
            if (null != intent.getStringExtra("mobile_url")) {
                mobile_url = intent.getStringExtra("mobile_url")
                setWebView(mobile_url)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            mGoGuangguangTV -> { //无网络页面刷新
                if (!IsInternet.isNetworkAvalible(this@WebActivity)) {
                    startActivityForResult(Intent(Settings.ACTION_WIRELESS_SETTINGS), 0)
                } else {
                    initView()
                }
            }
            mReturnView -> { //返回
                if (mWebView != null) {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack()                                                // 有网页时返回上一级
                    } else {
//                        startActivity(Intent(this@WebActivity,MainActivity::class.java))
                        this@WebActivity.finish()
                    }
                }
            }
            mCloseView -> {
                if (!IsInternet.isNetworkAvalible(this@WebActivity)) {
                    startActivityForResult(Intent(Settings.ACTION_WIRELESS_SETTINGS), 0)
                } else {
                    initView()
                }
            }
        }
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        initData()
    }*/
    override fun getContentView(): Int {
        return R.layout.activity_web
    }
    private fun setWebView(urls: String?) {
        mWebView.loadUrl(urls)//加载web资源
        //控制webView 字体大小
        // webView.getSettings().setTextSize(WebSettings.TextSize.LARGER);// SMALLEST(50%), SMALLER(75%),  NORMAL(100%), LARGER(150%), LARGEST(200%);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) //解决键盘遮住
        mWebView.webChromeClient = MyWebChromeClient()
        var ws = mWebView.settings
        ws.allowFileAccess = true // 设置允许访问文件数据
        ws.domStorageEnabled = true //启用支持DOM Storage
        ws.databaseEnabled = true
        ws.cacheMode = WebSettings.LOAD_NO_CACHE
        // 屏幕自适应
        ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        ws.useWideViewPort = true
        ws.loadWithOverviewMode = true
        //解决缓存
        ws.setSupportZoom(true) // cacheMode...
        ws.javaScriptEnabled = true //启用支持JavaScript
//        ws.userAgentString = ???  //todo
        ws.setGeolocationEnabled(true) //允许地理位置可用
//        ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS // TEXT_AUTOSIZING  SINGLE_COLUMN
        //去掉缩放按钮
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ws.builtInZoomControls = true
            ws.displayZoomControls = false
        } else {
            getControlls()
        }
        mWebView.webViewClient = object : WebViewClient() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                var url:String = request?.url.toString()
                val conn = IsInternet.isNetworkAvalible(this@WebActivity)
                if (!conn) {
                    mNotWebLayout.visibility = View.VISIBLE
                    //调用网络工具类中的方法，跳转到设置网络的界面
                    mRWebLayout.visibility = View.GONE
                } else {
                    mNotWebLayout.visibility = View.GONE
                    mRWebLayout.visibility = View.VISIBLE
                }
                LogTool.e("web",url)
                try {
                    if (url!!.startsWith("weixin://") //微信
                        || url!!.startsWith("alipays://") //支付宝
                        || url!!.startsWith("mailto://") //邮件
                        || url!!.startsWith("tel://")//电话
                        || url!!.startsWith("dianping://")//大众点评
                        || url!!.startsWith("pinduoduo://")//拼多多
                    //其他自定义的scheme
                    ) {
                        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    }
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        mWebView.loadUrl(url)
                        return true
                    }
                } catch (e: Exception) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
//                    var intent = Intent()
//                    intent.setAction("android.intent.action.VIEW")
//                    var content_url:Uri = Uri.parse(url)
//                    intent.setData(content_url)
//                    startActivity(intent)
                    return true//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                val conn = IsInternet.isNetworkAvalible(this@WebActivity)
                if (!conn) {
                    mNotWebLayout.visibility = View.VISIBLE
                    //调用网络工具类中的方法，跳转到设置网络的界面
                    mRWebLayout.visibility = View.GONE
                } else {
                    mNotWebLayout.visibility = View.GONE
                    mRWebLayout.visibility = View.VISIBLE
                }
                LogTool.e("web2",url.toString())
                try {
                    if(url!!.startsWith("taobao://")||url!!.startsWith("tmall://")){
                        val intent = Intent()
                        intent.action = "android.intent.action.View"
                        intent.setClassName("com.taobao.taobao", "com.taobao.browser.BrowserActivity")
                        val uri = Uri.parse(url) //clickUrl,领券地址
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                                  return true
                    }
                } catch (e: java.lang.Exception) {
                    return true
                }
                try {
                    if (url!!.startsWith("weixin://") //微信
                        || url!!.startsWith("alipays://") //支付宝
                        || url!!.startsWith("mailto://") //邮件
                        || url!!.startsWith("tel://")//电话
                        || url!!.startsWith("dianping://")//大众点评
                        || url!!.contains("dianping://")//大众点评
//                        || url!!.startsWith("pinduoduo://")//拼多多   //todo 暂且忽略拼多多
                    //其他自定义的scheme
                    ) {
                        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    }
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        mWebView.loadUrl(url)
                        return true
                    }
                } catch (e: Exception) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
//                    var intent = Intent()
//                    intent.setAction("android.intent.action.VIEW")
//                    var content_url:Uri = Uri.parse(url)
//                    intent.setData(content_url)
//                    startActivity(intent)
                    return true//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }

    private fun getControlls() {
        try {
            val webview = Class.forName("android.webkit.WebView")
            val method: Method = webview.getMethod("getZoomButtonsController")
            zoom_controll = method.invoke(this, true) as ZoomButtonsController
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class MyWebChromeClient : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) { //配置标题
            super.onReceivedTitle(view, title)
        }

        override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
            callback?.invoke(origin, true, false)
            super.onGeolocationPermissionsShowPrompt(origin, callback)
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }
    }
}
