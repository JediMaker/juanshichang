package com.example.juanshichang.activity


import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_real_name.*

/**
 * @作者：yzq
 * @创建时间：2019/11/7 18:58
 * @文件作用: 实名认证
 */
class RealNameActivity : BaseActivity(), View.OnClickListener{
    override fun getContentView(): Int {
        return R.layout.activity_real_name
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this, R.color.white)
    }

    override fun initData() {
        realRet.setOnClickListener(this)
        goReal.setOnClickListener(this)
        cancelReal.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            goReal ->{ //立即认证

            }
            realRet,
            cancelReal ->{// 取消/返回
                finish()
            }
        }
    }
}
