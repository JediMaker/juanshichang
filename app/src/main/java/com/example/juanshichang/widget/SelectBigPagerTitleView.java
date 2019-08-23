package com.example.juanshichang.widget;

import android.content.Context;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
/**
 * @作者: yzq
 * @创建日期: 2019/8/23 16:35
 * @文件作用: 这是一个可以改变 字体大小
 */
public class SelectBigPagerTitleView extends ColorTransitionPagerTitleView {
    public SelectBigPagerTitleView(Context context) {
        super(context);
    }
    @Override
    public void onSelected(int index, int totalCount) {
        setTextSize(18);
    }
    @Override
    public void onDeselected(int index, int totalCount) {
        setTextSize(16);
    }
}