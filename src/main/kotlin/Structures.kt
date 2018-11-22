package me.hltj.kaggregator.demo

interface HasId {
    val id: Int
}

data class Post(
    override val id: Int,
    val authorId: Int,
    val title: String,
    val content: String
): HasId

data class User(
    override val id: Int,
    val login: String,
    val name: String
): HasId

data class Reply(
    override val id: Int,
    val postId: Int,
    val content: String
): HasId

data class SimpleUser(
    val id: Int,
    val name: String
)

data class SimplePost(
    val id: Int,
    val authorName: String,
    val title: String
)

data class DetailedPost(
    val id: Int,
    val author: SimpleUser,
    val title: String,
    val content: String,
    val replies: List<Reply>
)
