package nl.buildtool.views.utils

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dependency.Uses
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Menu
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.lumo.LumoUtility
import nl.buildtool.LoggingService
import nl.buildtool.views.MainLayout
import nl.buildtool.views.build.PomFileDataProvider

@PageTitle("Utils")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0.0)
@Route(value = "utils", layout = MainLayout::class)
@Uses(
    Icon::class
)
class UtilsView(
    pomFileDataProvider: PomFileDataProvider,
    private val loggingService: LoggingService
) : Composite<VerticalLayout?>() {

    init {
        val mainRow = HorizontalLayout()
        mainRow.setId("mainRow")

        val leftColumn = VerticalLayout()
        leftColumn.setId("leftColumn")

        val prefixPomsButton = Button("Prefix pom files")
        val updateDependenciesButton = Button("Update  dependencies")
        val layoutColumn3 = VerticalLayout()
        layoutColumn3.setId("layoutColumn3")

        val h5 = H5()
        val innerRow = HorizontalLayout()
        innerRow.setId("innerRow")
        innerRow.minHeight = "490px"

        val middleColumn = VerticalLayout()
        middleColumn.setId("middleColumn")

        val pomFileSelectRadio = RadioButtonGroup<String>()
        val customOrAutoDetectPrefixRadio = RadioButtonGroup<String>()
        val textField = TextField()
        val rightColumn = VerticalLayout()
        rightColumn.setId("rightColumn")

        val treeGrid = pomFileDataProvider.createTreeGrid()

        val footerRow = HorizontalLayout()
        footerRow.setId("footerRow")

        val executeButton = Button()
        content!!.addClassName(LumoUtility.Gap.XSMALL)
        content!!.addClassName(LumoUtility.Padding.XSMALL)
        content!!.width = "100%"
        content!!.style["flex-grow"] = "1"
        mainRow.addClassName(LumoUtility.Gap.XSMALL)
        mainRow.width = "100%"
        mainRow.style["flex-grow"] = "1"
        leftColumn.addClassName(LumoUtility.Padding.XSMALL)
        leftColumn.style["flex-grow"] = "1"
        leftColumn.width = "190px"
        prefixPomsButton.width = "min-content"
        prefixPomsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        updateDependenciesButton.width = "min-content"
        updateDependenciesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        layoutColumn3.addClassName(LumoUtility.Gap.XSMALL)
        layoutColumn3.addClassName(LumoUtility.Padding.XSMALL)
        layoutColumn3.width = "100%"
        layoutColumn3.style["flex-grow"] = "1"
        h5.text = "Prefix pom files"
        h5.width = "max-content"
        innerRow.setWidthFull()
        layoutColumn3.setFlexGrow(1.0, innerRow)
        innerRow.addClassName(LumoUtility.Gap.XSMALL)
        innerRow.addClassName(LumoUtility.Padding.XSMALL)
        innerRow.width = "100%"
        innerRow.style["flex-grow"] = "1"
        middleColumn.setHeightFull()
        innerRow.setFlexGrow(1.0, middleColumn)
        middleColumn.addClassName(LumoUtility.Gap.XSMALL)
        middleColumn.addClassName(LumoUtility.Padding.XSMALL)
        middleColumn.width = "min-content"
        middleColumn.style["flex-grow"] = "1"
        pomFileSelectRadio.label = "Choose which pom files to update"
        pomFileSelectRadio.width = "min-content"
        pomFileSelectRadio.setItems("All in workspace", "Selection")
        pomFileSelectRadio.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)

        customOrAutoDetectPrefixRadio.label = "Auto-detect or custom prefix"
        customOrAutoDetectPrefixRadio.width = "min-content"
        customOrAutoDetectPrefixRadio.setItems("Auto-detect", "Custom prefix")
        customOrAutoDetectPrefixRadio.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
        textField.label = "Prefix"
        textField.width = "100%"
        rightColumn.setHeightFull()
        innerRow.setFlexGrow(1.0, rightColumn)
        rightColumn.addClassName(LumoUtility.Gap.XSMALL)
        rightColumn.addClassName(LumoUtility.Padding.XSMALL)
        rightColumn.width = "100%"
        rightColumn.style["flex-grow"] = "1"

        footerRow.addClassName(LumoUtility.Gap.MEDIUM)
        footerRow.width = "100%"
        footerRow.height = "min-content"
        executeButton.text = "Execute"
        executeButton.style["flex-grow"] = "1"
        executeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        content!!.add(mainRow)
        mainRow.add(leftColumn)
        leftColumn.add(prefixPomsButton)
        leftColumn.add(updateDependenciesButton)
        mainRow.add(layoutColumn3)
        layoutColumn3.add(h5)
        layoutColumn3.add(innerRow)
        innerRow.add(middleColumn)
        middleColumn.add(pomFileSelectRadio)
        middleColumn.add(customOrAutoDetectPrefixRadio)
        middleColumn.add(textField)
        innerRow.add(rightColumn)
        rightColumn.add(treeGrid)
        content!!.add(footerRow)
        footerRow.add(executeButton)
    }

    private fun addMainRowToContent() {
        val mainRow = HorizontalLayout()
        mainRow.setId("mainRow")
        mainRow.addClassName(LumoUtility.Gap.XSMALL)
        mainRow.width = "100%"
        mainRow.style["flex-grow"] = "1"
    }
}
