package com.example.juanshichang.adapter

import android.content.Intent
import android.text.TextUtils
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.activity.LookAllActivity
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.bean.TabOneBean

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
                // fatherid 是父类 id
                intent.putExtra("category_id",item.category_list[position].category_id)
                intent.putExtra("itemtype",0)
                BaseActivity.goStartActivity(mContext!!,intent)
            })
        }
        helper?.addOnClickListener(R.id.twoLowAllR)
    }


    public fun setIsId(id:Int){
        fatherid = id
    }
}