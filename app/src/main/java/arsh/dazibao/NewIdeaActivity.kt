package arsh.dazibao

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.lifecycleScope
import arsh.dazibao.model.Attachment
import arsh.dazibao.model.Idea
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_idea.*
import kotlinx.android.synthetic.main.activity_new_idea.mainTb
import kotlinx.coroutines.launch
import retrofit2.Response
import splitties.toast.toast
import java.io.IOException

class NewIdeaActivity : AppCompatActivity() {
    private var dialog: ProgressDialog? = null
    private var idSource: Long = -1L
    val REQUEST_IMAGE_CAPTURE = 1
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


                // Показываем крутилку
                dialog = ProgressDialog(this@NewIdeaActivity).apply {
                    setMessage(this@NewIdeaActivity.getString(R.string.please_wait))
                    setTitle(R.string.create_new_idea)
                    setCancelable(false)
                    setProgressBarIndeterminate(true)
                    show()
                }
                // Обворачиваем в try catch, потому что возможны ошибки при соединении с сетью
                try {
                    val result: Response<Idea>
                       if (attachmentModel != null) {
                            result = App.repository.addNewIdea(
                                content = contentEdt.text.toString(),
                                attid = attachmentModel?.id,
                                link = linkEdt.text.toString()
                            )
                            attachmentModel == null
                        } else {
                            result = App.repository.addNewIdea(
                                contentEdt.text.toString()
                            )
                        }
                    if (result.isSuccessful) {
                        handleSuccessfullResult()
                    } else {
                        handleFailedResult()
                    }
                } catch (e: IOException) {
                    // обрабатываем ошибку
                    handleFailedResult()
                } finally {
                    // закрываем диалог
                    dialog?.dismiss()
                }

            }
        }
    }

    private fun handleSuccessfullResult() {
        toast(R.string.idea_created_success)
        finish()
    }

    private fun handleFailedResult() {
        toast(R.string.error_occured)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            imageBitmap?.let {
                lifecycleScope.launch {

                    dialog = createProgressDialog()
                    val imageUploadResult = App.repository.upload(it)
                    dialog?.dismiss()
                    if (imageUploadResult.isSuccessful) {
                        imageUploaded()
                        attachmentModel = imageUploadResult.body()
                    } else {
                        toast("Can't upload image")
                    }
                }
            }
        }
    }

    private fun imageUploaded() {
        transparetAllIcons()
        // Показываем красную галочку над фото
        //attachPhotoDoneImg.visibility = View.VISIBLE
    }

    private fun transparetAllIcons() {
        attachPhotoIv.setImageResource(R.drawable.ic_photo)
    }

    private fun createProgressDialog(): ProgressDialog? {
        return ProgressDialog(this@NewIdeaActivity).apply {
            setMessage(this@NewIdeaActivity.getString(R.string.please_wait))
            setTitle(R.string.create_new_idea)
            setCancelable(false)
            setProgressBarIndeterminate(true)
            show()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
}