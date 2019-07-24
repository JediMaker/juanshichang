package com.example.juanshichang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.juanshichang.R
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.utils.ToastUtil

/**
 * @作者: yzq
 * @创建日期: 2019/7/24 16:14
 * @文件作用: 商品分类
 */
class ClassTypeActivity : BaseActivity() {
    var keyWord = ""
    override fun getContentView(): Int {
        return R.layout.activity_class_type
    }

    override fun initView() {
        if(null != getIntent().getStringExtra("keyword")){
            keyWord = getIntent().getStringExtra("keyword")
        }else{
            ToastUtil.showToast(this@ClassTypeActivity, "哎呀  出错啦!!!")
            finish()
        }

    }

    override fun initData() {
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_class_type)
//    }
    private fun cargoList(servicer:String,keyword:String,page:String){

    }
}
