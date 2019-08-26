package com.example.juanshichang.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.OnClick
import com.example.juanshichang.MyApp

import com.example.juanshichang.R
import com.example.juanshichang.activity.ClassTypeActivity
import com.example.juanshichang.activity.SearcheActivity
import com.example.juanshichang.activity.WebActivity
import com.example.juanshichang.base.*
import com.example.juanshichang.bean.*
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.CustomViewPager
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.TabCreateUtils
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_one.*
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import org.w3c.dom.Text
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:52
 * @文件作用: 首页
 */
class OneFragment : BaseFragment(){

    private var tabData:List<TabOneBean.Category>? = null
    private var mOr:RelativeLayout? = null
    private var mTl:LinearLayout? = null
    private var tEdit:EditText? = null
    private var tSearch:TextView? = null
    private var mainBack:ImageView? = null
    private var mainTab: MagicIndicator? = null
    private var mainVp:CustomViewPager? = null
    private var tabAp:CommonNavigatorAdapter? = null
    private var mainAdapter: NormalAdapter? = null
    private var fragmentList:ArrayList<Fragment>? = null
    private var oneFragment: SelectionFragment? = null
    private var twoFragment: OneOtherFragment? = null
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                2->{
                    if(tabData!=null){
                        setTab(tabData)
                    }else{
                        sendEmptyMessageDelayed(2, 50)
                    }
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_one
    }

    override fun initViews(savedInstanceState: Bundle) {
        MyApp.requestPermission(mContext!!)
        mainTab = mBaseView?.findViewById<MagicIndicator>(R.id.mainTab)
        mainVp = mBaseView?.findViewById<CustomViewPager>(R.id.vpOne)
        mainBack = mBaseView?.findViewById<ImageView>(R.id.mainBack)
        //头部空间 1
        mOr = mBaseView?.findViewById<RelativeLayout>(R.id.homeRelatie)
        //2
        mTl = mBaseView?.findViewById<LinearLayout>(R.id.mainT)
        tEdit = mBaseView?.findViewById<EditText>(R.id.mainTEdit)
        tSearch = mBaseView?.findViewById<TextView>(R.id.mainTSearch)
        mainVp?.setPagingEnabled(false) //设置ViewPager不可滑动
        getOneT(0)
        fragmentList = arrayListOf()
        oneFragment = SelectionFragment()
        twoFragment = OneOtherFragment()
        fragmentList!!.add(oneFragment!!)
        fragmentList!!.add(twoFragment!!)
        mainAdapter = NormalAdapter(childFragmentManager,fragmentList as List<Fragment>)
        mainVp?.adapter = mainAdapter
    }

    override fun initData() {
        handler.sendEmptyMessageDelayed(2, 50)
    }

    @OnClick(R.id.etsearchs,R.id.scan_home,R.id.message_home,R.id.mainTSearch)
    fun onViewClicked(v: View) {
        when (v.id) {
            R.id.etsearchs -> {
                val intent = Intent(mContext, SearcheActivity::class.java)
                //...
                startActivity(intent)
            }
            R.id.mainTSearch ->{
                val str = getEditText()
                if(!TextUtils.isEmpty(str)){
                    var intent = Intent(mContext!!, ClassTypeActivity::class.java)
                    intent.putExtra("keyword",str)
                    startActivity(intent)
                    tEdit?.text = null
                }else{
                    ToastUtil.showToast(mContext!!, "请输入搜索关键字")
                }
            }
            R.id.scan_home ->{ //扫一扫
                ToastUtil.showToast(mContext!!,"程序猿小哥 扫一扫 日夜赶工中...")
            }
            R.id.message_home ->{ //消息
                ToastUtil.showToast(mContext!!,"程序猿小哥 消息 日夜赶工中...")
            }
            else -> {
                ToastUtil.showToast(mContext!!,"程序猿小哥 日夜赶工中...")
            }
        }
    }
    private fun getEditText(): String {//获取Edit数据
        val text = tEdit?.text.toString().trim()
        if (text.length > 0 && !TextUtils.isEmpty(text)) {
            return text
        }
        return ""
    }
    override fun onResume() {
        super.onResume()
        //判空 重走
        if(fragmentList == null){
            fragmentList = arrayListOf()
            if(oneFragment == null){
                oneFragment = SelectionFragment()
            }
            if(twoFragment == null){
                twoFragment = OneOtherFragment()
            }
            fragmentList!!.add(oneFragment!!)
            fragmentList!!.add(twoFragment!!)
        }
        if(mainVp == null){
            mainVp = mBaseView?.findViewById<CustomViewPager>(R.id.vpOne)
            mainAdapter = NormalAdapter(childFragmentManager,fragmentList as List<Fragment>)
            //写入
            mainAdapter = NormalAdapter(childFragmentManager,fragmentList as List<Fragment>)
            mainVp?.adapter = mainAdapter
        }
        //Viewpager 滑动 监听 todo 已废弃！！！
        mainVp?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

            }

        })
    }
    //获取列表数据 unlogin
    private fun getOneT(parent_id:Int){
        HttpManager.getInstance().post(Api.CATEGORY,Parameter.getTabData(parent_id),object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject = JSONObject(str)
                    if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(mContext!!, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val data = Gson().fromJson(str,TabOneBean.TabOneBeans::class.java)
                        val list = data.data.category_list
                        if(list.size!=0){
                            tabData = list
//                            mainTab!!.post(object :Runnable{
//                                override fun run() {
//                                    setTab(list)
//                                }
//                            })
                        }
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted", "Tab加载完成!")
            }

            override fun onError(e: Throwable?) {
                Log.e("onError", "Tab加载失败!"+e)
            }
        })
    }
    companion object {
        var WebUrl: String? = null
        //获取链接 跳转
        fun getWebLink(channel_id: Long, context: Context) {
            //Parameter.getGridClickMap(channel_id, 0, 20)
            HttpManager.getInstance()
                .post(
                    Api.CHANNEL,
                    Parameter.getBaseSonMap("channel_id", channel_id, 0, 20),
                    object : Subscriber<String>() {
                        override fun onNext(str: String?) {
                            if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                                var jsonObj: JSONObject = JSONObject(str)
                                if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                                    ToastUtil.showToast(context, jsonObj.optString(JsonParser.JSON_MSG))
                                } else {
                                    val jsonObjs = jsonObj.getJSONObject("data")
                                    Log.e("kkkkk", jsonObjs.toString())
                                    WebUrl = jsonObjs.getString("url")
                                    Log.e("kkkkk", WebUrl.toString())
//                            context.runOnUiThread(Runnable {
//                                var intent = Intent(context,WebActivity::class.java)
//                                intent.putExtra("mobile_short_url",WebUrl!!.trim())  //todo 偷天换日法
//                                startActivity(intent)
//                            })
                                    context.runOnUiThread {
                                        var intent = Intent(context, WebActivity::class.java)
                                        intent.putExtra("mobile_short_url", WebUrl!!.trim())  //todo 偷天换日法
                                        BaseActivity.goStartActivity(context, intent)
                                    }
                                }
                            }
                        }

                        override fun onCompleted() {
                            Log.e("onCompleted", "Banner加载完成!")
                        }

                        override fun onError(e: Throwable?) {
                            Log.e("onError", "Banner加载失败!")
                        }
                    })
        }
        fun getTwoT(parent_id:Int,context:Context){
            HttpManager.getInstance().post(Api.CHANNEL,Parameter.getTabData(parent_id),object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(context, jsonObj.optString(JsonParser.JSON_MSG))
                        } else {

                        }
                    }
                }

                override fun onCompleted() {
                    Log.e("onCompleted", "Tab2加载完成!")
                }

                override fun onError(e: Throwable?) {
                    Log.e("onError", "Tab2加载失败!"+e)
                }
            })
        }
        fun getThreeT(parent_id:Int,context:Context){
            HttpManager.getInstance().post(Api.CHANNEL,Parameter.getTabData(parent_id),object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(context, jsonObj.optString(JsonParser.JSON_MSG))
                        } else {

                        }
                    }
                }

                override fun onCompleted() {
                    Log.e("onCompleted", "Tab3加载完成!")
                }

                override fun onError(e: Throwable?) {
                    Log.e("onError", "Tab3加载失败!"+e)
                }
            })
        }
    }

    private fun setTab(tabData: List<TabOneBean.Category>?) {
        val dataTab = ArrayList<String>()
        if(tabData!=null){
            dataTab.add("精选")
            for (i in 0 until tabData.size){
                dataTab.add(tabData[i].name)
            }
            Log.e("tastaaa2",""+dataTab.size)
            tabAp =TabCreateUtils.setOrangeTab(mContext!!,mainTab,dataTab,object : TabCreateUtils.onTitleClickListener {
                override fun onTitleClick(index: Int) {
                    if(index == 0){
                        mTl?.visibility = View.GONE
                        mOr?.visibility = View.VISIBLE
                        mainBack?.visibility = View.VISIBLE
                        mainVp?.currentItem = 0
                        StatusBarUtil.addStatusViewWithColor(this@OneFragment.activity, R.color.colorPrimary)
                    } else{
                        mOr?.visibility = View.GONE
                        mTl?.visibility = View.VISIBLE
                        mainBack?.visibility = View.INVISIBLE
                        mainVp?.currentItem = 1
                        StatusBarUtil.addStatusViewWithColor(this@OneFragment.activity, R.color.white)
                    }
                }
            })

        }
    }
    //适配器
    internal inner class NormalAdapter(fm: FragmentManager,fragmentList: List<Fragment>) :
        FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return fragmentList!![position]
        }

        override fun getCount(): Int {
            return fragmentList!!.size
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
