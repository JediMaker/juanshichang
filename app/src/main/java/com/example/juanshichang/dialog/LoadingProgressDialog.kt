package com.example.juanshichang.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import com.example.juanshichang.R

/**
 * @作者: yzq
 * @创建日期: 2019/7/16 17:53
 * @文件作用: 等待弹框 加载圈
 */
open class LoadingProgressDialog: Dialog {
    constructor(context: Context):super(context){
        setContentView(R.layout.dialog_progress)
        imageView = findViewById<View>(R.id.loadingImageView) as ImageView
        animationDrawable = imageView?.getBackground() as AnimationDrawable
        animationDrawable?.start()
    }
    private var imageView: ImageView? = null
    private var animationDrawable: AnimationDrawable? = null
    constructor(context: Context,theme:Int):super(context,theme){
        setContentView(R.layout.dialog_progress)
        imageView = findViewById<View>(R.id.loadingImageView) as ImageView
        animationDrawable = imageView?.background as AnimationDrawable
        animationDrawable?.start()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        animationDrawable?.start()
    }
}