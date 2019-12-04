package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_settle_acc.*

/**
 * @作者: yzq
 * @创建日期: 2019/12/4 17:59
 * @文件作用: 结算台
 */
class SettleAccActivity : BaseActivity(),View.OnClickListener{
    override fun getContentView(): Int {
        return R.layout.activity_settle_acc
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@SettleAccActivity, R.color.white)
    }

    override fun initData() {
        setARet.setOnClickListener(this) //返回

    }

    override fun onClick(v: View?) {

    }
}
