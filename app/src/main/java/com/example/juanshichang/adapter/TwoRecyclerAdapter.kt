package com.example.juanshichang.adapter

import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.GridView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.activity.LookAllActivity
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.bean.TabOneBean
import com.example.juanshichang.utils.ToastUtil

class TwoRecyclerAdapter:
    BaseQuickAdapter<TabOneBean.Data, BaseViewHolder>(R.layout.item_two_recycler){
    var gridAdp: TwoGridAdapterT? = null
    var gridManager:GridLayoutManager = GridLayoutManager(mContext,3)
    var fatherid:Int? = 0
    override fun convert(helper: BaseViewHolder?, item: TabOneBean.Data?) {
        if(!TextUtils.isEmpty(item?.category_name)){
            helper?.setText(R.id.twoTitle,item?.category_name)
        }
        if (item?.category_list?.size != 0){
            gridAdp =
                TwoGridAdapterT(item?.category_list!!)
            gridAdp?.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
            val grid:RecyclerView = helper?.getView(R.id.twoGrid)!!
            grid.layoutManager = gridManager
            grid.adapter = gridAdp
//            grid.isFocusable = false  //拒绝获取焦点
            gridAdp?.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                val intent: Intent = Intent(mContext!!, LookAllActivity::class.java)
                intent.putExtra("category_id",fatherid)
                intent.putExtra("itemtype",position+1)
                BaseActivity.goStartActivity(mContext!!,intent)
            })
        }
        helper?.addOnClickListener(R.id.twoLowAllR)
    }


    public fun setIsId(id:Int){
        fatherid = id
    }
}