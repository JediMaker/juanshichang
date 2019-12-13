package com.example.juanshichang.utils.glide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.juanshichang.MyApp;
import com.example.juanshichang.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

import java.security.MessageDigest;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;

/**
 * Created by Administrator on 2019/5/27/027.
 * <p>
 * 加载图片
 * 参考文档：https://segmentfault.com/a/1190000011423889
 */

public class GlideUtil {

    /***
     * makemoney
     * 加载方图
     * @param mContext
     * @param url
     * @param mImageView
     */
    public static void loadImage(Context mContext, String url, ImageView mImageView) {
        try {
            RequestOptions options = new RequestOptions();
            options.transform(new StaggeredBitmapTransform(MyApp.app))
//                    .placeholder(R.drawable.c_placeholderlong)
                    .error(R.drawable.c_error)
//                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .placeholder(R.drawable.c_error)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .dontAnimate(); //
            Glide.with(mContext).load(url)
                    .apply(options)
                    .into(mImageView);
        } catch (Exception e) {

        }
    }

    public static void loadHeadImage(Context mContext, String url, ImageView mImageView) {
        try {
            RequestOptions options = new RequestOptions();
            options.transform(new StaggeredBitmapTransform(MyApp.app))
//                    .placeholder(R.drawable.c_placeholderlong)
                    .error(R.drawable.head_img_def)
//                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .placeholder(R.drawable.c_error)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .dontAnimate(); //ALL
            Glide.with(mContext).load(url)
                    .apply(options)
                    .into(mImageView);
        } catch (Exception e) {

        }
    }

    public static void loadImage(Context mContext, String url, ImageView mImageView, int type) {
        try {
            RequestOptions options = new RequestOptions();
            options.transform(new StaggeredBitmapTransform(MyApp.app))
//                    .placeholder(R.drawable.c_placeholderlong)
                    .error(R.drawable.c_error)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE); //ALL
            if (type != 0) {
                options.format(DecodeFormat.PREFER_ARGB_8888);
            }
            if (type == 2) {
                int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
                Glide.with(mContext).load(url)
                        .override(screenWidth, SIZE_ORIGINAL)
                        .apply(options)
                        .centerCrop()
                        .into(mImageView);

            } else {
                Glide.with(mContext).load(url)
                        .apply(options)
                        .into(mImageView);
            }

        } catch (Exception e) {

        }
    }

    /**
     * 用来加载购物车图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void loadShopImg(Context context, String url, ImageView imageView, Drawable draw) { //加载购物车默认使用888等
        try { //glide设置了内存缓存skipMemoryCache(true)导致的 刷新闪烁
            //https://blog.csdn.net/wbw522/article/details/71195249
            if(TextUtils.isEmpty(url+"")){
                imageView.setImageResource(R.drawable.c_error);
            }else { //https://blog.csdn.net/u011814346/article/details/89521542  oom处理
                RequestOptions options = new RequestOptions();
                options.transform(new StaggeredBitmapTransform(MyApp.app))
                        .placeholder(draw)
                        .error(R.drawable.c_error)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .skipMemoryCache(false)
                        .dontAnimate();
            /*Glide.with(context).load(url)
                    .apply(options)
                    .into(imageView);*/
                Glide.with(context).load(url).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                    }
                });
            }
        } catch (Exception e) {

        }
    }

    public static void loadImage(Context mContext, int id, ImageView mImageView, int type) {
        try {
            RequestOptions options = new RequestOptions();
            options.transform(new StaggeredBitmapTransform(MyApp.app))
//                    .placeholder(R.drawable.c_placeholderlong)
                    .error(R.drawable.c_error)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .dontAnimate();  //ALL
            if (type != 0) {
                options.format(DecodeFormat.PREFER_ARGB_8888);
            }
            if (type == 2) {
                int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
                Glide.with(mContext).load(id)
                        .override(screenWidth, SIZE_ORIGINAL)
                        .apply(options)
                        .centerCrop()
                        .into(mImageView);

            } else {
                Glide.with(mContext).load(id)
                        .apply(options)
                        .into(mImageView);
            }

        } catch (Exception e) {

        }
    }

    /**
     * 加载长图
     *
     * @param mContext
     * @param url
     * @param mImageView
     */
    public static void loadImageLong(Context mContext, String url, ImageView mImageView) {
        try {
            RequestOptions options = new RequestOptions();
            options.transform(new StaggeredBitmapTransform(MyApp.app))
//                    .placeholder(R.drawable.c_placeholderlong)
                    .error(R.drawable.c_errorlong)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(mContext).load(url)
                    .apply(options)
                    .into(mImageView);
        } catch (Exception e) {
        }
    }

    /**
     * 圆形加载
     *
     * @param mContext
     * @param imageview
     */
    @SuppressLint("CheckResult")
    public static void LoadCircleImage(Context mContext, String url,
                                       ImageView imageview) {
        RequestOptions options = new RequestOptions();
        options.centerCrop().placeholder(R.drawable.c_error)
                .error(R.drawable.ic_no_pic)
                .transform(new GlideCircleTransform(mContext, 2, mContext.getResources().getColor(R.color.white)))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(mContext).load(url).apply(options).into(imageview);

    }

    static class GlideCircleTransform extends BitmapTransformation {

        private Paint mBorderPaint;
        private float mBorderWidth;

        public GlideCircleTransform(Context context) {
            super();
        }

        public GlideCircleTransform(Context context, int borderWidth, int borderColor) {
            super();
            mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;

            mBorderPaint = new Paint();
            mBorderPaint.setDither(true);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }


        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            if (mBorderPaint != null) {
                float borderRadius = r - mBorderWidth / 2;
                canvas.drawCircle(r, r, borderRadius, mBorderPaint);
            }
            return result;
        }


        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }

    /**
     * 半圆角图片   存在图片加载不完整问题 建议 操作控件 ...
     */
    public static void loadHalfRoundImage(final Context context, int roundRadius, String url, final ImageView imageView) {//int resId,
        //默认请求选项【不太习惯，还是每个请求重复使用吧】
//        builder.setDefaultRequestOptions(
//                new RequestOptions()
//                        //设置等待时的图片
//                        .placeholder(R.drawable.img_loading)
//                        //设置加载失败后的图片显示
//                        .error(R.drawable.img_error)
//                        .centerCrop()
//                        //缓存策略,跳过内存缓存【此处应该设置为false，否则列表刷新时会闪一下】
//                        .skipMemoryCache(false)
//                        //缓存策略,貌似只有这一个设置
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        //设置图片加载的优先级
//                        .priority(Priority.HIGH));
        //DiskCacheStrategy.RESOURCE 只缓存原始文件
        //DiskCacheStrategy.ALL 缓存所有size的图片和源文件
        //DiskCacheStrategy.RESULT 缓存最后的结果文件
        //DiskCacheStrategy.NONE 撒都不缓存
        try {
            RequestOptions options = new RequestOptions()
//                    .transform(new StaggeredBitmapTransform(context))
                    .fitCenter()
                    .optionalTransform(new GlideRoundedCornersTransform(QMUIDisplayHelper.dp2px(context, roundRadius), -5, GlideRoundedCornersTransform.CornerType.TOP))
//                    .skipMemoryCache(true)
                    .error(R.drawable.c_error)//加载失败显示图片
                    .placeholder(R.drawable.c_error)//预加载图片
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)//设置缓存策略 缓存最后的结果文件
                    .priority(Priority.HIGH); //优先级
            Glide.with(context).asBitmap().load(url).apply(options).into(imageView);
//            options.bitmapTransform(new CircleCrop()); //圆形
//            options.bitmapTransform(new RoundedCorners(cornerRadius)); //圆角
        } catch (Exception e) {

        }
    }

    /**
     * 圆角图
     */
    public static void loadRoundImage(final Context context, int roundRadius, String url, final ImageView imageView) {
        try {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.c_error) //预加载
                    .fitCenter()
                    .optionalTransform(new GlideRoundedCornersTransform(QMUIDisplayHelper.dp2px(context, roundRadius), GlideRoundedCornersTransform.CornerType.ALL))
//                    .skipMemoryCache(true) //不从内存加载
                    .error(R.drawable.c_error)      //失败
                    .priority(Priority.HIGH) //优先级
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);//设置缓存策略 缓存最后的结果文件
//                    .transform(new GlideRoundTransform(roundRadius));
//            options.bitmapTransform(roundedCorners);
            Glide.with(context).asBitmap().load(url).apply(options).into(imageView);
        } catch (Exception e) {

        }
    }

    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
    }
}
