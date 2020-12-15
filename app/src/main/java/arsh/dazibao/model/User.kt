package arsh.dazibao.model

data class User(
    val id:Long?=0,
    val name:String,
    val avatar:Attachment?=null,
    val type:UserType,
    val onlyReader:Boolean = false
)
enum class UserType{
    NEUTRAL,
    PROMOTER,
    HATER
}