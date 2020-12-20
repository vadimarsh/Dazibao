package arsh.dazibao.dto

data class IdeaRequestDto(
    val id:Long,
    val content: String,
    val attachmentImage: String?,
    val attachmentLink: String?
)
