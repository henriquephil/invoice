package dev.hphil.invoice.gateway.session

object SessionKeys {
    fun session(sessionId: String) = "invoice:gateway:session:$sessionId"
    fun refreshLock(sessionId: String) = "invoice:gateway:refresh-lock:$sessionId"
}