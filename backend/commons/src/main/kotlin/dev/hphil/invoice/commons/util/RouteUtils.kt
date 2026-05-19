package dev.hphil.invoice.commons.util

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*


fun Route.find(block: suspend RoutingCall.() -> Any): Route = get {
    call.apply {
        ok(block())
    }
}
fun Route.find(path: String, block: suspend RoutingCall.() -> Any): Route = get(path) {
    call.apply {
        ok(block())
    }
}

fun Route.create(block: suspend RoutingCall.() -> Any): Route = post {
    call.apply {
        respond(HttpStatusCode.Created, block())
    }
}

fun Route.update(block: suspend RoutingCall.() -> Any?): Route = patch {
    call.apply {
        ok(block())
    }
}

fun Route.del(block: suspend RoutingCall.() -> Any?): Route = delete {
    call.apply {
        ok(block())
    }
}

fun Route.action(path: String, block: suspend RoutingCall.() -> Any): Route = post(path) {
    call.apply {
        ok(block())
    }
}

private suspend fun RoutingCall.ok(message: Any?) {
    return when (message) {
        is Unit -> respond(HttpStatusCode.NoContent)
        null -> respond(HttpStatusCode.OK)
        else -> respond(HttpStatusCode.OK, message)
    }
}

inline fun <reified T : Any> Route.routeParam(paramName: String, crossinline build: Route.(selector: RoutingCall.() -> T) -> Unit): Route {
    return route("{$paramName}") {
        build { this.parameters.getOrFail<T>(paramName) }
    }
}
