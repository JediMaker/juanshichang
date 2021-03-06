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
import android.text.InputType
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
    private var mobileNum: String? = null
    private var uNDialog: QMUIDialog.EditTextDialogBuilder? = null
    private var mobileDialog: QMUIDialog.EditTextDialogBuilder? = null
    private var uIADialog: QMUIDialog.MenuDialogBuilder? = null
    private var userZfb: String? = null
    private var zfbDialog: QMUIDialog.EditTextDialogBuilder? = null
    private var cameraSavePath: Uri? = null
    override fun initView() {
        StatusBarUtil.addStatusViewWithColor(this@SettingActivity, R.color.colorPrimary)
        unlogin.setOnClickListener(this)
        setRet.setOnClickListener(this)
        setUserImage.setOnClickListener(this)
        setUserName.setOnClickListener(this)
        setZfb.setOnClickListener(this)
        realName.setOnClickListener(this)
        setMobile.setOnClickListener(this)
        modifyPassword.setOnClickListener(this)
        setUND()//?????? ????????????????????? ??? ???????????????
        setUi(SpUtil.getIstance().user)
    }

    override fun initData() {
//        cameraSavePath =
//            Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg") //??????????????????????????????
//        getSetting()//???????????????????????????
    }

    override fun onClick(v: View?) {
        when (v) {
            unlogin -> { //????????????
                this.showRegisterDialog(
                    "??????",
                    "????????????????????????",
                    "??????",
                    R.color.indicatorRed,
                    "??????",
                    object : BaseActivity.DialogCallback {
                        override fun sure() {
                            Thread {
                                kotlin.run {
                                    Glide.get(this@SettingActivity).clearDiskCache()//??????????????????
                                }
                            }.start()
                            LogTool.e("uuid", "??????????????????")
                            MyApp.sp.edit().remove("uu").apply() //????????????
//                            SpUtil.getIstance().getDelete()
                            SpUtil.getIstance().remove("useruid")//???????????????????????????????????????uid
                            Util.removeCookie(this@SettingActivity)
//                            ToastUtil.showToast(this@SettingActivity, "????????????")
                            LiveDataBus.get().with("mainGo").value = 0 //???????????????????????????
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
            setUserImage -> {//????????????
                goPermission() //????????????????????????
            }
            setUserName -> {
                if (userName != null && !TextUtils.isEmpty(userName)) {
                    uNDialog?.setDefaultText(userName)
                }
                uNDialog?.show()
            }
            setMobile -> {
                if (mobileNum != null && !TextUtils.isEmpty(mobileNum)) {
                    mobileDialog?.setDefaultText(mobileNum)
                }
                mobileDialog?.show()
            }
            modifyPassword -> { //????????????
//                goStartActivity(this@SettingActivity, SiteListActivity())
                val intent = Intent(this@SettingActivity, FastLoginActivity::class.java)
                intent.putExtra("type", FastLoginActivity.RESETPASSWORDCODE) // ?????????????????????????????????
                goStartActivity(this@SettingActivity, intent)
            }
            setZfb -> {
                if (userZfb != null && !TextUtils.isEmpty(userZfb)) {
                    zfbDialog?.setDefaultText(userZfb)
                }
                zfbDialog?.show()
            }
            realName -> { //????????????
                goStartActivity(this@SettingActivity, RealNameActivity())
            }
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_setting
    }

    //??????????????????
    private fun getSetting() {
        HttpManager.getInstance()
            .post(Api.SETTINGS, Parameter.getBenefitMap(), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo???????????????????????????????????????????????????
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
                            SpUtil.getIstance().user = u  //??????
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "??????Set????????????!")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "??????Set????????????!" + e)
                }

            })
    }

    private fun goPermission() {
        val PERMISSION_CAM = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
//        MyApp.requestPermission(this@SettingActivity)
        AndPermission.with(this@SettingActivity).runtime().permission(PERMISSION_CAM).onGranted({
            //????????????
            /*imageUriP = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //????????????
                val imageFile: File = Util.createImageFile(this)!!
                imageUriP = FileProvider.getUriForFile(this, "com.example.juanshichang.fileProvider", imageFile)
            } else {
                imageUriP = cameraSavePath
            }*/
            uIADialog?.show()
        }).onDenied({
            //??????????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //??????sdk??????23?????????
                ToastUtil.showToast(this@SettingActivity, "???????????????????????????????????????")
                JumpPermissionManagement.GoToSetting(this@SettingActivity)
            }
        }).start()
    }

    private fun setUi(user: User?) {
        Zfb.text = user?.ali_pay_account //????????????
        setUserReg.text = user?.date_added //??????
        nickName.text = user?.nick_name //??????
        mobileT.text = user?.phone_num //?????????.text = user!!.nick_name //??????
        GlideUtil.loadHeadImage(this@SettingActivity, user!!.avatar, userImage!!) //??????
        userName = user?.nick_name  //??????
        userZfb = user?.ali_pay_account  //????????????
        mobileNum = user?.phone_num  //?????????
    }

    private fun setUi(avatar: String, nickname: String, create_time: Int, ali: String) {
        Zfb.text = ali //????????????
        setUserReg.text = Util.getTimedateTwo(create_time.toLong()) //??????
        GlideUtil.loadHeadImage(this@SettingActivity, avatar, userImage!!) //??????
        userName = nickname //??????
        userZfb = ali //????????????
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
                REQUEST_IMAGE_CAPTURE -> {//????????????
                    val bundle: Bundle = data.extras!!
                    val images: Bitmap = bundle.get("data") as Bitmap //??????????????????
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
                REQUEST_IMAGE_PICK -> {//????????????
                    val filePath: String = getRealFilePath(this, data?.data!!)
//                    val bitmap = PhotoUtil.getSmallBitmap(filePath)
//                    val NewBitmap = PhotoUtil.rotatingImageView(90,bitmap)
                    val Newpath = PhotoUtil.compressImage(filePath)
                    showProgressDialog()
                    setIconUser(Newpath)
                }
                REQUEST_IMAGE_CROP -> {//????????????

                }
            }
        } else {
            ToastUtil.showToast(this, "Photo:404")
        }
    }

    private fun goCamera() { //???????????????
        if (PhotoUtils.hasCamera(this)) {
            //????????????????????????
//            val b = PhotoUtils.isFolderExists(getRealFilePath(this,imageUriP!!))
//            if(b){
            PhotoUtils.takePicture(this, imageUriP, REQUEST_IMAGE_CAPTURE)
//            }else{
//                ToastUtil.showToast(this,"Create Camera : 404")
//            }
        } else {
            ToastUtil.showToast(this, "??????????????????????????????")
        }
    }

    companion object {
        val REQUEST_IMAGE_CAPTURE: Int = 100 //??????
        val REQUEST_IMAGE_PICK: Int = 101 //??????
        val REQUEST_IMAGE_CROP: Int = 102 //??????

        /**
         *??????android7.0???????????????
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
        //???????????? Dialog
        uNDialog = QMUIDialog.EditTextDialogBuilder(this)
        uNDialog?.setTitle("??????")
        uNDialog?.setPlaceholder("????????????????????????") //Hint
        uNDialog?.addAction("??????", QMUIDialogAction.ActionListener { dialog, index ->
            ToastTool.showToast(this@SettingActivity, "????????????")
            dialog.dismiss()
        })
        uNDialog?.addAction("??????", QMUIDialogAction.ActionListener { dialog, index ->
            val newName = uNDialog?.editText?.text.toString()
            if (!newName.equals(userName.toString())) {
                if (!TextUtils.isEmpty(newName) && newName != "") {
                    if (newName.length > 10) {
                        ToastUtil.showToast(this@SettingActivity, "???????????? ???????????????")
                        uNDialog?.editText?.setText("")
                    } else {
                        showProgressDialog()  //
                        setNewName(newName.toString().trim())
                        dialog.dismiss()
                    }
                } else {
                    ToastUtil.showToast(this@SettingActivity, "??????????????????")
                }
            } else {
                ToastUtil.showToast(this@SettingActivity, "???????????????")
                dialog.dismiss()
            }
        })
        uNDialog?.create()
        //??????????????? Dialog
        mobileDialog = QMUIDialog.EditTextDialogBuilder(this)
        mobileDialog?.setInputType(InputType.TYPE_CLASS_PHONE)
        mobileDialog?.setTitle("?????????")
        mobileDialog?.setPlaceholder("???????????????????????????") //Hint
        mobileDialog?.addAction("??????", QMUIDialogAction.ActionListener { dialog, index ->
            ToastTool.showToast(this@SettingActivity, "????????????")
            dialog.dismiss()
        })
        mobileDialog?.addAction("??????", QMUIDialogAction.ActionListener { dialog, index ->
            val newmobilenum = mobileDialog?.editText?.text.toString()
            if (!newmobilenum.equals(mobileNum.toString())) {
                if (!TextUtils.isEmpty(newmobilenum) && newmobilenum != "") {
                    if (newmobilenum.length < 11) {
                        ToastUtil.showToast(this@SettingActivity, "??????????????????????????????")
                    }
                    if (!Util.validateMobile(newmobilenum)) {
                        ToastUtil.showToast(this@SettingActivity, "???????????????????????????")
                    } else {
                        showProgressDialog()  //
                        setNewMobile(newmobilenum.toString().trim())
                        dialog.dismiss()
                    }
                } else {
                    ToastUtil.showToast(this@SettingActivity, "?????????????????????")
                }
            } else {
                ToastUtil.showToast(this@SettingActivity, "??????????????????")
                dialog.dismiss()
            }
        })
        mobileDialog?.create()
        //??????????????????
        uIADialog = QMUIDialog.MenuDialogBuilder(this)
        uIADialog?.addItem("??????", DialogInterface.OnClickListener { dialogInterface, i ->
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
        uIADialog?.addItem("??????", DialogInterface.OnClickListener { dialogInterface, i ->
            //            PhotoUtils.openPic(this,REQUEST_IMAGE_PICK)
            val intent = Intent(Intent.ACTION_PICK, null)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION  or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
            dialogInterface.dismiss()
        })
        uIADialog?.create()
        //??????????????????????????????
        zfbDialog = QMUIDialog.EditTextDialogBuilder(this)
        zfbDialog?.setTitle("?????????????????????")
        zfbDialog?.setPlaceholder("???????????????????????????????????????") //Hint
        zfbDialog?.editText?.inputType = EditorInfo.TYPE_CLASS_PHONE // todo ???????????????????????????????????????
//        zfbDialog?.editText?.filters = InputFilter[]{(InputFilter.LengthFilter(11))}
        zfbDialog?.addAction("??????", QMUIDialogAction.ActionListener { dialog, index ->
            ToastTool.showToast(this@SettingActivity, "???????????????????????????")
            dialog.dismiss()
        })
        zfbDialog?.addAction("??????", QMUIDialogAction.ActionListener { dialog, index ->
            val newZfb = zfbDialog?.editText?.text.toString()
            if (!newZfb.equals(userZfb.toString())) {
                if (!TextUtils.isEmpty(newZfb) && newZfb != "") {
                    if (newZfb.length != 11 || !Util.validateMobile(newZfb)) {
                        ToastUtil.showToast(this@SettingActivity, "??????????????????????????????")
                        zfbDialog?.editText?.setText("")
                    } else {
                        showProgressDialog()  //
                        setNewZfb(newZfb.toString().trim())
                        dialog.dismiss()
                    }
                } else {
                    ToastUtil.showToast(this@SettingActivity, "????????????????????????")
                }
            } else {
                ToastUtil.showToast(this@SettingActivity, "?????????????????????")
                dialog.dismiss()
            }
        })
        zfbDialog?.create()
    }

    //??????????????????
    private fun setNewName(nickname: String) {
        HttpManager.getInstance()
            .post(Api.SETINFO, NewParameter.getUpdInfo(nickname), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo???????????????????????????????????????????????????
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
                            nickName.text = nickname //??????
                            SpUtil.getIstance().user = user
                            this@SettingActivity.runOnUiThread(object : Runnable {
                                override fun run() {
                                    ToastTool.showToast(this@SettingActivity, "??????????????????")
                                    userName = nickname
                                    dismissProgressDialog()
                                }
                            })
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "????????????????????????!")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "????????????????????????!" + e)
                    this@SettingActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            ToastUtil.showToast(this@SettingActivity, "??????????????????,???????????????")
                            dismissProgressDialog()
                        }
                    })
                }

            })
    }

    //?????????????????????
    private fun setNewMobile(mobile: String) {
        HttpManager.getInstance()
            .post(Api.SETMobile, NewParameter.getUpdMobile(mobile), object : Subscriber<String>() {
                override fun onNext(result: String?) {
                    //todo???????????????????????????????????????????????????
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
                            user.phone_num = mobile
                            mobileT.text = mobile //??????
                            SpUtil.getIstance().user = user
                            this@SettingActivity.runOnUiThread(object : Runnable {
                                override fun run() {
                                    ToastTool.showToast(this@SettingActivity, "?????????????????????")
                                    mobileNum = mobile
                                    dismissProgressDialog()
                                }
                            })
                        }
                    }
                }

                override fun onCompleted() {
                    LogTool.e("onCompleted", "?????????????????????!")
                }

                override fun onError(e: Throwable?) {
                    LogTool.e("onError", "?????????????????????!" + e)
                    this@SettingActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            ToastUtil.showToast(this@SettingActivity, "?????????????????????,???????????????")
                            dismissProgressDialog()
                        }
                    })
                }

            })
    }

    //?????????????????????
    private fun setNewZfb(ali_pay_account: String) {
        HttpManager.getInstance()
            .post(
                Api.UPDZFB,
                NewParameter.getUpdZfb(ali_pay_account),
                object : Subscriber<String>() {
                    override fun onNext(result: String?) {
                        //todo???????????????????????????????????????????????????
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
                                        ToastTool.showToast(this@SettingActivity, "????????????????????????")
                                        Zfb.text = ali_pay_account
                                        userZfb = ali_pay_account
                                        dismissProgressDialog()
                                    }
                                })
                            }
                        }
                    }

                    override fun onCompleted() {
                        LogTool.e("onCompleted", "??????????????????????????????!")
                    }

                    override fun onError(e: Throwable?) {
                        LogTool.e("onError", "????????????????????????!" + e)
                        this@SettingActivity.runOnUiThread(object : Runnable {
                            override fun run() {
                                ToastUtil.showToast(this@SettingActivity, "????????????????????????,???????????????")
                            }
                        })
                    }

                })
    }

    //??????????????????
    fun setIconUser(path: String) {//bt:Bitmap?,
        //1.??????????????????????????????
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
                                        ToastUtil.showToast(this@SettingActivity, "??????????????????")
                                        if (path != "" && !TextUtils.isEmpty(path)) {
                                            GlideUtil.loadHeadImage(
                                                this@SettingActivity,
                                                path,
                                                userImage
                                            )
                                        }

                                        val u = SpUtil.getIstance().user
                                        u.avatar = avatar
                                        SpUtil.getIstance().user = u  //??????
                                    }
                                })
                            }
                        }
                    }

                    override fun onCompleted() {
                        LogTool.e("onCompleted", "??????????????????!")
                    }

                    override fun onError(e: Throwable?) {
                        LogTool.e("onError", "??????????????????!" + e)
                        this@SettingActivity.runOnUiThread(object : Runnable {
                            override fun run() {
                                dismissProgressDialog()
                                ToastUtil.showToast(this@SettingActivity, "??????????????????,???????????????")
                            }
                        })
                    }

                })

    }
}
