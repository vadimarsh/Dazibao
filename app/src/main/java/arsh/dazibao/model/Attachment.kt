package arsh.dazibao.model

import arsh.dazibao.SERVER_URL

data class Attachment(val id: String, val mediaType: AttachmentType) {
    val url
        get() = "$SERVER_URL/api/v1/static/$id"

}

enum class AttachmentType {
    IMAGE
}

