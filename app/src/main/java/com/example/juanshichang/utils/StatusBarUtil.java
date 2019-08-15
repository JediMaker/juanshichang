package com.example.juanshichang.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import static com.qmuiteam.qmui.util.QMUIDisplayHelper.getStatusBarHeight;

/**
 * @作者: yzq
 * @创建日期: 2019/8/2 12:21
 * @文件作用: 参考 https://www.jianshu.com/p/29bf603359d6  状态栏
 */
public class StatusBarUtil {
    /**
     * 通过设置全屏，设置状态栏透明
     * @param activity
     */
    public static void fullScreen(Activity activity) {
        //4.4以上才能设置状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.0开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //底部导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams
                        .FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
                //底部导航栏
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    /**
     * 添加状态栏占位视图
     *
     * @param activity
     */
    public static  void addStatusViewWithColor(Activity activity, int colorId) {
        //4.4以上才能设置状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //设置 paddingTop 状态栏的高度
            ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView()
                    .findViewById(android.R.id.content);
            rootView.setPadding(0, getStatusBarHeight(activity), 0, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.0以上直接设置状态栏颜色
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(colorId));

            } else {
                //增加占位状态栏
                ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
                decorView.clearFocus();
                View statusBarView = new View(activity);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT,
                        getStatusBarHeight(activity));
                statusBarView.setBackgroundColor(colorId);
                decorView.addView(statusBarView, lp);
            }
        }
    }

}
