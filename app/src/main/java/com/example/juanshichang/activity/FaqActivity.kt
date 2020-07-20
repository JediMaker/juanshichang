package com.example.juanshichang.activity

import android.content.Intent
import android.view.View
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.http.PRIVACY_AGREEMENT_URL
import com.example.juanshichang.http.SERVICES_AGREEMENT_URL
import com.example.juanshichang.utils.SoftHideKeyBoardUtil
import com.example.juanshichang.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_faq.*

/**

 * author:xyb
 * type: .kt
 * details: 使用帮助 展示协议
 * create-date:2020/7/15.
 */
class FaqActivity : BaseActivity(), View.OnClickListener {
    override fun getContentView(): Int {
        return R.layout.activity_faq
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@FaqActivity, R.color.colorPrimary)
        SoftHideKeyBoardUtil.assistActivity(this@FaqActivity)
    }

    override fun initData() {
        serviceAgreement.setOnClickListener(this)//用户服务协议
        protectionAgreement.setOnClickListener(this)//隐私保护协议
        faqRet.setOnClickListener(this)//返回
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.faqRet -> {//退出
                if (!isFinishing)
                    finish()
            }
            R.id.serviceAgreement -> {//跳转用户服务协议链接页面
                val intent = Intent(this@FaqActivity, WebActivity::class.java)
                intent.putExtra("title","用户协议")
                intent.putExtra(
                    "mobile_short_url",
                    SERVICES_AGREEMENT_URL
                )
                BaseActivity.goStartActivity(this@FaqActivity, intent)
            }
            R.id.protectionAgreement -> {//跳转隐私保护协议链接页面
                val intent = Intent(this@FaqActivity, WebActivity::class.java)
                intent.putExtra("title","隐私政策")
                intent.putExtra(
                    "mobile_short_url",
                    PRIVACY_AGREEMENT_URL
                )
                BaseActivity.goStartActivity(this@FaqActivity, intent)

            }

        }
    }
}