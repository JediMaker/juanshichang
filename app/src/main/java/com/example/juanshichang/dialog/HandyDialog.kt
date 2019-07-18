package com.example.juanshichang.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity

class HandyDialog (type:Int,text:String,sureBtnText:String,cancleBtnText:String,callback:BaseActivity.DialogCallback): DialogFragment(), View.OnClickListener {
    private var type: Int? = null
    private var text: String? = null
    private var mSureBtnText: String? = null
    private var mCancleBtnText: String? = null
    private var mCallback: BaseActivity.DialogCallback? = null
    init {
        this.type = type
        this.text = text
        this.mSureBtnText = sureBtnText
        this.mCancleBtnText = cancleBtnText
        this.mCallback = callback
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.common_handy_dialog, null)
        val mTextTV = view.findViewById(R.id.mTextTV) as TextView
        mTextTV.text = text
        val mImageView = view.findViewById(R.id.mImageView) as ImageView
        when (type) {
            BaseActivity.TOAST_FAILURE -> mImageView.setImageResource(R.drawable.w_icon_toast_failure)
            BaseActivity.TOAST_SUCCESS -> mImageView.setImageResource(R.drawable.w_icon_toast_success)
            BaseActivity.TOAST_WARN -> mImageView.setImageResource(R.drawable.w_icon_toast_warn)
            else -> {
            }
        }
        val mCancleTV = view.findViewById(R.id.mCancleTV) as TextView
        if (!TextUtils.isEmpty(mCancleBtnText)) {
            mCancleTV.text = mCancleBtnText
        }
        mCancleTV.setOnClickListener(this)
        val mSureTV = view.findViewById(R.id.mSureTV) as TextView
        if (!TextUtils.isEmpty(mSureBtnText)) {
            mSureTV.text = mSureBtnText
        }
        mSureTV.setOnClickListener(this)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(false)

        return view
    }
    override fun onClick(v: View?) {
        dismiss()
        when (v?.getId()) {
            R.id.mCancleTV -> if (mCallback != null) {
                mCallback?.cancle()
            }
            R.id.mSureTV -> if (mCallback != null) {
                mCallback?.sure()
            }
            else -> {
            }
        }
    }
}