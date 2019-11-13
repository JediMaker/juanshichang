package com.example.juanshichang.activity


import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.view.get
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.juanshichang.MainActivity
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import kotlinx.android.synthetic.main.activity_guide.*
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.imageResource

/**
 * @作者：yzq
 * @创建时间：2019/9/4 14:53
 * @文件作用:  这是用户引导页面
 */
class GuideActivity : BaseActivity(),View.OnClickListener {
    override fun getContentView(): Int {
        return R.layout.activity_guide
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ty ->{
                MyApp.sp.edit().putBoolean("FIRST", false).apply()
                goStartActivity(this@GuideActivity, MainActivity())
                finish()
            }
        }
    }

    override fun initView() {
        guideFl.setPadding(0,QMUIDisplayHelper.getStatusBarHeight(this),0,0)
        MyApp.requestPermission(this)
        //初始化指示器
        val lpNew = LinearLayout.LayoutParams(37,21)
        v1.layoutParams = lpNew
        ty.setOnClickListener(this)
    }

    override fun initData() {
        val guideList = ArrayList<View>()
        guideList.add(getGuideView(R.drawable.spone))
        guideList.add(getGuideView(R.drawable.sptwo))
        guideList.add(getGuideView(R.drawable.spthree))
        val vpAdapter = GuidePageAdapter(guideList)
        guideVp.adapter = vpAdapter
        guideVp.addOnAdapterChangeListener(object : ViewPager.OnAdapterChangeListener{
            override fun onAdapterChanged(
                viewPager: ViewPager,
                oldAdapter: PagerAdapter?,
                newAdapter: PagerAdapter?
            ) {

            }

        })
        guideVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                setIndicator(position)
                if(position == guideList.size - 1){
                    guideFl.visibility = View.VISIBLE
                }else{
                    guideFl.visibility = View.INVISIBLE
                }

            }

        })
    }
    var oldSelect:Int = 0
    private fun setIndicator(p: Int){ //先把之前的颜色 变回来 再改变新的 选中项
        val lpOld = LinearLayout.LayoutParams(21,21)
        val rbOldId = rg.checkedRadioButtonId
        val rbOld = this@GuideActivity.findViewById<RadioButton>(rbOldId)
        val lpNew = LinearLayout.LayoutParams(37,21)
        val rbNew = rg.getChildAt(p) as RadioButton
        if(oldSelect==0 && p == 1){  //此处 判断 很有必要
            lpNew.setMargins(36,0,0,0)
        }else if(oldSelect == 1 && p == 0){
            lpOld.setMargins(36,0,0,0)
        }else{
            lpOld.setMargins(36,0,0,0)
            lpNew.setMargins(36,0,0,0)
        }
        rbOld.layoutParams = lpOld
        rbNew.layoutParams = lpNew
        rbNew.isChecked = true
        oldSelect = p
    }
    private fun getGuideView(id:Int):View{
        val iv = ImageView(this@GuideActivity)
        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        iv.layoutParams = lp
//        iv.backgroundResource = id
        iv.scaleType = ImageView.ScaleType.FIT_XY  //CENTER_INSIDE
        iv.imageResource = id
        return iv
    }
}
/**
 * @作者：yzq
 * @创建时间：2019/9/4 16:08
 * @文件作用: 嫌麻烦 写到这儿喽  谁想改 谁改呗！！！
 */
class GuidePageAdapter:PagerAdapter{
    private var views:List<View>? = null
    constructor(list:List<View>):super(){
        this.views = list
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return  view == `object`
    }

    override fun getCount(): Int {
        if (views!=null)
            return  views?.size!!
        return 0
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
        container.removeView(views?.get(position))
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(views?.get(position))
//        return super.instantiateItem(container, position)
        return views?.get(position)!!
    }
}