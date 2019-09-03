package com.example.juanshichang.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.juanshichang.MainActivity
import com.example.juanshichang.R
import com.example.juanshichang.adapter.MessageAdapter
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.MessageBean
import com.example.juanshichang.bean.TabOneBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.coroutines.Runnable
import org.json.JSONObject
import rx.Subscriber

class MessageActivity : BaseActivity(), View.OnClickListener{
    private var mAdapter:MessageAdapter? = null
    private var messageData:ArrayList<MessageBean.Message>? = null
    override fun getContentView(): Int {
        return R.layout.activity_message
    }

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this, R.color.white)
        MsRet.setOnClickListener(this) //返回
        titClose.setOnClickListener(this) //关闭
        titOpen.setOnClickListener(this) // 打开
        if(NotifyUtil.isPermissonOpen(this)){ //判断 是否显示顶部通知
            popHint.visibility = View.GONE
            ToastTool.showToast(this,"已有通知权限 关闭显示框")
        }
//        if(Util.hasLogin()){
            mAdapter = MessageAdapter()
            mAdapter?.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
            mAdapter?.emptyView = View.inflate(this, R.layout.activity_not_null, null)
            mesRecycler.adapter = mAdapter
            getMesList(this@MessageActivity)
//        }else{
//            val intent = Intent(this,Reg2LogActivity::class.java)
//            intent.putExtra("type",1)
//            BaseActivity.goStartActivity(this,intent)
//            ToastUtil.showToast(this@MessageActivity,"请登录...")
//            finish()
//        }
    }

    override fun initData() {
        mAdapter?.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener{
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, i: Int) {
                ToastTool.showToast(this@MessageActivity,"点击了第 ${i+1} 条消息")
                NotifyUtil.sendSimpleNotify(this@MessageActivity,messageData!![i].title,messageData!![i].content)
            }
        })
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.MsRet->{
                finish()
            }
            R.id.titClose->{
                popHint.visibility = View.GONE
            }
            R.id.titOpen->{
                NotifyUtil.openPermissionSetting(this)
            }
        }
    }

    private fun getMesList(context: Context) { //消息列表无需登录
        HttpManager.getInstance()
            .post(Api.MESSAGELIST, Parameter.getMainBannerMap(), object : Subscriber<String>() {
                override fun onNext(str: String?) {
                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject = JSONObject(str)
                        if (!jsonObj?.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                            ToastUtil.showToast(context, jsonObj.optString(JsonParser.JSON_MSG))
                        } else {
                            val data = Gson().fromJson(str, MessageBean.MessageBeans::class.java)
                            val list = data.data.message_list
                            mesRecycler.post(object : Runnable{
                                override fun run() {
                                    mAdapter?.setNewData(list)
                                    if (messageData == null){
                                        messageData = ArrayList()
                                    }else{
                                        messageData?.clear()
                                    }
                                    messageData?.addAll(list)
                                }
                            })
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "消息列表加载完成!")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "消息列表加载失败!" + e)
                }
            })
    }
}
