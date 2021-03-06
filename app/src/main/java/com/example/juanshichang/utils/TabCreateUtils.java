package com.example.juanshichang.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.juanshichang.MainActivity;
import com.example.juanshichang.R;
import com.example.juanshichang.bean.TopUpBarBean;
import com.example.juanshichang.widget.SelectBigPagerTitleView;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * @作者: yzq
 * @创建日期: 2019/8/23 16:08
 * @文件作用: 创建第三方菜单指示器
 */
public class TabCreateUtils {
    /**
     * 类型：橘色指示器
     * 字：选中橘色，未选中黑色，加粗
     * 指示器：指示器长度和文字长度相同，橘色
     */
    public static void setOrangeTab(Context context, MagicIndicator magicIndicator, ViewPager viewPager, List<String> tabNames) {
        CommonNavigator commonNavigator = new CommonNavigator(context);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return tabNames == null ? 0 : tabNames.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.colorAccent));
                colorTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.colorPrimary));
                colorTransitionPagerTitleView.setTextSize(16);
                colorTransitionPagerTitleView.setText(tabNames.get(index));
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setColors(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                indicator.setRoundRadius(3);
                return indicator;
            }
        });
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    /**
     * 类型：不关联ViewPager
     * 字：选中橘色，未选中黑色，加粗
     * 指示器：指示器长度和文字长度相同，橘色
     * 这个被自定义过... 满足 首页面需求
     */
    public interface onTitleClickListener {
        void onTitleClick(int index);
    }

    public static CommonNavigatorAdapter setOrangeTab(Context context, MagicIndicator magicIndicator, List<String> tabNames, onTitleClickListener listener) {
        FragmentContainerHelper mFragmentContainerHelper = new FragmentContainerHelper();
        CommonNavigator commonNavigator = new CommonNavigator(context);
        CommonNavigatorAdapter cA = new CommonNavigatorAdapter() {
            private int type = 0;

            @Override
            public int getCount() {
                return tabNames == null ? 0 : tabNames.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                //自定义
                SelectBigPagerTitleView colorTransitionPagerTitleView = new SelectBigPagerTitleView(context);
//                colorTransitionPagerTitleView.setTextSize(16); //不再设置
                setTextColor(colorTransitionPagerTitleView);
                colorTransitionPagerTitleView.setText(tabNames.get(index));
//                colorTransitionPagerTitleView.setPadding();
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (index != 0 && type == 0) { //点击了其它 条目  之前为第一条 刷新
                            type = index;
                            notifyDataSetChanged();
                        } else if (index == 0 && type != 0) {//点击了第一条  之前为其它 条目 刷新
                            type = 0;
                            notifyDataSetChanged();
                        } else {  //此判断 目的 避免性能过度消耗
                            type = index;
                        }
                        mFragmentContainerHelper.handlePageSelected(index);
                        if (listener != null) listener.onTitleClick(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return super.getTitleWeight(context, index);
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return getTypeindicator(context);
            }

            //设置指示器
            private LinePagerIndicator getTypeindicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);//MODE_WRAP_CONTENT
                indicator.setLineWidth(32);
//                indicator.setColors(ContextCompat.getColor(context, R.color.white));
                indicator.setYOffset(16f);
                indicator.setRoundRadius(3);
                indicator.setColors(ContextCompat.getColor(context, R.color.white));
               /* if (type == 0) { //默认
                    indicator.setColors(ContextCompat.getColor(context, R.color.white));
                } else {
                    indicator.setColors(ContextCompat.getColor(context, R.color.indicatorRed));
                }*/
                return indicator;
            }

            //设置字体 颜色...
            private void setTextColor(SelectBigPagerTitleView colorTransitionPagerTitleView) {
                colorTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.qmui_config_color_50_white));
                colorTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.white));
             /*   if (type == 0) {
                    colorTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.qmui_config_color_50_white));
                    colorTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    colorTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.qmui_config_color_50_pure_black));
                    colorTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.main_text));
                }*/
            }
        };
        commonNavigator.setAdapter(cA);
//        commonNavigator.setAdjustMode(true); //过多 会引起问题
        magicIndicator.setNavigator(commonNavigator);
        mFragmentContainerHelper.attachMagicIndicator(magicIndicator);
        return cA;
    }

    /**
     *  这是一个单一的 不可变色的Tab
     * @param context  上下文
     * @param magicIndicator 传入控件实例
     * @param tabNames  名称集合
     * @param listener  监听
     * @return
     */
    public static CommonNavigatorAdapter setOrangeTabT(Context context, MagicIndicator magicIndicator, List<String> tabNames, onTitleClickListener listener) {
        FragmentContainerHelper mFragmentContainerHelper = new FragmentContainerHelper();
        CommonNavigator commonNavigator = new CommonNavigator(context);
        CommonNavigatorAdapter cA = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return tabNames == null ? 0 : tabNames.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                //自定义
                SelectBigPagerTitleView colorTransitionPagerTitleView = new SelectBigPagerTitleView(context);
//                colorTransitionPagerTitleView.setTextSize(16); //不再设置
                setTextColor(colorTransitionPagerTitleView);
                colorTransitionPagerTitleView.setText(tabNames.get(index));
//                colorTransitionPagerTitleView.setPadding();
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mFragmentContainerHelper.handlePageSelected(index);
                        if (listener != null) listener.onTitleClick(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return super.getTitleWeight(context, index);
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return getTypeindicator(context);
            }

            //设置指示器
            private LinePagerIndicator getTypeindicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);//MODE_WRAP_CONTENT
                indicator.setLineWidth(50);
//                indicator.setColors(ContextCompat.getColor(context, R.color.white));
                indicator.setRoundRadius(3);
                indicator.setColors(ContextCompat.getColor(context, R.color.indicatorRed));
                return indicator;
            }

            //设置字体 颜色...
            private void setTextColor(SelectBigPagerTitleView colorTransitionPagerTitleView) {
                colorTransitionPagerTitleView.setTextSize(R.dimen.sp_txt_nomal);
                colorTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.qmui_config_color_50_pure_black));
                colorTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.main_text));
            }
        };
        commonNavigator.setAdapter(cA);
        if(tabNames.size()<4){ //todo 设置... 此处有坑
            commonNavigator.setAdjustMode(true); //过多 会引起问题
        }
        magicIndicator.setNavigator(commonNavigator);
        mFragmentContainerHelper.attachMagicIndicator(magicIndicator);
        return cA;
    }

    /**
     * 这是一个带图的
     * 指示器
     *
     */
    public static CommonNavigatorAdapter setTopUpTab(Context context, MagicIndicator magicIndicator, List<TopUpBarBean> tabNames, onTitleClickListener listener){
        FragmentContainerHelper mFragmentContainerHelper = new FragmentContainerHelper();
        CommonNavigator commonNavigator = new CommonNavigator(context);
        CommonNavigatorAdapter cT = new CommonNavigatorAdapter(){

            @Override
            public int getCount() {
                return tabNames == null?0:tabNames.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);
                commonPagerTitleView.setContentView(R.layout.topup_item);
                // 初始化
                final ImageView titleImg = (ImageView) commonPagerTitleView.findViewById(R.id.itemImg);
                titleImg.setImageResource(tabNames.get(index).getTopImg());
                final TextView titleText = (TextView) commonPagerTitleView.findViewById(R.id.itemText);
                titleText.setText(tabNames.get(index).getBotTit());
                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

                    @Override
                    public void onSelected(int index, int totalCount) {
//                        TextPaint tp = titleText.getPaint();
//                        tp.setFakeBoldText(true); //设置文字加粗
                    }

                    @Override
                    public void onDeselected(int index, int totalCount) {
//                        TextPaint tp = titleText.getPaint();
//                        tp.setFakeBoldText(false); // 取消文字加粗
                    }

                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                        titleImg.setScaleX(1.1f + (0.8f - 1.3f) * leavePercent);//1.3 --- 1.1
                        titleImg.setScaleY(1.1f + (0.8f - 1.3f) * leavePercent);
                    }

                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                        titleImg.setScaleX(0.6f + (1.3f - 0.8f) * enterPercent); // 0.8 --- 0.6
                        titleImg.setScaleY(0.6f + (1.3f - 0.8f) * enterPercent);
                    }
                });
                commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        mPager.setCurrentItem(index);
                        mFragmentContainerHelper.handlePageSelected(index);
                        if (listener != null) listener.onTitleClick(index);
                    }
                });
                return commonPagerTitleView;
            }
            @Override
            public IPagerIndicator getIndicator(Context context) {
                return getTypeindicator(context);
            }
            //设置指示器
            private LinePagerIndicator getTypeindicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);//MODE_WRAP_CONTENT
                indicator.setLineWidth(120);
//                indicator.setColors(ContextCompat.getColor(context, R.color.white));
                indicator.setRoundRadius(3);
                indicator.setColors(ContextCompat.getColor(context, R.color.indicatorRed));
                return indicator;
            }
        };
        commonNavigator.setAdapter(cT);
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        mFragmentContainerHelper.attachMagicIndicator(magicIndicator);
        return cT;
    }
}
