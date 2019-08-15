package com.example.juanshichang.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity

class RegisterDialog(title:String,content:String,sureBtnText:String,cancleBtnText:String,callback: BaseActivity.DialogCallback,isCanceled:Boolean) :DialogFragment(),View.OnClickListener{
    private var title: String? = null
    private var content: String? = null
    private var mSureBtnText: String? = null
    private var mCancleBtnText: String? = null
    private var mCallback: BaseActivity.DialogCallback? = null
    private var isCanceled:Boolean = false
    init {
        this.title = title  //标题
        this.content = content //内容
        this.mSureBtnText = sureBtnText //确定 按钮
        this.mCancleBtnText = cancleBtnText//取消按钮
        this.mCallback = callback
        this.isCanceled = isCanceled //是否点击空白区域可取消
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.regist_base_dialog, null)
        val titleDialog = view.findViewById(R.id.titleDialog) as TextView
        val contentDialog = view.findViewById(R.id.contentDialog) as TextView
        val sureReg = view.findViewById(R.id.sureReg) as TextView
        val cancelReg = view.findViewById(R.id.cancelReg) as TextView
        val centerLinear = view.findViewById(R.id.centerLinear) as View
        if(mCancleBtnText==null || TextUtils.isEmpty(mCancleBtnText)){
            cancelReg.visibility = View.GONE
            centerLinear.visibility = View.GONE
        }else{
            cancelReg.text =  mCancleBtnText
            cancelReg.setOnClickListener(this)
        }
        titleDialog.text = title
        contentDialog.text = content
        sureReg.text =  mSureBtnText
        sureReg.setOnClickListener(this)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(isCanceled) //默认 不可点击取消
        return view
    }
    override fun onClick(v: View?) {
        dismiss()
        when (v?.getId()) {
            R.id.cancelReg -> if (mCallback != null) {
                mCallback?.cancle()
            }
            R.id.sureReg -> if (mCallback != null) {
                mCallback?.sure()
            }
        }
    }
}