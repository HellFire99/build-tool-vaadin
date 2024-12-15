package nl.buildtool.views.components

import com.vaadin.flow.component.button.Button
import nl.buildtool.services.LoggingService

class ButtonCleanLog(val loggingService: LoggingService) : Button() {
    init {
        text = "Clean log"
        setId("buttonCleanLog")
        addClickListener {
            execute()
        }
    }

    private fun execute() {
        loggingService.clean()
    }
}