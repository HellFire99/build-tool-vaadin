package nl.buildtool.services

import com.google.common.eventbus.Subscribe
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.textfield.TextArea
import nl.buildtool.model.events.MavenLogEvent
import nl.buildtool.utils.GlobalEventBus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service

@Service
class LoggingService : InitializingBean {
    private val logger = LoggerFactory.getLogger(LoggingService::class.java)
    private lateinit var loggingTextArea: TextArea
    private lateinit var ui: UI

    @Subscribe
    fun subscribe(event: MavenLogEvent) {
        logger.info(event.text)
        ui.access {
            loggingTextArea.value = "${
                if (loggingTextArea.value.isNullOrEmpty()) {
                    event.text
                } else {
                    loggingTextArea.value + "\n" + event.text
                }
            } "
            loggingTextArea.scrollToEnd()
        }
    }

    fun clean() {
        ui.access {
            loggingTextArea.value = ""
        }
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
        loggingTextArea.maxHeight = "calc(100vh - 665px)"
        this.loggingTextArea = loggingTextArea
        this.ui = UI.getCurrent()
        return loggingTextArea
    }

    override fun afterPropertiesSet() {
        GlobalEventBus.eventBus.register(this)
    }
}