package nl.buildtool.utils

import com.vaadin.flow.component.UI
import nl.buildtool.model.RADIO_VALUE_RESET_POMS
import nl.buildtool.model.UpdatePomsParameters
import nl.buildtool.views.utils.UtilsView
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class PomFilePrefixExecutor(private val updatePomVersionUtil: UpdatePomVersionUtil) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Async
    fun executeJob(
        ui: UI,
        utilsView: UtilsView,
        jobExecutionParameter: UpdatePomsParameters
    ): CompletableFuture<Any>? = CompletableFuture.supplyAsync {
        try {
            if (jobExecutionParameter.autoDetectCustomOrReset == RADIO_VALUE_RESET_POMS) {
                updatePomVersionUtil.resetPoms(jobExecutionParameter)
            } else {
                updatePomVersionUtil.updatePoms(jobExecutionParameter)
            }

            ui.access {
                utilsView.progressBar.isVisible = false
                utilsView.executeButton.isVisible = true
                utilsView.executeButton.isEnabled = true
            }
        } catch (e: InterruptedException) {
            logger.error(e.message, e)
        }
    }
}