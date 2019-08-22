package com.example.juanshichang.utils.glide;

import com.example.juanshichang.MyApp;

/**
 * Created by Administrator on 2018/3/27/027.
 * 图片加载工具
 */

public class DimenUtil {

    public static float dp2px(float dp) {
        final float scale = MyApp.app.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }


    public static float sp2px(float sp) {
        final float scale = MyApp.app.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }



    public static int getScreenSize() {
        return MyApp.app.getResources().getDisplayMetrics().widthPixels;
    }
}
