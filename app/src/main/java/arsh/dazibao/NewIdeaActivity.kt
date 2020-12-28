package arsh.dazibao

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import arsh.dazibao.model.Attachment
import arsh.dazibao.model.Idea
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_idea.*
import kotlinx.android.synthetic.main.activity_new_idea.attachPhotoIv
import kotlinx.android.synthetic.main.activity_new_idea.mainTb
import kotlinx.android.synthetic.main.activity_new_idea.photoSw
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.launch
import retrofit2.Response
import splitties.toast.toast
import java.io.IOException

class NewIdeaActivity : AppCompatActivity() {
    private companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val GALLERY_REQUEST = 2
    }

    private var dialog: ProgressDialog? = null
    private var idSource: Long = -1L
    private var attachmentModel: Attachment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_idea)
        setSupportActionBar(mainTb)

        if (intent.extras != null) {
            idSource = intent.extras!!.getLong("id")
            attachPhotoIv.visibility = View.GONE
        } else {
            attachPhotoIv.visibility = View.VISIBLE
        }

        attachPhotoIv.setOnClickListener {
            dispatchTakePictureIntent()
        }
        createIdeaBtn.setOnClickListener {
            lifecycleScope.launch {

                dialog = ProgressDialog(this@NewIdeaActivity).apply {
                    setMessage(this@NewIdeaActivity.getString(R.string.please_wait))
                    setTitle(R.string.txt_create_new_idea)
                    setCancelable(false)
                    setProgressBarIndeterminate(true)
                    show()
                }
                try {
                    val result: Response<Idea>
                    if (attachmentModel != null && contentEdt.text.toString().isNotEmpty()) {
                        result = if (linkEdt.text.toString().isNotEmpty()) {
                            App.repository.addNewIdea(
                                content = contentEdt.text.toString(),
                                attid = attachmentModel!!.id,
                                link = linkEdt.text.toString()
                            )
                        } else {
                            App.repository.addNewIdea(
                                content = contentEdt.text.toString(),
                                attid = attachmentModel!!.id
                            )
                        }
                        if (result.isSuccessful) {
                            handleSuccessfullResult()
                        } else {
                            handleFailedResult()
                        }
                    } else {
                        toast(getString(R.string.msg_err_content_att))
                    }
                } catch (e: IOException) {
                    handleFailedResult()
                } finally {
                    dialog?.dismiss()
                }

            }
        }
    }

    private fun handleSuccessfullResult() {
        toast(R.string.msg_idea_created_success)
        finish()
    }

    private fun handleFailedResult() {
        toast(R.string.msg_err)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            sendImage(imageBitmap)

        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            val selectedImage = data?.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
            sendImage(imageBitmap)
        }
    }

    private fun sendImage(imageBitmap: Bitmap?) {
        imageBitmap?.let {
            lifecycleScope.launch {

                dialog = createProgressDialog()
                val imageUploadResult = App.repository.upload(it)
                dialog?.dismiss()
                if (imageUploadResult.isSuccessful) {
                    imageUploaded()
                    attachmentModel = imageUploadResult.body()

                    toast(getString(R.string.msg_sucs_send_img))
                } else {
                    toast(getString(R.string.msg_err_cant_send_img))
                }
            }
        }
    }

    private fun imageUploaded() {
        transparetAllIcons()
    }

    private fun transparetAllIcons() {
        attachPhotoIv.setImageResource(R.drawable.ic_photo)
    }

    private fun createProgressDialog(): ProgressDialog? {
        return ProgressDialog(this@NewIdeaActivity).apply {
            setMessage(this@NewIdeaActivity.getString(R.string.please_wait))
            setTitle(R.string.txt_create_new_idea)
            setCancelable(false)
            setProgressBarIndeterminate(true)
            show()
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
                    startActivityForResult(
                        takePictureIntent,
                        REQUEST_IMAGE_CAPTURE
                    )
                }
            }
        }
    }


}