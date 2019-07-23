package com.example.juanshichang.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.juanshichang.MyApp

import com.example.juanshichang.R
import com.example.juanshichang.activity.SearcheActivity
import com.example.juanshichang.base.BaseFragment
import com.example.juanshichang.utils.ToastUtil
import kotlinx.android.synthetic.main.fragment_one.*

/**
 * @作者: yzq
 * @创建日期: 2019/7/17 16:52
 * @文件作用: 首页
 */
class OneFragment : BaseFragment(),View.OnClickListener{
    /* override fun onCreateView(
         inflater: LayoutInflater, container: ViewGroup?,
         savedInstanceState: Bundle?
     ): View? {
         // Inflate the layout for this fragment
         return inflater.inflate(R.layout.fragment_one, container, false)
     }*/
    override fun getLayoutId(): Int {
        return  R.layout.fragment_one
    }

    override fun initViews(savedInstanceState: Bundle) {
        etsearch.setOnClickListener(this)
    }

    override fun initData() {

    }
    override fun onClick(view: View?) {
        when(view){
            etsearch -> {
                ToastUtil.showToast(MyApp.applicationContext,"搜索ing")
                val intent = Intent(mContext,SearcheActivity::class.java)
                //...
                startActivity(intent)
            }
            else ->{}
        }
    }

}
