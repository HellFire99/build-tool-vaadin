package nl.buildtool

import com.google.common.eventbus.Subscribe
import com.vaadin.flow.component.textfield.TextArea
import nl.buildtool.model.events.MavenLogEvent
import nl.buildtool.utils.GlobalEventBus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LoggingService {
    private val logger = LoggerFactory.getLogger(LoggingService::class.java)
    private lateinit var loggingTextArea: TextArea

    fun initialiseer(loggingTextArea: TextArea) {
        this.loggingTextArea = loggingTextArea
        GlobalEventBus.eventBus.register(this)
    }

    @Subscribe
    fun updateTextArea(event: MavenLogEvent) {
        logger.info(event.text)
        loggingTextArea.value = "${loggingTextArea.value}\n ${event.text}\n"
    }
}