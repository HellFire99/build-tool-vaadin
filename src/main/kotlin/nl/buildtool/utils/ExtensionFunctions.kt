package nl.buildtool.utils

import nl.buildtool.model.events.MavenLogEvent
import java.util.concurrent.CompletableFuture

object ExtensionFunctions {

    fun logEvent(message: String) {
        CompletableFuture.supplyAsync {
            GlobalEventBus.eventBus.post(MavenLogEvent(message))
        }
    }

    fun post(event: Any) {
        CompletableFuture.supplyAsync {
            GlobalEventBus.eventBus.post(event)
        }
    }
}