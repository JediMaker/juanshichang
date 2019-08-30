package com.example.juanshichang.activity

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
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
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
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
import com.example.juanshichang.widget.LiveDataBus
import com.google.gson.Gson
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.coroutines.Runnable
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import rx.Subscriber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class SettingActivity : BaseActivity(), View.OnClickListener {
    private var userName: String? = null
    private var uNDialog: QMUIDialog.EditTextDialogBuilder? = null
    private var uIADialog: QMUIDialog.MenuDialogBuilder? = null
    private var cameraSavePath: Uri? = null
    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@SettingActivity, R.color.white)
        unlogin.setOnClickListener(this)
        setRet.setOnClickListener(this)
        setUserImage.setOnClickListener(this)
        setUserName.setOnClickListener(this)
        setUND()//创建 用户昵称对话框 和 图片选择器
    }

    override fun initData() {
        cameraSavePath =
            Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg") //解决小米手机崩溃问题
        getSetting()
    }

    override fun onClick(v: View?) {
        when (v) {
            unlogin -> { //退出登录
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
                            Log.e("uuid", "执行清理程序")
                            MyApp.sp.edit().remove("uu").commit()
                            SpUtil.getIstance().getDelete()
                            Util.removeCookie(this@SettingActivity)
                            ToastUtil.showToast(this@SettingActivity, "清理完成")
                            LiveDataBus.get().with("mainGo").value =  0
                            finish()
                        }

                        override fun cancle() {

                        }

                    })
            }
            setRet -> {
                this@SettingActivity.finish()
            }
            setUserImage -> {//修改头像
                goPermission() //获取用户读写权限
            }
            setUserName -> {
                if (userName != null && !TextUtils.isEmpty(userName)) {
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
    private fun getSetting() {
        HttpManager.getInstance().post(Api.SETTINGS, Parameter.getBenefitMap(), object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@SettingActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val data = jsonObj.getJSONObject("data")
                        val avatar = data.getString("avatar")
                        val nickname = data.getString("nickname")
                        val create_time = data.getInt("create_time")
                        this@SettingActivity.runOnUiThread(object : Runnable {
                            override fun run() {
                                setUi(avatar, nickname, create_time)
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

    private fun goPermission() {
        val PERMISSION_CAM = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        AndPermission.with(this@SettingActivity).runtime().permission(PERMISSION_CAM).onGranted({
            //使用权限
            /*imageUriP = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //执行这个
                val imageFile: File = Util.createImageFile(this)!!
                imageUriP = FileProvider.getUriForFile(this, "com.example.juanshichang.fileProvider", imageFile)
            } else {
                imageUriP = cameraSavePath
            }*/
            uIADialog?.show()
        }).onDenied({
            //拒绝使用权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //如果sdk大于23则跳转
                ToastUtil.showToast(this@SettingActivity, "请前往设置中心开启相关权限")
                JumpPermissionManagement.GoToSetting(this@SettingActivity)
            }
        }).start()
    }

    private fun setUi(avatar: String, nickname: String, create_time: Int) {
        setUserReg.text = Util.getTimedateTwo(create_time.toLong()) //日期
        GlideUtil.loadHeadImage(this@SettingActivity, avatar, userImage) //头像
        userName = nickname //昵称
    }

    private var imageUriP: Uri? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (data != null) {
            var photoPath: String? = null
//        val uri = data?.data
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {//拍照图片
                    val bundle: Bundle = data.extras!!
                    val images: Bitmap = bundle.get("data") as Bitmap //获取到的图片
//                    val imageUri: Uri = imageUriP!!
                    val imageUri: Uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), images, null,null))
                    val ivStr = getRealFilePath(this, imageUri)
                    showProgressDialog()
                    setIconUser(ivStr)
                }
                REQUEST_IMAGE_PICK -> {//相册图片
                    val filePath: String = getRealFilePath(this, data?.data!!)
//                    val bitmap = PhotoUtil.getSmallBitmap(filePath)
//                    val NewBitmap = PhotoUtil.rotatingImageView(90,bitmap)
                    val Newpath = PhotoUtil.compressImage(filePath)
                    showProgressDialog()
                    setIconUser(Newpath)
                }
                REQUEST_IMAGE_CROP -> {//裁剪照片

                }
            }
        } else {
            ToastUtil.showToast(this, "Photo:404")
        }
    }

    private fun goCamera() { //相机操作类
        if (PhotoUtils.hasCamera(this)) {
            //相机指定拍照地址
//            val b = PhotoUtils.isFolderExists(getRealFilePath(this,imageUriP!!))
//            if(b){
                PhotoUtils.takePicture(this, imageUriP, REQUEST_IMAGE_CAPTURE)
//            }else{
//                ToastUtil.showToast(this,"Create Camera : 404")
//            }
        } else {
            ToastUtil.showToast(this, "你的设备暂不支持拍照")
        }
    }

    companion object {
        val REQUEST_IMAGE_CAPTURE: Int = 100 //拍照
        val REQUEST_IMAGE_PICK: Int = 101 //相册
        val REQUEST_IMAGE_CROP: Int = 102 //裁剪
        /**
         *兼容android7.0以上的系统
         */
        public fun getRealFilePath(activity: Activity, uri: Uri): String {
            val scheme: String = uri?.scheme!!
            var data: String? = null
            if (scheme == null) {
                data = uri.path
            } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                data = uri.getPath()
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                val cursor: Cursor = activity.getContentResolver().query(
                    uri,
                    arrayOf(MediaStore.Images.ImageColumns.DATA),
                    null,
                    null,
                    null
                )!!
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                        if (index > -1) {
                            data = cursor.getString(index)
                        }
                    }
                    cursor.close()
                }
            }
            return data!!
        }
    }

    private fun setUND() {
        //修改昵称 Dialog
        uNDialog = QMUIDialog.EditTextDialogBuilder(this)
        uNDialog?.setTitle("昵称")
        uNDialog?.setPlaceholder("在此输入您的昵称") //Hint
        uNDialog?.addAction("取消", QMUIDialogAction.ActionListener { dialog, index ->
            ToastUtil.showToast(this@SettingActivity, "昵称未修改")
            dialog.dismiss()
        })
        uNDialog?.addAction("确定", QMUIDialogAction.ActionListener { dialog, index ->
            val newName = uNDialog?.editText?.text
            if (!newName?.equals(userName)!!) {
                if (newName != null && !TextUtils.isEmpty(newName)) {
                    if (newName.length > 10) {
                        ToastUtil.showToast(this@SettingActivity, "昵称过长 请重新设置")
                        uNDialog?.editText?.setText("")
                    } else {
                        setNewName(newName.toString().trim())
                        dialog.dismiss()
                    }
                } else {
                    ToastUtil.showToast(this@SettingActivity, "昵称不能为空")
                }
            } else {
                ToastUtil.showToast(this@SettingActivity, "昵称未做修改")
            }
        })
        uNDialog?.create()
        //设置头像弹窗
        uIADialog = QMUIDialog.MenuDialogBuilder(this)
        uIADialog?.addItem("拍照", DialogInterface.OnClickListener { dialogInterface, i ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1);
                } else {
                    goCamera()
                    dialogInterface.dismiss()
                }
            }
        })
        uIADialog?.addItem("图库", DialogInterface.OnClickListener { dialogInterface, i ->
            //            PhotoUtils.openPic(this,REQUEST_IMAGE_PICK)
            val intent = Intent(Intent.ACTION_PICK, null)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION  or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
            dialogInterface.dismiss()
        })
        uIADialog?.create()
    }

    //修改用户昵称
    private fun setNewName(nickname: String) {
        HttpManager.getInstance().post(Api.SETINFO, Parameter.getUpdInfo(nickname), object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@SettingActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val user = SpUtil.getIstance().user
                        user.nick_name = nickname
                        SpUtil.getIstance().user = user
                        this@SettingActivity.runOnUiThread(object : Runnable {
                            override fun run() {
                                ToastUtil.showToast(this@SettingActivity, "昵称修改成功")
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
                this@SettingActivity.runOnUiThread(object : Runnable {
                    override fun run() {
                        ToastUtil.showToast(this@SettingActivity, "昵称修改失败,请稍后重试")
                    }
                })
            }

        })
    }

    //修改用户头像
    fun setIconUser(path: String) {//bt:Bitmap?,
        //1.获取图片，创建请求体
        val file = File(path)
        val requestFile: RequestBody =
            RequestBody.create("image/png".toMediaTypeOrNull(), file)//MediaType.parse("multipart/form-data")
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestFile)

        HttpManager.getInstance().upload(Api.SETAVATER, Parameter.getBenefitMap(), body, object : Subscriber<String>() {
            override fun onNext(str: String?) {
                if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                    var jsonObj: JSONObject? = null
                    jsonObj = JSONObject(str)
                    if (!jsonObj.optString(JsonParser.JSON_CODE).equals(JsonParser.JSON_SUCCESS)) {
                        ToastUtil.showToast(this@SettingActivity, jsonObj.optString(JsonParser.JSON_MSG))
                    } else {
                        val user = SpUtil.getIstance().user
                        SpUtil.getIstance().user = user
                        this@SettingActivity.runOnUiThread(object : Runnable {
                            override fun run() {
                                dismissProgressDialog()
                                ToastUtil.showToast(this@SettingActivity, "头像修改成功")
                                if (path != "" && !TextUtils.isEmpty(path)) {
                                    GlideUtil.loadHeadImage(this@SettingActivity, path, userImage)
                                }
                            }
                        })
                    }
                }
            }

            override fun onCompleted() {
                Log.e("onCompleted", "头像修改完成!")
            }

            override fun onError(e: Throwable?) {
                Log.e("onError", "头像修改失败!" + e)
                this@SettingActivity.runOnUiThread(object : Runnable {
                    override fun run() {
                        dismissProgressDialog()
                        ToastUtil.showToast(this@SettingActivity, "头像修改失败,请稍后重试")
                    }
                })
            }

        })
    }
}
