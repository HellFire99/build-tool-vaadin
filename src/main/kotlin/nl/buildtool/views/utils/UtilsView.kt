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

    init {
        content?.width = "100%"
        content?.style?.set("flex-grow", "1")

        val footerRow = VerticalLayout()
        footerRow.setId("footerRow")
        footerRow.addClassName(LumoUtility.Gap.MEDIUM)
        footerRow.width = "100%"
        footerRow.height = "min-content"
        footerRow.addClassName(LumoUtility.Gap.XSMALL)
        footerRow.addClassName(LumoUtility.Padding.XSMALL)

        executeButton = Button("Execute")
        executeButton.isDisableOnClick = true
        executeButton.setId("executeButton")
        executeButton.style["flex-grow"] = "1"
        executeButton.addClickListener {
            logEvent("executeButton clicked")
            // disable radio's etc
        }

        executeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        // enable/disable als aan voorwaarden wordt voldaan.

        utilsViewContent.initContent(this)
        utilsViewContent.evaluateExecuteButtonEnabling()

        val bottomRow = HorizontalLayout()
        bottomRow.setId("bottomRow")
        bottomRow.addClassName(LumoUtility.Gap.MEDIUM)
        bottomRow.width = "100%"
        bottomRow.height = "min-content"
        bottomRow.addClassName(LumoUtility.Gap.XSMALL)
        bottomRow.addClassName(LumoUtility.Padding.XSMALL)

        val loggingTextArea = loggingService.setupTextArea(TextArea())
        footerRow.setAlignSelf(FlexComponent.Alignment.CENTER, loggingTextArea)

        footerRow.add(executeButton)
        footerRow.add(loggingTextArea)

        content!!.add(footerRow)
    }


}
