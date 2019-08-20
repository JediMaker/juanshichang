package com.example.juanshichang.activity

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.juanshichang.MainActivity
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.base.Api
import com.example.juanshichang.base.BaseActivity
import com.example.juanshichang.base.JsonParser
import com.example.juanshichang.base.Parameter
import com.example.juanshichang.bean.FansBean
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.*
import com.example.juanshichang.utils.glide.GlideUtil
import com.google.gson.Gson
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.coroutines.Runnable
import org.json.JSONObject
import rx.Subscriber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class SettingActivity : BaseActivity(),View.OnClickListener {
    private var userName:String? = null
    private var uNDialog:QMUIDialog.EditTextDialogBuilder? = null
    private var uIADialog:QMUIDialog.MenuDialogBuilder? = null

    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@SettingActivity, R.color.white)
        unlogin.setOnClickListener(this)
        setRet.setOnClickListener(this)
        setUserImage.setOnClickListener(this)
        setUserName.setOnClickListener(this)
        setUND()//创建 用户昵称对话框 和 图片选择器
    }
    override fun initData() {
        getSetting()
    }
    override fun onClick(v: View?) {
        when(v){
            unlogin ->{ //退出登录
                this.showDialog(
                    BaseActivity.TOAST_WARN,
                    "确认退出吗？",
                    "确定",
                    "取消",
                    object : BaseActivity.DialogCallback {
                        override fun sure() {
                            Thread {
                                kotlin.run {
                                    Glide.get(this@SettingActivity).clearDiskCache()//磁盘缓存清理
                                }
                            }.start()
                            Log.e("uuid","执行清理程序")
                            MyApp.sp.edit().remove("uu").commit()
                            SpUtil.getIstance().getDelete()
                            Util.removeCookie(this@SettingActivity)
                            ToastUtil.showToast(this@SettingActivity,"清理完成")
                            BaseActivity.goStartActivity(this@SettingActivity, MainActivity())
                            finish()
                        }

                        override fun cancle() {

                        }

                    })
            }
            setRet ->{
                this@SettingActivity.finish()
            }
            setUserImage->{//修改头像
                goPermission() //获取用户读写权限
            }
            setUserName->{
                if(userName!=null && !TextUtils.isEmpty(userName)){
                    uNDialog?.setDefaultText(userName)
                }
                uNDialog?.show()
            }
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_setting
    }
    //获取用户信息
    private fun getSetting(){
        HttpManager.getInstance().post(Api.SETTINGS, Parameter.getBenefitMap(),object : Subscriber<String>(){
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@SettingActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else{
                        val data = jsonObj.getJSONObject("data")
                        val avatar = data.getString("avatar")
                        val nickname = data.getString("nickname")
                        val create_time = data.getInt("create_time")
                        this@SettingActivity.runOnUiThread(object:Runnable{
                            override fun run() {
                                setUi(avatar,nickname,create_time)
                            }
                        })
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted", "用户Set加载完成!")
            }

            override fun onError(e: Throwable?) {
                Log.e("onError", "用户Set加载失败!" + e)
            }

        })
    }
    //修改用户昵称
    private fun setNewName(nickname:String){
        HttpManager.getInstance().post(Api.SETINFO, Parameter.getUpdInfo(nickname),object : Subscriber<String>(){
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@SettingActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else{
                        val user = SpUtil.getIstance().user
                        user.nick_name = nickname
                        SpUtil.getIstance().user = user
                        this@SettingActivity.runOnUiThread(object:Runnable{
                            override fun run() {
                                ToastUtil.showToast(this@SettingActivity,"昵称修改成功")
                                userName = nickname
                            }
                        })
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted", "昵称修改加载完成!")
            }

            override fun onError(e: Throwable?) {
                Log.e("onError", "昵称修改加载失败!" + e)
                this@SettingActivity.runOnUiThread(object:Runnable{
                    override fun run() {
                        ToastUtil.showToast(this@SettingActivity,"昵称修改失败,请稍后重试")
                    }
                })
            }

        })
    }
    private fun goPermission() {
        val PERMISSION_CAM = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        AndPermission.with(this@SettingActivity).runtime().permission(PERMISSION_CAM).onGranted({
            //使用权限
            uIADialog?.show()
        }).onDenied({
            //拒绝使用权限
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //如果sdk大于23则跳转
                ToastUtil.showToast(this@SettingActivity, "请前往设置中心开启相关权限")
                JumpPermissionManagement.GoToSetting(this@SettingActivity)
            }
        }).start()
    }
    private fun setUi(avatar:String,nickname:String,create_time:Int){
        setUserReg.text = Util.getTimedateTwo(create_time.toLong()) //日期
        GlideUtil.loadHeadImage(this@SettingActivity,avatar,userImage) //头像
        userName = nickname //昵称
    }
    private var imageUriP: Uri? = null
    private fun setUND() {
        uNDialog= QMUIDialog.EditTextDialogBuilder(this)
        uNDialog?.setTitle("昵称")
        uNDialog?.setPlaceholder("在此输入您的昵称") //Hint
        uNDialog?.addAction("取消", QMUIDialogAction.ActionListener { dialog, index ->
            ToastUtil.showToast(this@SettingActivity,"昵称未修改")
            dialog.dismiss()
        })
        uNDialog?.addAction("确定", QMUIDialogAction.ActionListener { dialog, index ->
            val newName = uNDialog?.editText?.text
            if(newName!=null && !TextUtils.isEmpty(newName)){
                if(newName.length > 10){
                    ToastUtil.showToast(this@SettingActivity,"昵称过长 请重新设置")
                    uNDialog?.editText?.setText("")
                }else{
                    setNewName(newName.toString().trim())
                    dialog.dismiss()
                }
            }else{
                ToastUtil.showToast(this@SettingActivity,"昵称不能为空")
            }
        })
        uNDialog?.create()
        uIADialog = QMUIDialog.MenuDialogBuilder(this)
        uIADialog?.addItem("拍照", DialogInterface.OnClickListener { dialogInterface, i ->
            val imageFile: File = Util.createImageFile(this)!!
            imageUriP = null
            if(imageFile!=null){
                imageUriP = FileProvider.getUriForFile(this, "com.tencent.lg.fileProvider", imageFile)
                val takePictureIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriP)
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
            dialogInterface.dismiss()
        })
        uIADialog?.addItem("图库", DialogInterface.OnClickListener { dialogInterface, i ->
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/png")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION  or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
            dialogInterface.dismiss()
        })
        uIADialog?.create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_IMAGE_PICK ->{//相册图片
                    val uri:Uri = data?.data!!
                    if(!TextUtils.isEmpty(uri.toString())){
                        // 获得了图片
                        startCropImage(uri)
                        ToastUtil.showToast(this,"图片已裁剪处理 下一步待操作")
                    }else{
                        // TODO 处理异常
                        ToastUtil.showToast(this,"图片处理 异常")
                    }
                }
                REQUEST_IMAGE_CAPTURE->{//拍照图片
                    if(imageUriP!=null){
                        startCropImage(imageUriP!!)
                    }
                }
                REQUEST_IMAGE_CROP->{//裁剪照片
                    ToastUtil.showToast(this,"图片剪处理 完成")
                    val bitmap:Bitmap  = data?.getParcelableExtra("data")!!
                    userImage.setImageBitmap(bitmap)
                    val picFile:File = File(
                        Environment.getExternalStorageDirectory(),
                        "${System.currentTimeMillis()} .png")
                    // 把bitmap放置到文件中
                    // format 格式
                    // quality 质量
                    try {
                        bitmap.compress(
                            Bitmap.CompressFormat.PNG, 100, FileOutputStream(picFile)
                        )
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }

                    //todo 待上传
                }
            }
        }else{
            ToastUtil.showToast(this,"失败")
        }
    }
    companion object{
        val REQUEST_IMAGE_CAPTURE:Int = 100 //拍照
        val REQUEST_IMAGE_PICK:Int = 101 //相册
        val REQUEST_IMAGE_CROP:Int = 102 //裁剪
    }
    private fun startCropImage(uri:Uri){
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")//image/*
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        // 使图片处于可裁剪状态
        intent.putExtra("crop", "true")
        // 裁剪框的比例（根据需要显示的图片比例进行设置）
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // 让裁剪框支持缩放
        intent.putExtra("scale", true)
        // 裁剪后图片的大小（注意和上面的裁剪比例保持一致）
        intent.putExtra("outputX", 300)
        intent.putExtra("outputY", 300)

        /*// 传递原图路径
        try {
            cropFile = FileUtil.getCacheImageFile(this);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理错误
            showDialog("打开文件失败");
            return;
        }
        cropImageUri = FileProvider.getUriForFile(getContext(), AUTHORITY, cropFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);*/
        // 设置裁剪区域的形状，默认为矩形，也可设置为原形
        intent.putExtra("circleCrop", false)
        // 设置图片的输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
        // return-data=true传递的为缩略图，小米手机默认传递大图，所以会导致onActivityResult调用失败
        intent.putExtra("return-data", true)
        // 是否需要人脸识别
//        intent.putExtra("noFaceDetection", true)
        startActivityForResult(intent, REQUEST_IMAGE_CROP)
    }
}
