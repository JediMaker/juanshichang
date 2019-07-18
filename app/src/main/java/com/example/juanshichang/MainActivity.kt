package com.example.juanshichang

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.tabs.TabItem
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.QMUITabSegment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 11:32
 * @文件作用:  首页面
 */
class MainActivity : BaseActivity() {
    private var fragmentList:MutableList<Fragment>? = null
    private var oneFragment: OneFragment? = null
    private var twoFragment: TwoFragment? = null
    private var threeFragment: ThreeFragment? = null
    private var fourFragment: FourFragment? = null
    private var fiveFragment: FiveFragment? = null
    private var normalAdapter: NormalAdapter? = null
    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        setBottomView()
        fragmentList = mutableListOf<Fragment>()
        oneFragment = OneFragment()
        twoFragment = TwoFragment()
        threeFragment = ThreeFragment()
        fourFragment = FourFragment()
        fiveFragment = FiveFragment()

        fragmentList?.add(oneFragment!!)
        fragmentList?.add(twoFragment!!)
        fragmentList?.add(threeFragment!!)
        fragmentList?.add(fourFragment!!)
        fragmentList?.add(fiveFragment!!)
    }

    @SuppressLint("ResourceType")
    private fun setBottomView() {
//        val xpp = resources.getXml(R.drawable.selector_tab_color)
//        val csl = ColorStateList.createFromXml(resources,xpp)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){ // todo 此处 待确定 版本
//            val view = android.support.design.widget.BottomNavigationView(this@MainActivity)
            views.visibility = View.VISIBLE
            setTecentBottom()
        }else{
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
            R.id.store -> {
                vp_main.currentItem = 2
                return@OnNavigationItemSelectedListener true
            }
            R.id.community -> {
                vp_main.currentItem = 3
                return@OnNavigationItemSelectedListener true
            }
            R.id.me -> {
                vp_main.currentItem = 4
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    private val mTencentBottomView = object : QMUITabSegment.OnTabSelectedListener{
        override fun onDoubleTap(index: Int) { //当某个 Tab 被双击时会触发
        }

        override fun onTabReselected(index: Int) {//被选中状态下再次被点击时会触发
        }

        override fun onTabUnselected(index: Int) { //取消选中时会触发

        }

        override fun onTabSelected(index: Int) { //当某个 Tab 被选中时会触发
            vp_main.currentItem = index
        }

    }
    override fun initData() {

        normalAdapter = NormalAdapter(supportFragmentManager, fragmentList!!)
        vp_main.adapter = normalAdapter
        vp_main.offscreenPageLimit = fragmentList!!.size
    }

    internal inner class NormalAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

    }

    /**
     *  腾讯底部栏
     */
    private fun setTecentBottom() {
//        val view : QMUITabSegment= QMUITabSegment(this@MainActivity,false)
//        views.setDefaultTabIconPosition(QMUITabSegment.ICON_POSITION_TOP) // top
//        views.setHasIndicator(false) //设置是否显示下划线
        val normalColor = QMUIResHelper.getAttrColor(this@MainActivity, R.attr.qmui_config_color_gray_6);
        val selectColor = QMUIResHelper.getAttrColor(this@MainActivity, R.attr.qmui_config_color_blue);
        views.setDefaultNormalColor(normalColor); //设置tab正常下的颜色  
        views.setDefaultSelectedColor(selectColor); //设置tab选中下的颜色  
//        views.setTabTextSize(20)
        val component:QMUITabSegment.Tab  = QMUITabSegment.Tab(
            ContextCompat.getDrawable(this, R.mipmap.icon_home_sel),
            ContextCompat.getDrawable(this, R.mipmap.icon_home_nor),
            "首页", true
        )
        val component1:QMUITabSegment.Tab  = QMUITabSegment.Tab(
            ContextCompat.getDrawable(this, R.mipmap.icon_home_sel),
            ContextCompat.getDrawable(this, R.mipmap.icon_home_nor),
            "学院", true
        )
        val component2:QMUITabSegment.Tab  = QMUITabSegment.Tab(
            ContextCompat.getDrawable(this, R.mipmap.icon_home_sel),
            ContextCompat.getDrawable(this, R.mipmap.icon_home_nor),
            "小店", true
        )
        val component3:QMUITabSegment.Tab  = QMUITabSegment.Tab(
            ContextCompat.getDrawable(this, R.mipmap.icon_home_sel),
            ContextCompat.getDrawable(this, R.mipmap.icon_home_nor),
            "社区", true
        )
        val component4:QMUITabSegment.Tab  = QMUITabSegment.Tab(
            ContextCompat.getDrawable(this, R.mipmap.icon_home_sel),
            ContextCompat.getDrawable(this, R.mipmap.icon_home_nor),
            "我的", true
        )
        views.addTab(component)
        views.addTab(component1)
        views.addTab(component2)
        views.addTab(component3)
        views.addTab(component4)
        views.addOnTabSelectedListener(mTencentBottomView)
        views.setupWithViewPager(vp_main)
        views.notifyDataChanged()
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
//        view.setOnNavigationItemSelectedListener(mBottomNavigationView)
        var menuItem:MenuItem? = null
        vp_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
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

}

