package dev.hphil.invoice.commons.util

import io.ktor.server.application.ApplicationCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.slf4j.MDCContext

fun ApplicationCall.launchDetached(
    block: suspend CoroutineScope.() -> Unit
): Job = application.launch(coroutineContext.minusKey(Job) + MDCContext(), block = block)