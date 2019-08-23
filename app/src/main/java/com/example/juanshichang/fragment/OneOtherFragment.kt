package com.example.juanshichang.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager


import com.example.juanshichang.R
import com.example.juanshichang.base.BaseFragment

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:54
 * @文件作用: 社区界面
 */
class OneOtherFragment : BaseFragment() {
    private var vp:ViewPager? =null
    private var nA: NormalAdapter? = null
    private var fragmentList: MutableList<Fragment>? = null
    private var oneFragment: OneOtherFragment? = null
    private var twoFragment: TwoFragment? = null
    private var two2Fragment: TwoFragment? = null
    override fun getLayoutId(): Int {
        return  R.layout.fragment_four
    }

    override fun initViews(savedInstanceState: Bundle) {
    }

    override fun initData() {
        fragmentList = mutableListOf<Fragment>()
        oneFragment = OneOtherFragment()
        twoFragment = TwoFragment()
        two2Fragment = TwoFragment()
        fragmentList!!.add(oneFragment!!)
        fragmentList!!.add(twoFragment!!)
        fragmentList!!.add(two2Fragment!!)
        vp = mBaseView?.findViewById(R.id.vp)
        nA = NormalAdapter(childFragmentManager, fragmentList!!)
    }
    internal inner class NormalAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>) :
        FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

    }
}
