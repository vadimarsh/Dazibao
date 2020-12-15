package arsh.dazibao

import android.graphics.Bitmap
import arsh.dazibao.dto.AuthRequestParams
import arsh.dazibao.dto.IdeaRequestDto
import arsh.dazibao.dto.RegistrationRequestParams
import arsh.dazibao.model.Attachment
import com.example.arshkotlin9.api.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.ByteArrayOutputStream

class Repository(private val api: API) {

//    private val retrofit: Retrofit by lazy {
//        val client = OkHttpClient.Builder()
//            .addInterceptor(App.authTokenInterceptor)
//            .build()
//        Retrofit.Builder()
//            .baseUrl(SERVER_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }


    suspend fun authenticate(login: String, password: String): Response<Token> {
        val token: Response<Token> =
            api.authenticate(AuthRequestParams(username = login, password = password))
        return token
    }

    suspend fun register(login: String, password: String) =
        api.register(
            RegistrationRequestParams(
                login,
                password
            )
        )

    suspend fun getPosts() =
        api.getAllPosts()

    suspend fun getPostsRecent() =
        api.getRecent()

    suspend fun getPostsAfter(id: Long) =
        api.after(id)

    suspend fun getPostsBefore(id: Long) =
        api.before(id)

    suspend fun likePost(id: Long) =
        api.likePost(id)

    suspend fun dislikePost(id: Long) =
        api.dislikePost(id)

    suspend fun addNewPost(content: String, attid: String?) =
        api.createPost(IdeaRequestDto(text = content, attachmentImage = attid,attachmentLink = ""))

    suspend fun addNewPost(content: String) = api.createPost(IdeaRequestDto(text = content,attachmentImage = "",attachmentLink = ""))

    suspend fun upload(bitmap: Bitmap): Response<Attachment> {
        // Создаем поток байтов
        val bos = ByteArrayOutputStream()
        // Помещаем Bitmap в качестве JPEG в этот поток
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val reqFIle =
            // Создаем тип медиа и передаем массив байтов с потока
            RequestBody.create(MediaType.parse("image/jpeg"), bos.toByteArray())
        val body =
        // Создаем multipart объект, где указываем поле, в котором
            // содержатся посылаемые данные, имя файла и медиафайл
            MultipartBody.Part.createFormData("file", "image.jpg", reqFIle)
        return api.uploadImage(body)
    }
}
