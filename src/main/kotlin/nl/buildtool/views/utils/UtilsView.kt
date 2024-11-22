package nl.buildtool.views.utils

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dependency.Uses
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.router.Menu
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.lumo.LumoUtility
import nl.buildtool.model.UpdatePomsParameters
import nl.buildtool.model.UtilsMode
import nl.buildtool.services.LoggingService
import nl.buildtool.utils.ExtensionFunctions.logEvent
import nl.buildtool.utils.PomFilePrefixExecutor
import nl.buildtool.utils.UpdateDependenciesExecutor
import nl.buildtool.views.MainLayout
import nl.buildtool.views.components.ProgressBarIndeterminate
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture


@PageTitle("Utils")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0.0)
@Route(value = "utils", layout = MainLayout::class)
@Uses(
    Icon::class
     )
class UtilsView(utilsViewContent: UtilsViewContent,
                loggingService: LoggingService,
                val pomFilePrefixExecutor: PomFilePrefixExecutor,
                val updateDependenciesExecutor: UpdateDependenciesExecutor) : Composite<VerticalLayout?>() {
    private val logger = LoggerFactory.getLogger(UtilsView::class.java)
    var executeButton: Button
    var progressBar: ProgressBarIndeterminate

    init {
        content?.setId("UtilsViewContent")
        content?.width = "100%"
        content?.style?.set("flex-grow", "1")
        content?.style?.set("gap", "0")

        val footerRow = VerticalLayout()
        footerRow.setId("footerRow")
        footerRow.width = "100%"
        footerRow.height = "min-content"
        footerRow.style?.set("gap", "0")

        progressBar = ProgressBarIndeterminate()

        executeButton = Button("Execute")
        executeButton.setId("executeButton")
        executeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        executeButton.isDisableOnClick = true
        executeButton.width = "181px"
        executeButton.addClickListener {
            when (utilsViewContent.utilsMode) {
                UtilsMode.UPDATE_POM_VERSIONS -> updatePomVersions(it, utilsViewContent)
                UtilsMode.UPDATE_DEPENDENCIES -> updateDepencencies(it, utilsViewContent)
            }
        }

        val buttonRow = HorizontalLayout()
        buttonRow.setId("buttonRow")
        buttonRow.width = "100%"
        buttonRow.height = "min-content"
        buttonRow.style?.set("gap", "0")

        buttonRow.add(executeButton)
        buttonRow.add(progressBar)

        val bottomRow = HorizontalLayout()
        bottomRow.setId("bottomRow")
        bottomRow.addClassName(LumoUtility.Gap.MEDIUM)
        bottomRow.width = "100%"
        bottomRow.height = "min-content"
        bottomRow.addClassName(LumoUtility.Gap.XSMALL)
        bottomRow.addClassName(LumoUtility.Padding.XSMALL)

        val loggingTextArea = loggingService.setupTextArea(
            loggingTextArea = TextArea()
                                                          )
        footerRow.setAlignSelf(FlexComponent.Alignment.CENTER, loggingTextArea)

        footerRow.add(buttonRow)
        footerRow.add(loggingTextArea)

        utilsViewContent.initContent(this)
        utilsViewContent.evaluateExecuteButtonEnabling()

        content!!.add(footerRow)
    }

    private fun updateDepencencies(event: ClickEvent<Button>,
                                   content: UtilsViewContent) {

        val ui: UI = event.source.ui.orElseThrow()
        val startJobFuture = CompletableFuture.supplyAsync {
            ui.access {
                progressBar.isVisible = true
                executeButton.isVisible = false
            }
        }
        val jobExecution = updateDependenciesExecutor.executeJob(ui, this)

        startJobFuture.thenRun {
            logEvent("Execute button clicked. Update dependencies.")
        }
        jobExecution?.thenRun {
            logEvent("Job done!")
        }
    }

    private fun updatePomVersions(event: ClickEvent<Button>,
                                  utilsViewContent: UtilsViewContent) {
        val ui: UI = event.source.ui.orElseThrow()
        val startJobFuture = CompletableFuture.supplyAsync {
            ui.access {
                progressBar.isVisible = true
                executeButton.isVisible = false
            }
        }

        val jobExecutionParameter = UpdatePomsParameters(
            autoDetectCustomOrReset = utilsViewContent.autoDetectCustomOrResetRadio.value,
            pomFileSelectRadioValue = utilsViewContent.pomFileSelectRadio.value,
            customPrefixTextfield = utilsViewContent.customPrefixTextfield.value,
            selectedPomFiles = utilsViewContent.pomFileSelectionGrid.selectedItems
                                                        )
        val jobExecution = pomFilePrefixExecutor.executeJob(ui, this, jobExecutionParameter)

        startJobFuture.thenRun {
            logEvent("Execute button clicked. Update pom versions.")
        }
        jobExecution?.thenRun {
            logEvent("Job done!")
        }
    }

}
