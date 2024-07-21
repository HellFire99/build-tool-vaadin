package nl.buildtool.utils

import nl.buildtool.model.events.MavenLogEvent

object ExtensionFunctions {

    fun logEvent(message: String) {
        // TODO make async
        run {
            GlobalEventBus.eventBus.post(MavenLogEvent(message))
        }
    }

    fun post(event: Any) {
        run {
            GlobalEventBus.eventBus.post(event)
        }
    }
}