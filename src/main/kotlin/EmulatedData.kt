package me.hltj.kaggregator.demo

import io.ktor.http.Parameters
import kotlinx.coroutines.delay

val sampleReplies = listOf(
    Reply(10001, 1001, "just reply"),
    Reply(10002, 1001, "hello too"),
    Reply(10003, 1001, "欢迎"),
    Reply(10004, 1002, "赞，终于发布了"),
    Reply(10005, 1002, "可以用契约了"),
    Reply(10006, 1003, "太好了，后端也能用上 Kotlin 了"),
    Reply(10007, 1003, "这可是 Kotlin 语言开发的框架"),
    Reply(10008, 1003, "还是 JetBrains 官方出品"),
    Reply(10009, 1002, "期待 SAM")
)

val samplePosts = listOf(
    Post(1001, 3, "第一帖", "Hello, world"),
    Post(1002, 1, "Kotlin 1.3 发布", "Kotlin 1.3 正式发布了"),
    Post(1003, 5, "Ktor 框架简介", "用 Kotlin 开发互联应用")

)

val sampleUsers = listOf(
    User(1, "zhang3", "张三"),
    User(2, "li4", "李四"),
    User(3, "wang2", "王二"),
    User(4, "zhao1", "赵大"),
    User(5, "xiaotaoqi", "小淘气")
)

suspend fun getPosts(parameters: Parameters) : List<Post> {
    return delayFilter(samplePosts, parameters)
}


suspend fun getUsers(parameters: Parameters) : List<User> {
    return delayFilter(sampleUsers, parameters)
}

suspend fun getReplies(parameters: Parameters): List<Reply> {
    delay(100L)

    val postId = parameters["post_id"]?.toIntOrNull() ?: return emptyList()

    return sampleReplies.filter { it.postId == postId }
}

private suspend fun <T: HasId> delayFilter(
    list: List<T>,
    parameters: Parameters
): List<T> {
    delay(100L)

    val ids = parameters["ids"]?.split(',')?.map { s -> s.toInt() } ?: listOf()

    return if (ids.isEmpty()) list else list.filter { t -> t.id in ids }
}
