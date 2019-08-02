package com.example.juanshichang.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import butterknife.OnClick
import com.example.juanshichang.MyApp

import com.example.juanshichang.R
import com.example.juanshichang.activity.PromotionActivity
import com.example.juanshichang.activity.SearcheActivity
import com.example.juanshichang.adapter.MainGridAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseFragment
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.Banner
import com.example.juanshichang.bean.GridItemBean
import com.example.juanshichang.bean.MainBannerBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.GlideImageLoader
import com.example.juanshichang.utils.StatusBarUtil
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.fragment_one.*
import org.jetbrains.anko.sdk27.coroutines.onItemClick
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:52
 * @文件作用: 首页
 */
class OneFragment : BaseFragment() {
    /* override fun onCreateView(
         inflater: LayoutInflater, container: ViewGroup?,
         savedInstanceState: Bundle?
     ): View? {
         // Inflate the layout for this fragment
         return inflater.inflate(R.layout.fragment_one, container, false)
     }*/
    override fun getLayoutId(): Int {
        return R.layout.fragment_one
    }

    override fun initViews(savedInstanceState: Bundle) {
        QMUIStatusBarHelper.translucent(mContext)
        StatusBarUtil.addStatusViewWithColor(mContext,R.color.colorPrimary)
        MyApp.requestPermission(mContext!!)
    }

    override fun initData() {
        getBanner()
        getGrid()
    }

    @OnClick(R.id.etsearch)
    fun onViewClicked(v: View) {
        when (v.id) {
            R.id.etsearch -> {
                val intent = Intent(mContext, SearcheActivity::class.java)
                //...
                startActivity(intent)
            }
            else -> {
            }
        }
    }

    override fun onStart() {
        super.onStart()
        main_banner.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        main_banner.stopAutoPlay()
    }

    fun getBanner() {
        HttpManager.getInstance().post(Api.MAINBANNER, Parameter.getMainBannerMap(), object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject = JSONObject(str)
                    if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@OneFragment.mContext!!, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val Data = Gson().fromJson(str, MainBannerBean::class.java)
                        val bannerList = Data.data.banner_list
                        this@OneFragment.mContext?.runOnUiThread(Runnable {
                            setBanner(bannerList)
                        })
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

    private fun getGrid() {
        HttpManager.getInstance().post(Api.CHANNELLIST, Parameter.getMainBannerMap(), object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = JSONObject(str)
                    if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@OneFragment.mContext!!, jsonObj!!.optString(JsonParser.JSON_MSG))
                    } else {
                        val data = Gson().fromJson(str, GridItemBean.GridItemBeans::class.java)
                        val gridList = data.data.channel_list
                        this@OneFragment.mContext?.runOnUiThread(Runnable {
                            setGrid(gridList)
                        })
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted", "Grid加载完成!")
            }

            override fun onError(e: Throwable?) {
                Log.e("onCompleted", "Grid加载完成!")
            }

        })
    }

    //    var gridLists: MutableList<GridItemBean.Channel> = mutableListOf()
//    var gridAdapter: MainGridAdapter? = null
    private fun setGrid(gridList: List<GridItemBean.Channel>) {
        var gridAdapter = MainGridAdapter(this.mContext!!, gridList)
        val size = gridList.size   //动态设置列数
        var column = 0
        if (size % 2 != 0) {
            val l = size / 2 as Int
            column = l + 1
        } else {
            column = size / 2 as Int
        }
        main_gridView.numColumns = column
        main_gridView.adapter = gridAdapter
        gridAdapter.notifyDataSetChanged()
        main_gridView.onItemClick { p0, p1, p2, p3 ->
            ToastUtil.showToast(mContext!!, "p0:$p0,p1:$p1,p2:$p2,p3:$p3")
            Log.e("jjjj","p0:$p0,p1:$p1,p2:$p2,p3:$p3")
        }

    }

    var imgs: MutableList<Banner>? = null
    private fun setBanner(bannerList: List<Banner>) {
        //随机数
        imgs = mutableListOf()
        var imgUrls: MutableList<String> = mutableListOf() //设置返回数据如果多于5 则 只随机抽取5张轮播 否则 显示全部
        if (bannerList.size > 5) {
            imgs?.clear()
            while (imgs?.size!! < 5) {
                var r = (0 until bannerList.size).random()// (0…10).random() === (0 until 11).random()
                if (!imgs!!.contains(bannerList[r])) {
                    imgs?.add(bannerList[r])
                }
            }
        } else {
            imgs?.clear()
            imgs?.addAll(bannerList)
        }
        for (index in 0 until imgs!!.size) {
            imgUrls.add(imgs!![index].image_url)
        }

        main_banner.visibility = View.VISIBLE
        main_banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR) //显示数字指示器
        //设置指示器位置（当banner模式中有指示器时）
        main_banner.setIndicatorGravity(BannerConfig.LEFT)//指示器居右
        //设置图片加载器
        main_banner.setImageLoader(GlideImageLoader())
        //设置图片集合
        main_banner.setImages(imgUrls)
        //设置动画效果
        main_banner.setBannerAnimation(Transformer.Default)
        //设置轮播图片间隔时间（不设置默认为2000）
        main_banner.setDelayTime(4500)
        //设置是否自动轮播（不设置则默认自动）
        main_banner.isAutoPlay(true)
        //设置轮播要显示的标题和图片对应（如果不传默认不显示标题）
        //meBanner.setBannerTitles(images);
        //点击事件请放到start()前
        main_banner.setOnBannerListener(OnBannerListener {
            if (imgs!![it].type.equals("goods")) {
                var intent = Intent(this.mContext!!, PromotionActivity::class.java)
                intent.putExtra("banner_id", imgs!![it].banner_id.toLong())
                startActivity(intent)
            } else {
                ToastUtil.showToast(this.mContext!!, "这个商品类型异常,快去看日志...")
                Log.e(
                    "shopping",
                    "id:" + imgs!![it].banner_id + "  type" + imgs!![it].type
                ) //1.列表
            }
        })
        //banner设置方法全部调用完毕时最后调用
        main_banner.start()
    }
}
