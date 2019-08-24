package com.example.juanshichang.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.GridView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.juanshichang.R
import com.example.juanshichang.activity.PromotionActivity
import com.example.juanshichang.activity.ShangPinContains
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.bean.*
import com.example.juanshichang.fragment.OneFragment.Companion.WebUrl
import com.example.juanshichang.fragment.OneFragment.Companion.getWebLink
import com.example.juanshichang.utils.GlideImageLoader
import com.example.juanshichang.utils.ToastUtil
import com.example.juanshichang.utils.Util
import com.example.juanshichang.utils.glide.GlideUtil
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.listener.OnBannerListener
import com.zhy.autolayout.AutoLinearLayout
import kotlinx.android.synthetic.main.fragment_one.*
import kotlinx.android.synthetic.main.home_banner_item.view.*
import kotlinx.android.synthetic.main.home_grid_item.view.*
import kotlinx.android.synthetic.main.home_recycler_item.view.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onItemClick

class HomeAdapter : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    var b_i = 1
    var g_i = 1
    var r_i = 1
    var imgUrls: MutableList<String>? = mutableListOf()
    var imgs: MutableList<MainBannerBean.Banner>? = mutableListOf()
    var gridList = mutableListOf<GridItemBean.Channel>()
    var gridAdapter: MainGridAdapter? = null
    var rvList = mutableListOf<MainRecyclerBean.Theme>()
    var rvAdapter: MainRecyclerAdapter? = null

    constructor(data: List<MultiItemEntity>) : super(data) {
        addItemType(HomeEntity.TYPE_BANNER, R.layout.home_banner_item)
        addItemType(HomeEntity.TYPE_GRID, R.layout.home_grid_item)
        addItemType(HomeEntity.TYPE_RECYCLER, R.layout.home_recycler_item)
    }

    override fun convert(helper: BaseViewHolder?, item: MultiItemEntity?) {
        val homeEntity = item as HomeEntity
        when (homeEntity.itemType) {
            HomeEntity.TYPE_BANNER -> {
                setBanner(homeEntity, helper)
            }
            HomeEntity.TYPE_GRID -> {
                setGrid(homeEntity, helper)
            }
            HomeEntity.TYPE_RECYCLER -> {
                setRecycler(homeEntity, helper)
            }
            else -> {
            }
        }
    }
    fun recyclerAddData(data: List<MainRecyclerBean.Theme>){ //自定义数据添加方法
        for (index in 0 until data.size){
            rvAdapter?.addData(data[index])
        }
    }
    private fun setRecycler(item: HomeEntity, helper: BaseViewHolder?) {
//        val home_recycler = helper!!.getView<AutoLinearLayout>(R.id.auto_3).home_recycler
        val home_recycler = helper!!.itemView.find<RecyclerView>(R.id.home_recycler)
        if (r_i == 1) {
            rvList.clear()
            val lm = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
            /*val lms =object : LinearLayoutManager(mContext){
                override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
//                    return super.generateDefaultLayoutParams()
                    return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                }
            }
            lms.orientation = RecyclerView.VERTICAL*/
            home_recycler.layoutManager = lm
            r_i = 2
        }
        for (index in 0 until item.recyclers?.size!!){
            if (!rvList.contains(item.recyclers!![index])) {
                rvList.add(item.recyclers!![index])
            }
        }
        rvAdapter = MainRecyclerAdapter(R.layout.item_main_recycler,mContext)
        home_recycler.adapter = rvAdapter
        rvAdapter?.setNewData(rvList)
        rvAdapter?.setOnItemChildClickListener(object : OnItemChildClickListener{
            override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                when(view?.id){ //theme_id
                    R.id.head_iv ->{ //头图片
                        if(rvList[position].theme_goods_servicer.equals("pdd")){//拼多多
                            val intent = Intent(mContext,PromotionActivity::class.java)
                            intent.putExtra("id",rvList[position].theme_id.toLong()) //theme_id
                            intent.putExtra("idName","theme_id")
                            BaseActivity.goStartActivity(mContext,intent)
                        }
                    }
                    R.id.r1 ->{
                        if(rvList[position].theme_goods_servicer.equals("pdd")){//拼多多
                            val intent = Intent(mContext,ShangPinContains::class.java)
                            intent.putExtra("goods_id",rvList[position].theme_goods_list[0].goods_id)
                            BaseActivity.goStartActivity(mContext,intent)
                        }
                    }
                    R.id.r2 ->{
                        if(rvList[position].theme_goods_servicer.equals("pdd")){//拼多多
                            val intent = Intent(mContext,ShangPinContains::class.java)
                            intent.putExtra("goods_id",rvList[position].theme_goods_list[1].goods_id)
                            BaseActivity.goStartActivity(mContext,intent)
                        }
                    }
                    else->{}
                }
            }
        })
    }

    private fun setGrid(item: HomeEntity, helper: BaseViewHolder?) {
//        val home_gridView = helper!!.getView<AutoLinearLayout>(R.id.auto_2).home_gridView
        val home_gridView = helper!!.getView<GridView>(R.id.home_gridView)
//        var grid = helper?.convertView!!.findViewById<GridView>(R.id.main_gridView)
//        val grid = helper!!.itemView.find<GridView>(R.id.home_gridView)
        if (g_i == 1) {
            gridList.clear()
            gridAdapter = MainGridAdapter(mContext, gridList)
            home_gridView.adapter = gridAdapter
            g_i = 2
        }
        for (index in 0 until item.grids?.size!!){
            if (!gridList.contains(item.grids!![index])) {
                gridList.add(item.grids!![index])
            }
        }
        val size = gridList.size   //动态设置列数
        var column = 0
        if (size % 2 != 0) {
            val l = size / 2 as Int
            column = l + 1
        } else {
            column = size / 2 as Int
        }
        home_gridView.numColumns = column
        gridAdapter?.notifyDataSetChanged()
        home_gridView.onItemClick { p0, p1, p, p3 ->
            if (gridList[p].type.equals("link")) {
                WebUrl = null
                getWebLink(gridList[p].channel_id, mContext)
            } else if (gridList[p].type.equals("goods")) {
                var intent = Intent(mContext!!, PromotionActivity::class.java)
                intent.putExtra("id", gridList[p].channel_id)
                intent.putExtra("idName","channel_id")
                BaseActivity.goStartActivity(mContext, intent)
            } else {
                ToastUtil.showToast(mContext!!, "不存在的类型:" + gridList[p].type)
            }
        }
    }

    private fun setBanner(item: HomeEntity, helper: BaseViewHolder?) {
        val main_banner = helper!!.getView<AutoLinearLayout>(R.id.auto_1).main_banner
        if (b_i == 1) {
            imgUrls?.clear()
            imgs?.clear()
            main_banner.visibility = View.VISIBLE
            main_banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR) //显示数字指示器
            //设置指示器位置（当banner模式中有指示器时）
            main_banner.setIndicatorGravity(BannerConfig.LEFT)//指示器居右
            //设置图片加载器
            main_banner.setImageLoader(GlideImageLoader(1))
            //设置动画效果
            main_banner.setBannerAnimation(Transformer.Tablet)
            //设置轮播图片间隔时间（不设置默认为2000）
            main_banner.setDelayTime(4500)
            //设置是否自动轮播（不设置则默认自动）
            main_banner.isAutoPlay(true)
            //设置轮播要显示的标题和图片对应（如果不传默认不显示标题）
            //meBanner.setBannerTitles(images);
            b_i = 2
        }
        for (index in 0 until item.banners?.size!!){
            if (!imgs!!.contains(item.banners!![index])) {
                imgs!!.add(item.banners!![index])
            }
        }
        for (index in 0 until imgs!!.size) {
            imgUrls?.add(imgs!![index].image_url)
        }
        //设置图片集合
        main_banner.setImages(imgUrls)
        //点击事件请放到start()前
        main_banner.setOnBannerListener(OnBannerListener {
            if (imgs!![it].type.equals("goods")) {
                var intent = Intent(mContext, PromotionActivity::class.java)
                intent.putExtra("id", imgs!![it].banner_id.toLong())
                intent.putExtra("idName","banner_id")
                BaseActivity.goStartActivity(mContext, intent)
            } else {
                ToastUtil.showToast(this.mContext!!, "这个商品类型异常,快去看日志...")
                Log.i("shopping","异常地址：")
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