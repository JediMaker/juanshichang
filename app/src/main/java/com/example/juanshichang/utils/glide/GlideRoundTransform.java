package com.example.juanshichang.utils.glide;


import android.content.res.Resources;
import android.graphics.*;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * @作者: yzq  有问题的 方法
 * @创建日期: 2019/8/7 20:25
 * @文件作用: 绘制圆角图
 **/
public class GlideRoundTransform extends BitmapTransformation {
    private static float radius = 0f;
    private static int type = 0;

    public GlideRoundTransform() {
        this(4);
    }

    public GlideRoundTransform(int dp) {
        super();
        this.type = 0;
        this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
    }

    /**
     * @param dp
     * @param type 为1 则只绘制 图片上面两个圆角 , 0 绘制四角
     */
    public GlideRoundTransform(int dp, int type) {
        super();
        this.type = type;
        this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        //变换的时候裁切
        Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
        return roundCrop(pool, bitmap);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {

    }


    private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) {
            return null;
        }

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);

        if (type == 1) {
            RectF rectRound = new RectF(0f, 100f, source.getWidth(), source.getHeight());
            canvas.drawRect(rectRound, paint);
        }
        return result;
    }

}

