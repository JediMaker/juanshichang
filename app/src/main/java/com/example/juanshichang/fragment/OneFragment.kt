package com.example.juanshichang.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import com.example.juanshichang.MyApp

import com.example.juanshichang.R
import com.example.juanshichang.activity.PromotionActivity
import com.example.juanshichang.activity.SearcheActivity
import com.example.juanshichang.activity.ShangPinContains
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseFragment
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.MainBannerBean
import com.example.juanshichang.bean.Theme
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.GlideImageLoader
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.activity_shang_pin_contains.*
import kotlinx.android.synthetic.main.fragment_one.*
import org.json.JSONObject
import rx.Subscriber
import kotlin.random.Random

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
//        QMUIStatusBarHelper.translucent(mContext)
        return R.layout.fragment_one
    }

    override fun initViews(savedInstanceState: Bundle) {
        MyApp.requestPermission(mContext!!)
    }

    override fun initData() {
        getBanner()
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
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@OneFragment.mContext!!, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val Data = Gson().fromJson(str, MainBannerBean::class.java)
                        val bannerList: List<Theme> = Data.data.theme_list_get_response.theme_list
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

    var imgs: MutableList<Theme>? = null
    private fun setBanner(bannerList: List<Theme>) {
        //随机数
        imgs = mutableListOf()
        var imgUrls: MutableList<String> = mutableListOf()
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

//        main_banner.visibility = View.VISIBLE
        main_banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR) //显示数字指示器
        //设置指示器位置（当banner模式中有指示器时）
        main_banner.setIndicatorGravity(BannerConfig.CENTER)//指示器居右
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
            if (imgs!![it].goods_num > 1 && imgs!![it].type == 1) {
                var intent = Intent(this.mContext!!, PromotionActivity::class.java)
                intent.putExtra("theme_id", imgs!![it].id.toLong())
                intent.putExtra("goods_num", imgs!![it].goods_num)
                startActivity(intent)
            } else {
                ToastUtil.showToast(this.mContext!!, "这个商品数量小于1,请参详日志")
                Log.e(
                    "shopping",
                    "id:" + imgs!![it].id + "  num" + imgs!![it].goods_num + " type:" + imgs!![it].type
                ) //1.列表
            }
        })
        //banner设置方法全部调用完毕时最后调用
        main_banner.start()
    }
}
