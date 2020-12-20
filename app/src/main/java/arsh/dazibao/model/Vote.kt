package arsh.dazibao.model

data class Vote(
    val date: String,
    val authorId: Long,
    val authorName: String,
    //val status: UserStatus,
    val action: VoteType,
    val avatar: Attachment?)

enum class VoteType{
    LIKE, DISLIKE
}