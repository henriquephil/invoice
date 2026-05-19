package dev.hphil.invoice.auth.exception

class AuthenticationFailedException(reason: String): RuntimeException(reason) {
}