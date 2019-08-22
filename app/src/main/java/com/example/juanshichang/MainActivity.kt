package com.example.juanshichang

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.juanshichang.activity.Reg2LogActivity
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.UserBean
import com.example.juanshichang.fragment.*
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 11:32
 * @文件作用:  首页面
 */
class MainActivity : BaseActivity() {
    private var fragmentList: MutableList<Fragment>? = null
    private var oneFragment: OneFragment? = null
    private var twoFragment: TwoFragment? = null
    private var threeFragment: ThreeFragment? = null
//    private var fourFragment: OneOtherFragment? = null  //设计五个页面 现保留至四个
    private var fourFragment: FourFragment? = null
    private var normalAdapter: NormalAdapter? = null
    override fun getContentView(): Int {
        return R.layout.activity_main
    }
    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@MainActivity, R.color.colorPrimary)
        setBottomView()
        fragmentList = mutableListOf<Fragment>()
        oneFragment = OneFragment()
        twoFragment = TwoFragment()
        threeFragment = ThreeFragment()
        fourFragment = FourFragment()

        fragmentList?.add(oneFragment!!)
        fragmentList?.add(twoFragment!!)
        fragmentList?.add(threeFragment!!)
        fragmentList?.add(fourFragment!!)
//            PermissionHelper.with(this).requestPermission(*PERMISSION_CAM).requestCode(CAM_CODE).request
        MyApp.requestPermission(this@MainActivity)
    }

    @SuppressLint("ResourceType")
    private fun setBottomView() {
//        val xpp = resources.getXml(R.drawable.selector_tab_color)
//        val csl = ColorStateList.createFromXml(resources,xpp)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { // todo 此处 待确定 版本
            //如果SDK版本过低 就关闭 滑动事件 并启用 较Low 的旧版本状态栏
//            val view = android.support.design.widget.BottomNavigationView(this@MainActivity)
            vp_main.setPagingEnabled(false)
            views.visibility = View.VISIBLE
            setTecentBottom()
        } else {
            view.visibility = View.VISIBLE
            setGoogleBottom()
        }
    }

    private val mBottomNavigationView = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.menu -> {
                vp_main.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.study -> {
                vp_main.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
//            R.id.store -> {
//                vp_main.currentItem = 2
//                return@OnNavigationItemSelectedListener true
//            }
            R.id.community -> {
                vp_main.currentItem = 2
                return@OnNavigationItemSelectedListener true
            }
            R.id.me -> {
                vp_main.currentItem = 3
                if (!Util.hasLogin()) {
                    val intent = Intent(this@MainActivity,Reg2LogActivity::class.java)
                    intent.putExtra("type",Reg2LogActivity.LOGINCODE)
                    BaseActivity.Companion.goStartActivity(this@MainActivity, intent)
                    finish()
                } else {
                    ToastUtil.showToast(this@MainActivity, "登录检查通过2")
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    /*private val mTencentBottomView = object : QMUITabSegment.OnTabSelectedListener{
        override fun onDoubleTap(index: Int) { //当某个 Tab 被双击时会触发
        }

        override fun onTabReselected(index: Int) {//被选中状态下再次被点击时会触发
        }

        override fun onTabUnselected(index: Int) { //取消选中时会触发

        }

        override fun onTabSelected(index: Int) { //当某个 Tab 被选中时会触发
            vp_main.currentItem = index
        }

    }*/
    private val mTabLayoutBottom = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {
        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {

        }

        override fun onTabSelected(p0: TabLayout.Tab?) {
            vp_main.currentItem = p0!!.position
            if(p0.position != 3){
                StatusBarUtil.addStatusViewWithColor(this@MainActivity, R.color.colorPrimary)
            }
            if (p0.position == 3) {
                StatusBarUtil.addStatusViewWithColor(this@MainActivity, R.color.white)
                if (!Util.hasLogin()) {
                    val intent = Intent(this@MainActivity,Reg2LogActivity::class.java)
                    intent.putExtra("type",Reg2LogActivity.LOGINCODE)
                    BaseActivity.Companion.goStartActivity(this@MainActivity, intent)
                    finish()
                } else {
                    ToastUtil.showToast(this@MainActivity, "登录检查通过3")
                }
            }
        }

    }

    override fun initData() {
        normalAdapter = NormalAdapter(supportFragmentManager, fragmentList!!)//supportFragmentManager
        vp_main.adapter = normalAdapter
        vp_main.offscreenPageLimit = fragmentList!!.size  //设置预加载
        val token = SpUtil.getIstance().user.usertoken
        Log.e("token", "本地的token值为:" + token)
        if (token != null && !TextUtils.isEmpty(token)) {
            downUser(this@MainActivity)
        }
    }

    internal inner class NormalAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>) :
        FragmentPagerAdapter(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

    }

    /**
     *  腾讯底部栏舍弃 - 采用原生底部栏
     */
    private fun setTecentBottom() {
        views.addTab(views.newTab().setText(R.string.first).setIcon(R.drawable.tab_one))
        views.addTab(views.newTab().setText(R.string.study).setIcon(R.drawable.tab_two))
//        views.addTab(views.newTab().setText(R.string.store).setIcon(R.drawable.tab_three))
        views.addTab(views.newTab().setText(R.string.community).setIcon(R.drawable.tab_three))
        views.addTab(views.newTab().setText(R.string.me).setIcon(R.drawable.tab_four))
        views.addOnTabSelectedListener(mTabLayoutBottom)
        vp_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                views.getTabAt(position)?.isSelected()
            }

        })
    }

    /**
     *  原生谷歌官方底部栏
     */
    private fun setGoogleBottom() {
//        val view = com.google.android.material.bottomnavigation.BottomNavigationView(this@MainActivity)
//        view.isItemHorizontalTranslationEnabled = false  //设置为false时，动画即消失 New
//        view.labelVisibilityMode  = LabelVisibilityMode.LABEL_VISIBILITY_LABELED  // labeled 、auto、unlabeled 、selected  新API中...
//        view.inflateMenu(R.menu.navigation)
//        view.itemTextColor = csl
//        view.itemIconTintList = csl
//        view.itemBackground = null
        view.setOnNavigationItemSelectedListener(mBottomNavigationView)
        var menuItem: MenuItem? = null
        vp_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                // todo new add
                if(position != 3){
                    StatusBarUtil.addStatusViewWithColor(this@MainActivity, R.color.colorPrimary)
                }
                if (position == 3) {
                    StatusBarUtil.addStatusViewWithColor(this@MainActivity, R.color.white)
                    if (!Util.hasLogin()) {
                        val intent = Intent(this@MainActivity,Reg2LogActivity::class.java)
                        intent.putExtra("type",Reg2LogActivity.LOGINCODE)
                        BaseActivity.Companion.goStartActivity(this@MainActivity, intent)
                        finish()
                    } else {
                        ToastUtil.showToast(this@MainActivity, "登录检查通过1")
                    }
                }
                // new add
                if (menuItem != null) {
                    menuItem!!.isChecked = false
                } else {
                    view.getMenu().getItem(0).isChecked = false
                }
                menuItem = view.getMenu().getItem(position)
                menuItem!!.isChecked = true
            }

        })
    }
    companion object {
        private const val CAM_CODE = 101
        /**
         * 获取用户信息
         */
        public fun downUser(context:Context) {
            HttpManager.getInstance().post(Api.USERINFO, Parameter.getBenefitMap(), object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(str)
                        } catch (e: JSONException) {
                            e.printStackTrace();
                        }
                        if (!jsonObj?.optString(JsonParser.JSON_CODE)!!.equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(context, jsonObj!!.optString(JsonParser.JSON_MSG))
                        } else {
                            /*val data = jsonObj!!.getJSONObject("data")
    //                        val age: String = data.getString("age")
                            val avatar: String = data.getString("avatar")
                            val name: String = data.getString("name")
                            var user = SpUtil.getIstance().user
    //                        user.userage = age
                            user.avatar = avatar
                            user.nick_name = name*/
                            val data:UserBean.UserBeans = Gson().fromJson(str,UserBean.UserBeans::class.java)
                            val item = data.data
                            var user = SpUtil.getIstance().user
                            user.avatar = item.avatar
                            user.balance = item.balance
                            user.current_day_benefit = item.current_day_benefit
                            user.current_month_benefit = item.current_month_benefit
                            user.last_day_benefit = item.last_day_benefit
                            user.from_invite_userid = item.from_invite_userid.toLong()
                            user.invite_code = item.invite_code
                            user.nick_name = item.nick_name
                            SpUtil.getIstance().user = user //写入

                        }
                    }
                }

                override fun onCompleted() {
                    Log.e("onCompleted", "用户信息请求完成!")
                }

                override fun onError(e: Throwable?) {
                    Log.e("onError", "用户信息请求错误!")
                }
            })
        }
    }

}

