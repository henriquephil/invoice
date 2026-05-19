package dev.hphil.invoice.auth

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testAuthHealth() = testApplication {
        application {
            module()
        }
        client.get("/auth/health").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("""{"status":"UP"}""", bodyAsText())
        }
    }
}
