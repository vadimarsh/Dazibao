package arsh.dazibao.model

data class Idea(
    val id: Long,
    val authorName: String,
    val authorId: Long,
    val avatar: Attachment? = null,
    val created: Int,
    val content: String? = null,
    var likes: Int = 0,
    var dislikes: Int = 0,
    var likedByMe: Boolean = false,
    var dislikedByMe: Boolean = false,
    val link: String? = null,
    val attachment: Attachment? = null
)