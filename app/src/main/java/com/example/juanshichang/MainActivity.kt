package com.example.juanshichang

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.UserBean
import com.example.juanshichang.fragment.*
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.*
import com.example.juanshichang.widget.LiveDataBus
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber
import java.util.*

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
    private var shopFragment: ShopListFragment? = null   //购物车
    private var fourFragment: FourFragment? = null
    private var normalAdapter: NormalAdapter? = null
    private var bus = LiveDataBus.get()
    // 用于确定 首页面Tab 是否处在第一个
    private var topTabIsOne: Boolean = true

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
        shopFragment = ShopListFragment()
        fourFragment = FourFragment()
//        oFragment = OneOtherFragment()
        fragmentList?.add(oneFragment!!)
        fragmentList?.add(twoFragment!!)
        fragmentList?.add(threeFragment!!)
        fragmentList?.add(shopFragment!!)
        fragmentList?.add(fourFragment!!)
//            PermissionHelper.with(this).requestPermission(*PERMISSION_CAM).requestCode(CAM_CODE).request
        MyApp.requestPermission(this@MainActivity)
        bus.with("mainTopStatusView", Int::class.java)
            .observe(this, object : Observer<Int> {
                override fun onChanged(t: Int?) {
                    StatusBarUtil.addStatusViewWithBack(this@MainActivity, t!!)
                }
            })
    }

    override fun initData() {
        normalAdapter =
            NormalAdapter(supportFragmentManager, fragmentList!!)//supportFragmentManager
        vp_main.adapter = normalAdapter
//        vp_main.offscreenPageLimit = fragmentList!!.size  //设置预加载  todo 后期需调优Fragment为懒加载...
        val token = SpUtil.getIstance().user.usertoken
        LogTool.e("token", "本地的token值为:" + token)
        if (token != null && !TextUtils.isEmpty(token)) {
            downUser(this@MainActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!Util.hasLogin()) { //如果没有登录就关闭滑动
            vp_main.setPagingEnabled(false)
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                vp_main.setPagingEnabled(true) //如果登录 并且版本大于25 开启滑动
            }
        }

    }

    @SuppressLint("ResourceType")
    private fun setBottomView() {
//        val xpp = resources.getXml(R.drawable.selector_tab_color)
//        val csl = ColorStateList.createFromXml(resources,xpp)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) { // todo 此处 待确定 版本 预设25...
            //如果SDK版本过低 就关闭 滑动事件
            vp_main.setPagingEnabled(false)
//            views.visibility = View.VISIBLE
//            setTabBottom()
        }
//        else {}
        view.visibility = View.VISIBLE
        setGoogleBottom()
        bus.with("topisone", Boolean::class.java).observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                topTabIsOne = t!!
            }
        })
    }

    private val mBottomNavigationView =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
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
                R.id.store -> {
                    if (Util.hasLogin(this)) {
                        vp_main.currentItem = 3
                        return@OnNavigationItemSelectedListener true
                    }
                    return@OnNavigationItemSelectedListener false
                }
                R.id.me -> {
                    if (Util.hasLogin(this)) {
                        vp_main.currentItem = 4
                        return@OnNavigationItemSelectedListener true
                    }
                    /*if (!Util.hasLogin()) { //登录检查
                        val intent = Intent(this@MainActivity,Reg2LogActivity::class.java)
                        intent.putExtra("type",Reg2LogActivity.LOGINCODE)
                        BaseActivity.Companion.goStartActivity(this@MainActivity, intent)
                        finish()
                    } else {
                        ToastTool.showToast(this@MainActivity, "登录检查通过2")
                    }*/
                    return@OnNavigationItemSelectedListener false
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
    internal inner class NormalAdapter(
        fm: FragmentManager,
        private val fragmentList: List<Fragment>
    ) :
        FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

    }

    /**
     *  采用原生Tab栏
     *  不好使  废弃!!!
     */
    private fun setTabBottom() {
        views.addTab(views.newTab().setText(R.string.first).setIcon(R.drawable.tab_one))
        views.addTab(views.newTab().setText(R.string.study).setIcon(R.drawable.tab_two))
        views.addTab(views.newTab().setText(R.string.community).setIcon(R.drawable.tab_three_t))
        views.addTab(views.newTab().setText(R.string.store).setIcon(R.drawable.tab_shopcar))
        views.addTab(views.newTab().setText(R.string.me).setIcon(R.drawable.tab_four))
        views.addOnTabSelectedListener(mTabLayoutBottom)
        vp_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                views.getTabAt(position)?.isSelected()
            }

        })
        //这是一个返回首页的 广播 --- 不好使 废弃
        /*bus.with("mainGo", Int::class.java).observe(this, object : Observer<Int> {
            override fun onChanged(t: Int?) {
                val tab = views.getTabAt(t!!)
                LogTool.e("lowTab", tab.toString())
                tab?.select()
            }
        })*/
    }
    private val mTabLayoutBottom = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {
        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {

        }

        override fun onTabSelected(p0: TabLayout.Tab?) {
            when (p0?.position) {
                0 -> {
                    if (topTabIsOne) {
                        bus.with("mainTopStatusView").value = R.color.colorPrimary
                    } else {
                        bus.with("mainTopStatusView").value = R.color.white
                    }
                    vp_main.currentItem = 0
                }
                1, 2 -> {
                    bus.with("mainTopStatusView").value = R.color.white
                    vp_main.currentItem = p0.position
                }
                3 -> {
                    if (!Util.hasLogin(this@MainActivity)) {
                        return
                    } else {
                        bus.with("mainTopStatusView").value = R.color.white
                        vp_main.currentItem = p0.position
                    }
                }
                4 -> {
                    if (!Util.hasLogin(this@MainActivity)) {
                        return
                    } else {
                        StatusBarUtil.addStatusViewWithBack(
                            this@MainActivity,
                            R.drawable.bg_me_twotop
                        )
                        vp_main.currentItem = p0.position
                    }
                }
            }
        }

    }
    /**
     *  原生谷歌官方底部栏
     */
    private fun setGoogleBottom() {
        var oldpositionOffset = 0f
        view.setOnNavigationItemSelectedListener(mBottomNavigationView)
        var menuItem: MenuItem? = null
        vp_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                oldpositionOffset = 0f
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position == 2 && positionOffset > oldpositionOffset && oldpositionOffset != 0f) {
                    if (!Util.hasLogin(this@MainActivity)) {
                        vp_main.currentItem = 2
//                        LiveDataBus.get().with("mainGo").value =  2  //这设置 没有登录的页面停留
                        return
                    }
                }
                oldpositionOffset = positionOffset
            }

            override fun onPageSelected(position: Int) {
                LogTool.e("vpmain3", "position:$position")
                // new add
                if (position == 3 && !Util.hasLogin()) {
                    return
                } else {
                    if (menuItem != null) {
                        menuItem!!.isChecked = false
                    } else {
                        view.getMenu().getItem(0).isChecked = false
                    }
                    menuItem = view.getMenu().getItem(position)
                    menuItem!!.isChecked = true
                }
                when (position) {
                    0 -> {
                        if (topTabIsOne) {
                            bus.with("mainTopStatusView").value = R.color.colorPrimary
                        } else {
                            bus.with("mainTopStatusView").value = R.color.white
                        }
                    }
                    1, 2, 3 -> {
                        bus.with("mainTopStatusView").value = R.color.white
                    }
                    4 -> {
//                        bus.with("mainTopStatusView").value = R.drawable.bg_me_twotop
                        bus.with("mainTopStatusView").value = R.drawable.bg_me_twotop
//                        StatusBarUtil.addStatusViewWithBack(this@MainActivity,R.drawable.bg_me_twotop)
                    }
                }
            }
        })
        //这是一个返回首页的 广播
        bus.with("mainGo", Int::class.java).observe(this, object : Observer<Int> {
            override fun onChanged(t: Int?) {
                vp_main.currentItem = t!!
                menuItem?.isChecked = false
                view.getMenu().getItem(t).isChecked = true
            }
        })
    }

    companion object {
        private const val CAM_CODE = 101
        /**
         * 获取用户信息
         */
        public fun downUser(context: Context) {
            HttpManager.getInstance()
                .post(Api.USERINFO, Parameter.getBenefitMap(), object : Subscriber<String>() {
                    override fun onNext(str: String?) {
                        if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                            var jsonObj: JSONObject? = null
                            try {
                                jsonObj = JSONObject(str)
                            } catch (e: JSONException) {
                                e.printStackTrace();
                            }
                            if (!jsonObj?.optString(JsonParser.JSON_CODE)!!.equals(JsonParser.JSON_SUCCESS)) {
                                ToastUtil.showToast(
                                    context,
                                    jsonObj!!.optString(JsonParser.JSON_MSG)
                                )
                            } else {
                                /*val data = jsonObj!!.getJSONObject("data")
        //                        val age: String = data.getString("age")
                                val avatar: String = data.getString("avatar")
                                val name: String = data.getString("name")
                                var user = SpUtil.getIstance().user
        //                        user.userage = age
                                user.avatar = avatar
                                user.nick_name = name*/
                                val data: UserBean.UserBeans =
                                    Gson().fromJson(str, UserBean.UserBeans::class.java)
                                val item = data.data
                                var user = SpUtil.getIstance().user
                                user.avatar = item.avatar
                                user.balance = Util.getFloatPrice(item.balance.toLong()).toFloat()
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
                        LogTool.e("onCompleted", "用户信息请求完成!")
                    }

                    override fun onError(e: Throwable?) {
                        LogTool.e("onError", "用户信息请求错误!")
                    }
                })
        }

    }

    var isExit: Boolean = false
    override fun onBackPressed() {
//        super.onBackPressed()
        var tExit: Timer? = null
        if (!isExit) {
            isExit = true
            ToastUtil.showToast(this, "再来一次 退出App")
            tExit = Timer()
            tExit.schedule(object : TimerTask() {
                override fun run() {
                    isExit = false
                }
            }, 2000)
        } else {
            ActivityManager().exit()
        }
    }
}

