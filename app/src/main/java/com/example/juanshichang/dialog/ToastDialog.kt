package com.example.juanshichang.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity

class ToastDialog : DialogFragment {
    private var text: String? = null
    private var type:Int? = null
    constructor(type: Int, text: String) {
        this.text = text
        this.type = type
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val toastRoot = inflater.inflate(R.layout.common_toast, null)
        val mTextTV = toastRoot.findViewById(R.id.mTextTV) as TextView
        mTextTV.text = text
        val mImageView = toastRoot.findViewById(R.id.mImageView) as ImageView
        when (type) {
            BaseActivity.TOAST_FAILURE -> mImageView.setImageResource(R.drawable.w_icon_toast_failure)
            BaseActivity.TOAST_SUCCESS -> mImageView.setImageResource(R.drawable.w_icon_toast_success)
            BaseActivity.TOAST_WARN -> mImageView.setImageResource(R.drawable.w_icon_toast_warn)
            else -> {
            }
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(false)
        return toastRoot
    }
}