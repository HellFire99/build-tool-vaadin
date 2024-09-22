package nl.buildtool.views.utils

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dependency.Uses
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Menu
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.lumo.LumoUtility
import nl.buildtool.views.MainLayout
import org.slf4j.LoggerFactory

@PageTitle("Utils")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0.0)
@Route(value = "utils", layout = MainLayout::class)
@Uses(
    Icon::class
)
class UtilsView(utilsViewContent: UtilsViewContent) : Composite<VerticalLayout?>() {
    private val logger = LoggerFactory.getLogger(UtilsView::class.java)

    init {
        val footerRow = HorizontalLayout()
        footerRow.setId("footerRow")
        footerRow.addClassName(LumoUtility.Gap.MEDIUM)
        footerRow.width = "100%"
        footerRow.height = "min-content"

        val executeButton = Button("Execute")
        executeButton.setId("executeButton")
        executeButton.style["flex-grow"] = "1"
        executeButton.addClickListener {
            logger.info("executeButton")
        }
        executeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)

        utilsViewContent.initContent(this)
        footerRow.add(executeButton)
        content!!.add(footerRow)
    }

}
