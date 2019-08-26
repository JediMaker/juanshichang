package com.example.juanshichang.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

/**
 * author:翊-yzq
 * type: ShareUtil.java
 * details:  这是一个系统级原生分享工具类
 * create-date:2019/8/26 12:03
 */
public class ShareUtil {
    private static final String EMAIL_ADDRESS = "1942413777@qq.com";

    public static void shareText(Context context, String text, String title)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, title));
    }
    public static void shareText(Context context,String flag,String text, String title)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, flag);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, title));
    }
    public static void shareImage(Context context, Uri uri, String title)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, title));
        //多图分享
    }

    public static void sendEmail(Context context, String title)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + EMAIL_ADDRESS));
        context.startActivity(Intent.createChooser(intent, title));
    }

    public static void sendMoreImage(Context context, ArrayList<Uri> imageUris, String title)
    {
        Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        mulIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        mulIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(mulIntent, "多图文件分享"));
    }
    //筛选c
    //以及 Android 打开各种文件 https://www.cnblogs.com/HopeGi/archive/2016/05/06/5467248.html
    //建议参考... 微信筛选 https://www.jianshu.com/p/88f166dd43b7
    // 筛选 参考 下面不完整方法地址：https://www.cnblogs.com/HopeGi/archive/2016/05/06/5467248.html
    public void screenApp(Activity context,Intent intent){
//        List<ResolveInfo> resolveInfo=context.getPackageManager().queryIntentActivities(intent, 0);
//
//        String label;
//
//        Drawable icon;
//
//        ResolveInfo info;
//
//        HashMap<String,Object> item;
//
//        datasource.clear();
//
//        for(int i=0;i<resolveInfo.size();i++)
//
//        {
//
//            item=new HashMap<String,Object>();
//
//            info=resolveInfo.get(i);
//
//            label=info.loadLabel(context.getPackageManager()).toString();
//
//            icon= info.loadIcon(context.getPackageManager());
//
//            if(this.appNameMapping.containsKey(info.activityInfo.applicationInfo.packageName))
//
//                item.put("label", this.appNameMapping.get( info.activityInfo.applicationInfo.packageName)+"——"+label);
//
//            else
//
//                item.put("label", label);
//
//            item.put("icon", icon );
//
//            item.put("resolve", resolveInfo.get(i));
//
//            item.put("intent", intent);
//
//            datasource.add(item);
//
//        }
//        //  two
//        List<ApplicationInfo> listAppcations = context.getPackageManager()
//
//                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
//
//        for(ApplicationInfo info :listAppcations)
//
//        {
//
//            appNameMapping.put(info.packageName, info.loadLabel(getPackageManager()));
//
//        }
//        // three
//        ResolveInfo resolve=(ResolveInfo) datasource.get(index).get("resolve");
//
//        ActivityInfo ai= resolve.activityInfo;
//
//        Intent intent=new Intent((Intent) datasource.get(index).get("intent"));
//
//        intent.setComponent( new ComponentName(ai.applicationInfo.packageName,ai.name) );
//
//        startActivity(intent);
    }
}
