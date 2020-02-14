package com.yokoro

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import io.ktor.sessions.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.generateNonce
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.consumeEach
import java.time.Duration


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.main(testing: Boolean = false) {
    OekakiApplication().apply { main() }
}

class OekakiApplication {
    val server = OekakiServer()

    @KtorExperimentalAPI
    fun Application.main(){
        install(DefaultHeaders)
        install(CallLogging)
        install(WebSockets){
            pingPeriod = Duration.ofMillis(1)
        }
        install(Sessions){
            cookie<OekakiSession>("SESSION")
        }
        install(ContentNegotiation){
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)
            }
        }
        intercept(ApplicationCallPipeline.Features){
            if(call.sessions.get<OekakiSession>() == null){
                call.sessions.set(OekakiSession(generateNonce()))
            }
        }

        routing() {
            webSocket("/ws") {
                val session = call.sessions.get<OekakiSession>()

                if(session == null){
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                    return@webSocket
                }

                server.memberJoin(session.id, this)

                try {
                    incoming.consumeEach { frame ->
                        if(frame is Frame.Text){
                            server.message(session.id, frame.readText())
                        }
                    }
                } finally {
                    server.memberLeft(session.id, this)
                }

                static {
                    defaultResource("index.html", "web")
                    resources("web")
                }

            }
        }
    }

    data class OekakiSession(val id: String)

}