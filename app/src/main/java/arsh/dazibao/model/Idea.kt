package arsh.dazibao.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Idea(
    val id: Long,
    val authorName: String,
    val authorId: Long,
    val avatar: Attachment? = null,
    val created: String,
    val content: String? = null,
    var likes: Int = 0,
    var dislikes: Int = 0,
    var likedByMe: Boolean = false,
    var dislikedByMe: Boolean = false,
    val link: String? = null,
    val attachment: Attachment? = null
)
{
    var likeActionPerforming = false
    var disLikeActionPerforming = false
    fun updateLikes(updatedIdea: Idea) {
        if (id != updatedIdea.id) throw IllegalAccessException("Ids are different")
        likes = updatedIdea.likes
        likedByMe = updatedIdea.likedByMe
    }
    fun updateDisLikes(updatedIdea: Idea) {
        if (id != updatedIdea.id) throw IllegalAccessException("Ids are different")
        dislikes = updatedIdea.dislikes
        dislikedByMe = updatedIdea.dislikedByMe

    }
}