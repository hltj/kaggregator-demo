package me.hltj.kaggregator.demo

import io.ktor.application.*
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.jackson
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.async
import java.util.concurrent.atomic.AtomicLong

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

val httpClient = HttpClient {
    install(JsonFeature)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson { }
    }

    install(CallId) {
        header(HttpHeaders.XRequestId)

        val counter = AtomicLong()
        generate { "hltj-me-${counter.incrementAndGet()}" }
    }

    install(CallLogging) {
        callIdMdc("request-id")
    }

    routing {

        get("/atom/forum/posts") {
            call.respond(getPosts(call.parameters))
        }

        get("/atom/user/users") {
            call.respond(getUsers(call.parameters))
        }

        get("/atom/forum/replies") {
            call.respond(getReplies(call.parameters))
        }

        get("/public/forum/posts") {
            val posts = httpClient.get<List<Post>>("http://localhost:8080/atom/forum/posts")

            val authorIds = posts.map { post -> post.authorId }.joinToString(separator = ",")

            val users = httpClient.get<List<User>>("http://localhost:8080/atom/user/users?ids=$authorIds")
                .map { user -> user.id to user }.toMap()

            call.respond(posts.map { post ->
                SimplePost(
                    id = post.id,
                    title = post.title,
                    authorName = users[post.authorId]?.name ?: "无名氏"
                )
            })
        }

        get("/public/forum/posts/{id}") {
            val postId = call.parameters["id"]

            val deferredPost = async {
                httpClient.get<List<Post>>("http://localhost:8080/atom/forum/posts?ids=$postId")[0]
            }

            val deferredReplies = async {
                httpClient.get<List<Reply>>("http://localhost:8080/atom/forum/replies?post_id=$postId")
            }

            val post = deferredPost.await()
            val deferredUser = async {
                httpClient.get<List<User>>("http://localhost:8080/atom/user/users?ids=${post.authorId}")[0]
            }

            val user = deferredUser.await()
            call.respond(
                DetailedPost(
                    id = post.id,
                    author = SimpleUser(user.id, user.name),
                    title = post.title,
                    content = post.content,
                    replies = deferredReplies.await()
                )
            )
        }
    }
}
