package com.example.juanshichang.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class CameraUtil {
    /**
     * @param uri   返回 地址
     * @param REQUE_CODE_CROP  返回码
     * @param context  Activity实例
     */
    private void startPhotoZoom(Uri uri, int REQUE_CODE_CROP, Activity context) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // 去黑边
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        // aspectX aspectY 是宽高的比例，根据自己情况修改
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高像素
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //取消人脸识别功能
        intent.putExtra("noFaceDetection", true);
        //设置返回的uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //设置为不返回数据
        intent.putExtra("return-data", false);
        context.startActivityForResult(intent, REQUE_CODE_CROP);
    }
    private String uriToFilePath(Uri uri,Activity context) {
        //获取图片数据
        String[] proj = {MediaStore.Images.Media.DATA};
        //查询
        Cursor cursor = context.managedQuery(uri, proj, null, null, null);
        //获得用户选择的图片的索引值
        int image_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        //返回图片路径
        return cursor.getString(image_index);
    }

    /**
     * 上网查看资料Android7.0后，以前适用的File Uri需要更改为Content Uri。 Android 框架强制执行了 StrictMode API 政策禁止向你的应用外公开 file:// URI。  如果一项包含文件 file:// URI类型 的 Intent 离开你的应用，应用失败，并出现 FileUriExposedException 异常，如调用系统相机拍照，或裁切照片。 这里我们可以使用 content:// URI类型的Uri，网上有好多的方法都是使用FileProvider类。可以去网上查看，这里我使用的一个方法，把File Uri换成Content Uri
     *  ————————————————
     * 版权声明：本文为CSDN博主「阴小猿」的原创文章，遵循CC 4.0 by-sa版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/qq_33463102/article/details/78132199
     * @param context
     * @param imageFile
     * @return
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
