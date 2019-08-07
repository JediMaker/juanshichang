package com.example.juanshichang.utils.glide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.juanshichang.MyApp;
import com.example.juanshichang.R;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2018/3/27/027.
 *
 * 加载图片
 */

public class GlideUtil {

    /***
     * makemoney
     * 加载方图
     * @param mContext
     * @param url
     * @param mImageView
     */
    public static void loadImage(Context mContext, String url, ImageView mImageView){
        try {
            RequestOptions options = new RequestOptions();
            options.transform(new StaggeredBitmapTransform(MyApp.app))
//                    .placeholder(R.drawable.c_placeholderlong)
                    .error(R.drawable.c_error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(mContext).load(url)
                    .apply(options)
                    .into(mImageView);
        } catch (Exception e) {

        }
    }

    /**
     * 加载长图
     * @param mContext
     * @param url
     * @param mImageView
     */
    public static void loadImageLong(Context mContext, String url, ImageView mImageView){
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
                .transform(new GlideCircleTransform(mContext,2,mContext.getResources().getColor(R.color.white)))
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
     * 圆角图片
     */
    public static void loadRoundImage(){

    }
}
