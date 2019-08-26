package com.example.juanshichang.fragment


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView


import com.example.juanshichang.R
import com.example.juanshichang.adapter.ClassifyListAdpater
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseFragment
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.TabOneBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.ToastUtil
import com.google.gson.Gson
import kotlinx.coroutines.Runnable
import org.json.JSONObject
import rx.Subscriber

/**
 * @作者: yzq
 * @创建日期: 2019/8/26 17:47
 * @文件作用: 主页面的其它页面
 */
class OneOtherFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return  R.layout.fragment_one_other
    }

    override fun initViews(savedInstanceState: Bundle) {

    }

    override fun initData() {
    }
}
