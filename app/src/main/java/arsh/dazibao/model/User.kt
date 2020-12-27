package arsh.dazibao.model

data class User(
    val id:Long?=0,
    val username:String,
    val avatar:Attachment?=null,
    val fbToken:String?=null,
    val onlyReader:Boolean = false
)
