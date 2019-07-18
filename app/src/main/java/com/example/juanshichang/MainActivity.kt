package com.example.juanshichang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 11:32
 * @文件作用:  首页面
 */
class MainActivity : BaseActivity() {
    private var fragmentList = mutableListOf<Fragment>()
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
        oneFragment = OneFragment()
        twoFragment = TwoFragment()
        threeFragment = ThreeFragment()
        fourFragment = FourFragment()
        fiveFragment = FiveFragment()

        fragmentList.add(oneFragment!!)
        fragmentList.add(twoFragment!!)
        fragmentList.add(threeFragment!!)
        fragmentList.add(fourFragment!!)
        fragmentList.add(fiveFragment!!)

        normalAdapter = NormalAdapter(supportFragmentManager, fragmentList)
        vp_main.adapter = normalAdapter
        initAllView()
        vp_main.offscreenPageLimit = fragmentList.size
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
    private fun initAllView() {
        bottom_navigation.setOnNavigationItemSelectedListener(mBottomNavigationView)
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
                    bottom_navigation.getMenu().getItem(0).isChecked = false
                }
                menuItem = bottom_navigation.getMenu().getItem(position)
                menuItem!!.isChecked = true
            }

        })
    }

    override fun initData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    internal inner class NormalAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>) :
        FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

    }
}

