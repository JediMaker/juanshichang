package com.example.juanshichang.base

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.example.juanshichang.R
import com.example.juanshichang.utils.StatusBarUtil
import com.qmuiteam.qmui.util.QMUIStatusBarHelper

abstract class BaseFragment : Fragment(){
    var mContext: BaseActivity? = null
    var StatusbarHeight:Int? = 0
    protected var mBaseView: View? = null
    protected abstract fun getLayoutId(): Int
    protected abstract fun initViews(savedInstanceState: Bundle)
    protected abstract fun initData()
    //new Add
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context as BaseActivity
    }
    //参考:https://www.jianshu.com/p/035a7e19fd9b 不好使
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mBaseView == null) {
            mBaseView = inflater.inflate(getLayoutId(), container, false)
            ButterKnife.bind(this,mBaseView!!)
            if(savedInstanceState != null){  //todo 此处添加状态判断 防止走空
                initViews(savedInstanceState)
            }else{
                initViews(savedInstanceState = Bundle())
            }
            initData()
        }
        return mBaseView
    }
    /*public fun initStatusBar(id:Int){ //因为没有使用全屏的模式,所以适配4.4是没戏了
        if(statusBarView == null){
            val identifier:Int = mContext?.resources!!.getIdentifier("statusBarBackground", "id", "android")
            statusBarView = mContext?.window!!.findViewById(identifier)
        }
        if(statusBarView != null){
//            statusBarView.setBackgroundResource("你的渐变drawable资源id")
            statusBarView?.setBackgroundColor(R.color.white)
        }
    }*/
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