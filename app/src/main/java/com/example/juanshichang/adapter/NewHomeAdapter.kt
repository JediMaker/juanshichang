package com.example.juanshichang.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.juanshichang.R
import com.example.juanshichang.activity.ShangPinZyContains
import com.example.juanshichang.bean.HomeBean
import com.example.juanshichang.utils.LogTool

class NewHomeAdapter : BaseQuickAdapter<HomeBean.Product, BaseViewHolder>(R.layout.item_newzy_home) {
    override fun convert(helper: BaseViewHolder?, item: HomeBean.Product?) {
        item?.let {
            helper?.setText(R.id.topHTit,it.name)
            if (it.name.contains("热")){
                helper?.setImageResource(R.id.topHImg,R.drawable.index_hot)
            }else if(it.name.contains("新")){
                helper?.setImageResource(R.id.topHImg,R.drawable.index_new)
            }else{
                helper?.setImageResource(R.id.topHImg,R.drawable.index_new)
            }
            val data = item.date
            val rc = helper?.getView(R.id.botGrid) as RecyclerView
            val rAdapter = NewHomeSonAdapter()
            rc.adapter = rAdapter
            rAdapter.setNewData(data)
            rAdapter.setOnItemClickListener(object :OnItemClickListener{
                override fun onItemClick(
                    adapter: BaseQuickAdapter<*, *>?,
                    view: View?,
                    position: Int
                ) {
                    LogTool.e("homeAdapter2","$position")
                    val intent = Intent(mContext, ShangPinZyContains::class.java)
                    intent.putExtra("product_id",data[position].product_id)
                    mContext.startActivity(intent)
                }
            })
        }

    }
}