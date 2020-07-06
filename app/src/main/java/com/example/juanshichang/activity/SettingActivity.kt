package com.example.juanshichang.activity

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.juanshichang.MyApp
import com.example.juanshichang.R
import com.example.juanshichang.base.*
import com.example.juanshichang.bean.User
import com.example.juanshichang.http.HttpManager
import com.example.juanshichang.utils.*
import com.example.juanshichang.utils.glide.GlideUtil
import com.example.juanshichang.widget.LiveDataBus
import com.qmuiteam.qmui.widget.QMUIRadiusImageView
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.coroutines.Runnable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import rx.Subscriber
import java.io.File

class SettingActivity : BaseActivity(), View.OnClickListener {
    private var userName: String? = null
    private var uNDialog: QMUIDialog.EditTextDialogBuilder? = null
    private var uIADialog: QMUIDialog.MenuDialogBuilder? = null
    private var userZfb: String? = null
    private var zfbDialog: QMUIDialog.EditTextDialogBuilder? = null
    private var cameraSavePath: Uri? = null
    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@SettingActivity, R.color.white)
        unlogin.setOnClickListener(this)
        setRet.setOnClickListener(this)
        setUserImage.setOnClickListener(this)
        setUserName.setOnClickListener(this)
        setZfb.setOnClickListener(this)
        realName.setOnClickListener(this)
        mSite.setOnClickListener(this)
        setUND()//创建 用户昵称对话框 和 图片选择器
        setUi(SpUtil.getIstance().user)
    }

    override fun initData() {
//        cameraSavePath =
//            Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg") //解决小米手机崩溃问题
//        getSetting()//接口取消重新上接口
    }

    override fun onClick(v: View?) {
        when (v) {
            unlogin -> { //退出登录
                this.showRegisterDialog(
                    "提示",
                    "确认退出登录吗？",
                    "退出",
                    R.color.indicatorRed,
                    "取消",
                    object : BaseActivity.DialogCallback {
                        override fun sure() {
                            Thread {
                                kotlin.run {
                                    Glide.get(this@SettingActivity).clearDiskCache()//磁盘缓存清理
                                }
                            }.start()
                            LogTool.e("uuid", "执行清理程序")
                            MyApp.sp.edit().remove("uu").apply() //延时清理
//                            SpUtil.getIstance().getDelete()
                            SpUtil.getIstance().remove("useruid")//更改授权模式退出登录只清除uid
                            Util.removeCookie(this@SettingActivity)
                            ToastUtil.showToast(this@SettingActivity, "清理完成")
                            LiveDataBus.get().with("mainGo").value = 0 //发送返回主界面广播
                            finish()
                        }

                        override fun cancle() {

                        }

                    }, false
                )
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
            mSite -> { //我的收货地址
                goStartActivity(this@SettingActivity, SiteListActivity())
            }
            setZfb -> {
                if (userZfb != null && !TextUtils.isEmpty(userZfb)) {
                    zfbDialog?.setDefaultText(userZfb)
                }
                zfbDialog?.show()
            }
            realName -> { //实名认证
                goStartActivity(this@SettingActivity, RealNameActivity())
            }
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_setting
    }

    //获取用户信息
    private fun getSetting() {
        HttpManager.getInstance()
            .post(Api.SETTINGS, Parameter.getBenefitMap(), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str = result?.substring(result?.indexOf("{"), result.length)

                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        jsonObj = JSONObject(str)
                        if (!jsonObj.optString(JsonParser.JSON_CODE)
                                .equals(JsonParser.JSON_SUCCESS)
                        ) {
                            ToastUtil.showToast(
                                this@SettingActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val data = jsonObj.getJSONObject("data")
                            val avatar = data.getString("avatar")
                            val nickname = data.getString("nickname")
                            val create_time = data.getInt("create_time")
                            val ali_pay_account = data.getString("ali_pay_account")
                            this@SettingActivity.runOnUiThread(object : Runnable {
                                override fun run() {
                                    setUi(avatar, nickname, create_time, ali_pay_account)
                                }
                            })
                            val u = SpUtil.getIstance().user
                            u.ali_pay_account = ali_pay_account
                            SpUtil.getIstance().user = u  //写入
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "用户Set加载完成!")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "用户Set加载失败!" + e)
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

    private fun setUi(user: User?) {
        Zfb.text = user!!.ali_pay_account //提现账户
        setUserReg.text = user!!.date_added //日期
        nickName.text = user!!.nick_name //昵称
        GlideUtil.loadHeadImage(this@SettingActivity, user!!.avatar, userImage!!) //头像
        userName = user!!.nick_name  //昵称
        userZfb = user!!.ali_pay_account  //提现账户
    }

    private fun setUi(avatar: String, nickname: String, create_time: Int, ali: String) {
        Zfb.text = ali //提现账户
        setUserReg.text = Util.getTimedateTwo(create_time.toLong()) //日期
        GlideUtil.loadHeadImage(this@SettingActivity, avatar, userImage!!) //头像
        userName = nickname //昵称
        userZfb = ali //提现账户
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
                    val imageUri: Uri = Uri.parse(
                        MediaStore.Images.Media.insertImage(
                            getContentResolver(),
                            images,
                            null,
                            null
                        )
                    )
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
            ToastTool.showToast(this@SettingActivity, "取消修改")
            dialog.dismiss()
        })
        uNDialog?.addAction("确定", QMUIDialogAction.ActionListener { dialog, index ->
            val newName = uNDialog?.editText?.text.toString()
            if (!newName.equals(userName.toString())) {
                if (!TextUtils.isEmpty(newName) && newName != "") {
                    if (newName.length > 10) {
                        ToastUtil.showToast(this@SettingActivity, "昵称过长 请重新设置")
                        uNDialog?.editText?.setText("")
                    } else {
                        showProgressDialog()  //
                        setNewName(newName.toString().trim())
                        dialog.dismiss()
                    }
                } else {
                    ToastUtil.showToast(this@SettingActivity, "昵称不能为空")
                }
            } else {
                ToastUtil.showToast(this@SettingActivity, "昵称未修改")
                dialog.dismiss()
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
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
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
        //修改支付宝账号对话框
        zfbDialog = QMUIDialog.EditTextDialogBuilder(this)
        zfbDialog?.setTitle("支付宝提现账户")
        zfbDialog?.setPlaceholder("在此输入您的支付宝提现账户") //Hint
        zfbDialog?.editText?.inputType = EditorInfo.TYPE_CLASS_PHONE // todo 设置输入类型为手机号？？？
//        zfbDialog?.editText?.filters = InputFilter[]{(InputFilter.LengthFilter(11))}
        zfbDialog?.addAction("取消", QMUIDialogAction.ActionListener { dialog, index ->
            ToastTool.showToast(this@SettingActivity, "提现账户修改已取消")
            dialog.dismiss()
        })
        zfbDialog?.addAction("确定", QMUIDialogAction.ActionListener { dialog, index ->
            val newZfb = zfbDialog?.editText?.text.toString()
            if (!newZfb.equals(userZfb.toString())) {
                if (!TextUtils.isEmpty(newZfb) && newZfb != "") {
                    if (newZfb.length != 11 || !Util.validateMobile(newZfb)) {
                        ToastUtil.showToast(this@SettingActivity, "请输入正确的提现账户")
                        zfbDialog?.editText?.setText("")
                    } else {
                        showProgressDialog()  //
                        setNewZfb(newZfb.toString().trim())
                        dialog.dismiss()
                    }
                } else {
                    ToastUtil.showToast(this@SettingActivity, "提现账户不能为空")
                }
            } else {
                ToastUtil.showToast(this@SettingActivity, "提现账户未修改")
                dialog.dismiss()
            }
        })
        zfbDialog?.create()
    }

    //修改用户昵称
    private fun setNewName(nickname: String) {
        HttpManager.getInstance()
            .post(Api.SETINFO, NewParameter.getUpdInfo(nickname), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo后台返回数据结构问题，暂时这样处理
                    val str = result?.substring(result?.indexOf("{"), result.length)

                    if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                        var jsonObj: JSONObject? = null
                        jsonObj = JSONObject(str)
                        if (!jsonObj.optBoolean(JsonParser.JSON_Status)
                        ) {
                            ToastUtil.showToast(
                                this@SettingActivity,
                                jsonObj.optString(JsonParser.JSON_MSG)
                            )
                        } else {
                            val user = SpUtil.getIstance().user
                            user.nick_name = nickname
                            nickName.text = nickname //昵称
                            SpUtil.getIstance().user = user
                            this@SettingActivity.runOnUiThread(object : Runnable {
                                override fun run() {
                                    ToastTool.showToast(this@SettingActivity, "昵称修改成功")
                                    userName = nickname
                                    dismissProgressDialog()
                                }
                            })
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "昵称修改加载完成!")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "昵称修改加载失败!" + e)
                    this@SettingActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            ToastUtil.showToast(this@SettingActivity, "昵称修改失败,请稍后重试")
                            dismissProgressDialog()
                        }
                    })
                }

            })
    }

    //修改用户支付宝
    private fun setNewZfb(ali_pay_account: String) {
        HttpManager.getInstance()
            .post(
                Api.UPDZFB,
                NewParameter.getUpdZfb(ali_pay_account),
                object : Subscriber<String>() {
                    override fun onNext(result: String?) {
                        //todo后台返回数据结构问题，暂时这样处理
                        val str = result?.substring(result?.indexOf("{"), result.length)

                        if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                            var jsonObj: JSONObject? = null
                            jsonObj = JSONObject(str)
                            if (!jsonObj.optBoolean(JsonParser.JSON_Status)
                            ) {
                                ToastUtil.showToast(
                                    this@SettingActivity,
                                    jsonObj.optString(JsonParser.JSON_MSG)
                                )
                            } else {
                                val user = SpUtil.getIstance().user
                                user.ali_pay_account = ali_pay_account
                                SpUtil.getIstance().user = user
                                this@SettingActivity.runOnUiThread(object : Runnable {
                                    override fun run() {
                                        ToastTool.showToast(this@SettingActivity, "提现账户修改成功")
                                        Zfb.text = ali_pay_account
                                        userZfb = ali_pay_account
                                        dismissProgressDialog()
                                    }
                                })
                            }
                        }
                    }

                    override fun onCompleted() {
                        LogTool.e("onCompleted", "提现账户修改加载完成!")
                    }

                    override fun onError(e: Throwable?) {
                        LogTool.e("onError", "提现账户修改失败!" + e)
                        this@SettingActivity.runOnUiThread(object : Runnable {
                            override fun run() {
                                ToastUtil.showToast(this@SettingActivity, "提现账户修改失败,请稍后重试")
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
            RequestBody.create(
                "image/png".toMediaTypeOrNull(),
                file
            )//MediaType.parse("multipart/form-data")
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        HttpManager.getInstance()
            .post(
                Api.SETAVATER,
                NewParameter.getUploadUserFaceMap(file),
                object : Subscriber<String>() {
                    override fun onNext(str: String?) {
                        if (JsonParser.isValidJsonWithSimpleJudge(str!!)) {
                            var jsonObj: JSONObject? = null
                            jsonObj = JSONObject(str)
                            if (!jsonObj.optBoolean(JsonParser.JSON_Status)
                            ) {
                                ToastUtil.showToast(
                                    this@SettingActivity,
                                    jsonObj.optString(JsonParser.JSON_MSG)
                                )
                            } else {
                                this@SettingActivity.runOnUiThread(object : Runnable {
                                    override fun run() {
                                        dismissProgressDialog()
                                        val data = jsonObj.getJSONObject("data")
                                        val avatar = data.getString("uri")
                                        ToastUtil.showToast(this@SettingActivity, "头像修改成功")
                                        if (path != "" && !TextUtils.isEmpty(path)) {
                                            GlideUtil.loadHeadImage(
                                                this@SettingActivity,
                                                path,
                                                userImage
                                            )
                                        }

                                        val u = SpUtil.getIstance().user
                                        u.avatar = avatar
                                        SpUtil.getIstance().user = u  //写入
                                    }
                                })
                            }
                        }
                    }

                    override fun onCompleted() {
                        LogTool.e("onCompleted", "头像修改完成!")
                    }

                    override fun onError(e: Throwable?) {
                        LogTool.e("onError", "头像修改失败!" + e)
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
