package arsh.dazibao

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.launch
import splitties.toast.toast
import java.io.IOException

class SettingsActivity : AppCompatActivity() {
    private companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val GALLERY_REQUEST = 2
    }

    private var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(mainTb)

        attachPhotoIv.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun setUserAuth(token: String) =
        getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).edit {
            putString(AUTHENTICATED_SHARED_KEY, token)
        }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val dialog = ProgressDialog(this@SettingsActivity).apply {
                setMessage(this@SettingsActivity.getString(R.string.please_wait))
                setTitle(R.string.loading_posts)
                setCancelable(false)
                setProgressBarIndeterminate(true)
                show()
            }
            val response = App.repository.getMe()
            try {

                if (response.isSuccessful) {
                    val user = response.body()!!
                    authorNameTv.text = user.username
                    if(user.onlyReader){
                        rankTv.text = "Только просмотр и голосование"
                    }else{
                        rankTv.text = "Полноправный автор"}
                    if (user.avatar != null) {
                        loadImage(avatarIv, user.avatar.url)
                    }
                } else {
                    toast(R.string.msg_err)
                }
            } catch (e: IOException) {
                toast(R.string.msg_connection_err)
            } finally {
                dialog.dismiss()
            }
        }

        changesConfirmBut.setOnClickListener {
            val oldPassword = et_old_pswd.text.toString()
            val newPassword = et_pswd.text.toString()
            val confirmPassword = et_new_Pswd.text.toString()
            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                toast(R.string.msg_paswd_invalid_err)
            } else if (newPassword != confirmPassword) {
                toast(R.string.msg_difpsw_err)
            } else {
                lifecycleScope.launch {
                    dialog = ProgressDialog(this@SettingsActivity).apply {
                        setMessage(this@SettingsActivity.getString(R.string.please_wait))
                        setTitle(R.string.loading_posts)
                        setCancelable(false)
                        setProgressBarIndeterminate(true)
                        show()
                    }
                    try {
                        val response = App.repository.changePswd(oldPassword, newPassword)
                        if (response.isSuccessful) {
                            setUserAuth(response.body()!!.token)
                            toast("Пароль изменен успешно")
                        } else {
                            toast(R.string.msg_err)
                        }
                    } catch (e: Exception) {
                        toast(R.string.msg_connection_err)
                    } finally {
                        dialog?.dismiss()
                    }
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        if (photoSw.isChecked) {
            Intent(Intent.ACTION_PICK).also { photoPickerIntent ->
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST)
            }

        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun loadImage(photoImg: ImageView, url: String) {
        Glide.with(photoImg.context)
            .load(url)
            .into(photoImg)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            loadImageToServer(imageBitmap)
        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
            loadImageToServer(imageBitmap)
        }
    }

    private fun loadImageToServer(imageBitmap: Bitmap?) {
        imageBitmap?.let {
            lifecycleScope.launch {
                try {
                    dialog = ProgressDialog(this@SettingsActivity).apply {
                        setMessage(this@SettingsActivity.getString(R.string.please_wait))
                        setTitle(R.string.loading_posts)
                        setCancelable(false)
                        setProgressBarIndeterminate(true)
                        show()
                    }
                    val imageUploadResult = App.repository.upload(it)

                    if (imageUploadResult.isSuccessful) {
                        toast("Изображение успешно загружено")
                        val attachment = imageUploadResult.body()!!
                        loadImage(avatarIv, attachment.url)
                        val attachImageToUser = App.repository.setAvatar(attachment)
                        if (attachImageToUser.isSuccessful) {
                            toast("Аватар установлен")
                        }
                    } else {
                        toast(R.string.msg_err)
                    }
                } catch (e: IOException) {
                    toast(R.string.msg_connection_err)
                } finally {
                    dialog?.dismiss()
                }
            }
        }
    }


}
