package arsh.dazibao

import android.graphics.Bitmap
import arsh.dazibao.dto.AuthRequestParams
import arsh.dazibao.dto.IdeaRequestDto
import arsh.dazibao.dto.PassChangeRequestParams
import arsh.dazibao.dto.RegistrationRequestParams
import arsh.dazibao.model.Attachment
import com.example.arshkotlin9.api.API
import com.example.arshkotlin9.api.Token
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.ByteArrayOutputStream

class Repository(private val api: API) {

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

    suspend fun getIdeasRecent() =
        api.getRecent()

    suspend fun getIdeasAfter(id: Long) =
        api.after(id)

    suspend fun getPostsBefore(id: Long) =
        api.before(id)

    suspend fun likeIdea(id: Long) =
        api.likeIdea(id)

    suspend fun dislikeIdea(id: Long) =
        api.dislikeIdea(id)

    suspend fun addNewIdea(content: String, attid: String, link: String?) =
        api.createIdea(
            IdeaRequestDto(
                id = -1,
                content = content,
                attachmentImage = attid,
                attachmentLink = link
            )
        )

    suspend fun addNewIdea(content: String, attid: String) = api.createIdea(
        IdeaRequestDto(
            id = -1,
            content = content,
            attachmentImage = attid,
            attachmentLink = null
        )
    )

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

    suspend fun getVotes(idIdea: Long) =
        api.getVotes(idIdea)

    suspend fun getIdeasByAuthor(authorId: Long) =
        api.getIdeasByAuthor(authorId)

    suspend fun getMe() = api.getMe()

    suspend fun changePswd(oldPassword: String, newPassword: String) =
        api.changePswd(PassChangeRequestParams(oldPassword, newPassword))

    suspend fun setAvatar(attachment: Attachment) = api.setAvatar(attachment)

    suspend fun firebasePushToken(token: Token): Response<Void> = api.firebasePushToken(token)


}
