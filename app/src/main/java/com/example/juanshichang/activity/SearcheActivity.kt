package com.example.juanshichang.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_searche.*
import kotlinx.android.synthetic.main.activity_seek_bar.*

/**
 * @作者: yzq
 * @创建日期: 2019/7/24 14:47
 * @文件作用: 搜索页面
 */
class SearcheActivity : BaseActivity(), View.OnClickListener {

    override fun getContentView(): Int {
        return R.layout.activity_searche
    }

    override fun initView() {
        etsearch.setOnClickListener(this)
        mbackLayout.setOnClickListener(this)
        mSearchBt.setOnClickListener(this)
    }

    override fun initData() {
        setEditText()
    }

    //输入框处理
    private fun setEditText() {
        etsearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) { //在文字改变后的回调

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }

    private fun getEditText(): String {//获取Edit数据
        val text = etsearch.text.toString().trim()
        if (text.length > 0 && !TextUtils.isEmpty(text)) {
            return text
        }
        return ""
    }

    override fun onClick(v: View?) {
        when (v) {
            mbackLayout -> {//返回
                ToastUtil.showToast(this@SearcheActivity, "主人 你又要离开了吗 ┭┮﹏┭┮")
                finish()
            }
            etsearch -> {//搜索框
                ToastUtil.showToast(this@SearcheActivity, "主人 你终于来啦!!!")
            }
            mSearchBt -> {//搜索按钮
                val str = getEditText()
                if(!TextUtils.isEmpty(str)){
                    ToastUtil.showToast(this@SearcheActivity, "开始寻找喽...")
                    var intent = Intent(this@SearcheActivity,ClassTypeActivity::class.java)
                    intent.putExtra("keyword",str)
                    startActivity(intent)
                    finish()
                }else{
                    ToastUtil.showToast(this@SearcheActivity, "主人 你想找什么嘛!!!")
                }

            }
        }
    }
    /* override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_searche)
     }*/

}
