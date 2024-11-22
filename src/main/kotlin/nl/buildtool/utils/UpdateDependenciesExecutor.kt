package nl.buildtool.utils

import com.vaadin.flow.component.UI
import nl.buildtool.views.utils.UtilsView
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture


@Service
class UpdateDependenciesExecutor(private val updatePomVersionUtil: UpdatePomVersionUtil) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Async
    fun executeJob(
        ui: UI,
        utilsView: UtilsView): CompletableFuture<Any>? = CompletableFuture.supplyAsync {
        try {
            Thread.sleep(2000)

            // TODO implementeer update dependencies

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