package nl.buildtool.views.utils

import com.vaadin.flow.component.Composite
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
import nl.buildtool.services.LoggingService
import nl.buildtool.utils.ExtensionFunctions.logEvent
import nl.buildtool.views.MainLayout
import nl.buildtool.views.components.ProgressBarIndeterminate
import org.slf4j.LoggerFactory


@PageTitle("Utils")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0.0)
@Route(value = "utils", layout = MainLayout::class)
@Uses(
    Icon::class
)
class UtilsView(
    utilsViewContent: UtilsViewContent,
    loggingService: LoggingService
) : Composite<VerticalLayout?>() {
    private val logger = LoggerFactory.getLogger(UtilsView::class.java)
    var executeButton: Button
    private var progressBar: ProgressBarIndeterminate

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
        progressBar.setId("progressBar")

        executeButton = Button("Execute")
        executeButton.setId("executeButton")
        executeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        executeButton.isDisableOnClick = true
        executeButton.width = "181px"
        executeButton.addClickListener {
            logEvent("executeButton clicked")
            // disable radio's etc
            this.progressBar.isVisible = true
            this.executeButton.isVisible = false
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

        val loggingTextArea = loggingService.setupTextArea(TextArea())
        footerRow.setAlignSelf(FlexComponent.Alignment.CENTER, loggingTextArea)

        footerRow.add(buttonRow)
        footerRow.add(loggingTextArea)

        utilsViewContent.initContent(this)
        utilsViewContent.evaluateExecuteButtonEnabling()

        content!!.add(footerRow)
    }


}
