package com.example.juanshichang.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.example.juanshichang.R;
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
     */
    public interface onTitleClickListener{
        void onTitleClick(int index);
    }
    public static void setOrangeTab(Context context,MagicIndicator magicIndicator, List<String> tabNames ,onTitleClickListener listener) {
        FragmentContainerHelper mFragmentContainerHelper = new FragmentContainerHelper();
        CommonNavigator commonNavigator = new CommonNavigator(context);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return tabNames == null ? 0 : tabNames.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                //自定义
                SelectBigPagerTitleView colorTransitionPagerTitleView = new SelectBigPagerTitleView (context);
                colorTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.qmui_config_color_50_white));
                colorTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.white));
//                colorTransitionPagerTitleView.setTextSize(16); //不再设置
                colorTransitionPagerTitleView.setText(tabNames.get(index));
                Log.e("titles",tabNames.get(index));
//                colorTransitionPagerTitleView.setPadding();
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(index!=0){
                            colorTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.qmui_config_color_50_white));
                            colorTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.white));
                        }
                        mFragmentContainerHelper.handlePageSelected(index);
                        if (listener!=null)listener.onTitleClick(index);
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
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);//MODE_WRAP_CONTENT
                indicator.setLineWidth(32);
                indicator.setColors(ContextCompat.getColor(context, R.color.white));
                indicator.setRoundRadius(3);
                return indicator;
            }
        });
//        commonNavigator.setAdjustMode(true); //过多 会引起问题
        magicIndicator.setNavigator(commonNavigator);
        mFragmentContainerHelper.attachMagicIndicator(magicIndicator);
    }
}
