package arsh.dazibao.model

import arsh.dazibao.SERVER_URL

data class Attachment(val id: String, val mediaType: MediaType) {
    val url
        get() = "$SERVER_URL/api/v1/static/$id"

}

enum class MediaType {
    IMAGE
}

