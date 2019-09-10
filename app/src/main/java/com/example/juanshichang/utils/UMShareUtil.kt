package com.example.juanshichang.utils

import android.app.Activity
import android.graphics.Bitmap
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMVideo
import com.umeng.socialize.media.UMWeb
import com.umeng.socialize.shareboard.ShareBoardConfig

class UMShareUtil {
    companion object{
        private var umShare:UMShareUtil? = null
        fun getShareSingle():UMShareUtil{
            if(umShare==null){
                synchronized(UMShareUtil::class){
                    if(umShare==null){
                        umShare = UMShareUtil()
                    }
                }
            }
            return umShare!!
        }
    }

    /**
     * 打开UI分享链接
     * @param activity  当前所在Activity
     * @param url    要分享的url地址  必须http开头
     * @param imageUri   缩略图文件 可以是 资源地址 或 bitmap图 或者 链接
     * @param tit 分享的标题
     * @param description 分享描述
     */
    fun openUi2Web(activity: Activity,url:String,imageUri:Any,tit:String,description:String,umShareListener: UMShareListener){
        var image:UMImage = UMImage(activity,"")
        if (imageUri is Int){
            image = UMImage(activity,imageUri)
        }
        if (imageUri is String){
            image = UMImage(activity,imageUri)
        }
        if(imageUri is Bitmap){
            image = UMImage(activity,imageUri)
        }
        image.compressStyle = UMImage.CompressStyle.SCALE//大小压缩，默认为大小压缩，适合普通很大的图
//        image.compressStyle = UMImage.CompressStyle.QUALITY//质量压缩，适合长图的分享
//        //压缩格式设置
//        image.compressFormat = Bitmap.CompressFormat.PNG//用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
        val web = UMWeb(url)
        web.title = tit //标题
        web.description = description //描述
        web.setThumb(image) //设置缩略图
        //微信 QQ 微博
        ShareAction(activity).withText("劵市场").withMedia(web).setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.QQ,SHARE_MEDIA.SINA)
            .setCallback(umShareListener).open()
    }

    fun openUi2Video(activity: Activity,videourl:String,imageUri:Any,tit:String,description:String,umShareListener: UMShareListener){
        var image:UMImage = UMImage(activity,"")
        if (imageUri is Int){
            image = UMImage(activity,imageUri as Int)
        }
        if (imageUri is String){
            image = UMImage(activity,imageUri as String)
        }
        if(imageUri is Bitmap){
            image = UMImage(activity,imageUri as Bitmap)
        }
        image.compressStyle = UMImage.CompressStyle.SCALE//大小压缩，默认为大小压缩，适合普通很大的图
        val video =  UMVideo(videourl)
        video.title = tit //标题
        video.setThumb(image) //缩略图
        video.description = description //描述
        //微信 QQ 微博
        ShareAction(activity).withText("分享视频").withMedia(video).setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.QQ,SHARE_MEDIA.SINA)
            .setCallback(umShareListener).open()
//            .setShareboardclickCallback(shareBoardlistener)//面板点击监听器
    }

    // 自定义分享面板
    fun getConfig():ShareBoardConfig{
        val config = ShareBoardConfig() //新建ShareBoardConfig
        config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER) //设置位置
        /**
         * ShareBoardConfig.BG_SHAPE_NONE(无背景)
            ShareBoardConfig.BG_SHAPE_CIRCULAR(圆形)
            ShareBoardConfig.BG_SHAPE_ROUNDED_SQUARE(圆角方形)
         */
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR)
        config.setCancelButtonVisibility(true)
//        config.setTitleText("")
//        config.setTitleTextColor()
//        config.setTitleVisibility(boolean visibility)
//        config.setMenuItemTextColor(int color)
//        setMenuItemBackgroundColor(int normalColor)
//        setMenuItemBackgroundColor(int normalColor, int pressedColor) //normalColor背景色  pressedColor按下时色值
        return config
    }
}