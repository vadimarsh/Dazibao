package arsh.dazibao.model

data class Action(
    val user: User,
    val date: Long,
    val type: ActionType)

enum class ActionType{
    LIKE, DISLIKE
}