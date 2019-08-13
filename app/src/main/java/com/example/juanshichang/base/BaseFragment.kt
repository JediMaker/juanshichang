package com.example.juanshichang.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.qmuiteam.qmui.util.QMUIStatusBarHelper

abstract class BaseFragment : Fragment(){
    var mContext: BaseActivity? = null
    protected var mBaseView: View? = null
    protected abstract fun getLayoutId(): Int
    protected abstract fun initViews(savedInstanceState: Bundle)
    protected abstract fun initData()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context as BaseActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        QMUIStatusBarHelper.translucent(mContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mBaseView == null) {
            mBaseView = inflater.inflate(getLayoutId(), container, false)
            ButterKnife.bind(this,mBaseView!!)
            if(savedInstanceState != null){  //todo 此处添加状态判断 防止走空
                initViews(savedInstanceState)
            }else{
                initViews(Bundle())
            }
            initData()
        }
        return mBaseView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }
}