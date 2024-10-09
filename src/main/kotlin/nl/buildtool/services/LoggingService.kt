package nl.buildtool.services

import com.google.common.eventbus.Subscribe
import com.vaadin.flow.component.textfield.TextArea
import nl.buildtool.model.events.MavenLogEvent
import nl.buildtool.utils.GlobalEventBus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
class LoggingService : InitializingBean {
    private val logger = LoggerFactory.getLogger(LoggingService::class.java)
    private lateinit var loggingTextArea: TextArea

    @Subscribe
    fun updateTextArea(event: MavenLogEvent) {
        logger.info(event.text)
        loggingTextArea.value = "${loggingTextArea.value}\n ${event.text}\n"
    }

    private fun initialiseer() {
        GlobalEventBus.eventBus.register(this)
    }

    fun setupTextArea(loggingTextArea: TextArea): TextArea {
        loggingTextArea.setId("logTextArea")
        loggingTextArea.setWidthFull()
        loggingTextArea.minHeight = "100px"
        loggingTextArea.maxHeight = "150px"
        loggingTextArea.label = "Log"
        loggingTextArea.placeholder = "<empty>"
        loggingTextArea.isClearButtonVisible = true
        loggingTextArea.isReadOnly = true
        loggingTextArea.style["flex-grow"] = "1"
        loggingTextArea.height = "100%"
        this.loggingTextArea = loggingTextArea
        return loggingTextArea
    }

    override fun afterPropertiesSet() {
        initialiseer()
    }
}