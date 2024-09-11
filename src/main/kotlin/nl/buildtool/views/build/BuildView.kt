package nl.buildtool.views.build

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant
import com.vaadin.flow.component.html.Hr
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Menu
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias
import com.vaadin.flow.theme.lumo.LumoUtility
import nl.buildtool.LoggingService
import nl.buildtool.utils.ExtensionFunctions.logEvent
import nl.buildtool.views.MainLayout

@PageTitle("Build")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0.0)
@Route(value = "", layout = MainLayout::class)
@RouteAlias(value = "", layout = MainLayout::class)
class BuildView(
    pomFileDataProvider: PomFileDataProvider,
    private val loggingService: LoggingService
) : Composite<VerticalLayout?>() {
    lateinit var textArea: TextArea

    init {
        val layoutRow = HorizontalLayout()
        layoutRow.setId("layoutRow1")

        val textField = TextField()
        textField.setId("filterTextField")
        val buttonClearFilter = Button("X")
        buttonClearFilter.setId("buttonClearFilter")
        buttonClearFilter.addClickListener {
            logEvent("clearFilterButton clicked")
        }

        val layoutRow2 = HorizontalLayout()
        layoutRow2.setId("layoutRow2")

        val leftColumn = VerticalLayout()
        leftColumn.setId("leftColumn")

        val rightColumn = VerticalLayout()
        rightColumn.setId("rightColumn")

        val layoutRow3 = HorizontalLayout()
        layoutRow3.setId("layoutRow3")

        val layoutColumn4 = VerticalLayout()
        layoutColumn4.setId("layoutColumn4")

        val targetsChackbox = CheckboxGroup<Any?>()
        targetsChackbox.setId("checkboxGroup")

        val layoutColumn5 = VerticalLayout()
        layoutColumn5.setId("layoutColumn5")

        val optionsCheckbox = CheckboxGroup<Any?>()
        optionsCheckbox.setId("checkboxGroup2")

        val hr = Hr()
        val layoutRow4 = HorizontalLayout()
        optionsCheckbox.setId("layoutRow4")

        val pomManipulationRadioGroup = RadioButtonGroup<Any?>()
        pomManipulationRadioGroup.setId("radioGroup")

        val textFieldJiraNr = TextField()
        val buttonUpdatePomFiles = Button("Update pom files")
        buttonUpdatePomFiles.setId("buttonUpdatePomFiles")
        buttonUpdatePomFiles.addClickListener {
            logEvent("buttonUpdatePomFiles clicked")
        }
        val layoutColumn6 = VerticalLayout()
        layoutColumn6.setId("layoutColumn6")

        val layoutRow5 = HorizontalLayout()
        layoutRow5.setId("layoutRow5")

        val buttonBuild = Button("Build")
        val buttonCancel = Button("Cancel")
        val layoutRow6 = HorizontalLayout()
        layoutRow6.setId("layoutRow6")

        val buttonRefresh = Button("Refresh")
        buttonRefresh.setId("buttonRefresh")
        buttonRefresh.addClickListener {
            logEvent("buttonRefresh clicked")
        }

        val buttonCleanLog = Button("Clean log")
        buttonCleanLog.setId("buttonCleanLog")
        buttonCleanLog.addClickListener {
            logEvent("buttonCleanLog clicked")
        }
        val layoutRow7 = HorizontalLayout()
        layoutRow7.setId("layoutRow7")

        setupTextArea()

        content?.addClassName(LumoUtility.Gap.XSMALL)
        content?.addClassName(LumoUtility.Padding.XSMALL)
        content?.width = "100%"
        content?.style?.set("flex-grow", "1")
        layoutRow.addClassName(LumoUtility.Gap.MEDIUM)
        layoutRow.width = "100%"
        layoutRow.height = "min-content"
        textField.label = "Maven projects"
        layoutRow.setAlignSelf(FlexComponent.Alignment.START, textField)
        textField.width = "500px"
        layoutRow.setAlignSelf(FlexComponent.Alignment.END, buttonClearFilter)
        buttonClearFilter.width = "min-content"
        buttonClearFilter.maxWidth = "20px"
        buttonClearFilter.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        layoutRow2.addClassName(LumoUtility.Gap.MEDIUM)
        layoutRow2.width = "100%"
        layoutRow2.style.set("flex-grow", "1")
        leftColumn.addClassName(LumoUtility.Padding.XSMALL)
        leftColumn.width = "100%"
        leftColumn.style.set("flex-grow", "1")
        rightColumn.addClassName(LumoUtility.Gap.XSMALL)
        rightColumn.addClassName(LumoUtility.Padding.XSMALL)
        rightColumn.style.set("flex-grow", "1")
        layoutRow3.setWidthFull()
        rightColumn.setFlexGrow(1.0, layoutRow3)
        rightColumn.width = "25%"
        layoutRow3.addClassName(LumoUtility.Gap.XSMALL)
        layoutRow3.addClassName(LumoUtility.Padding.XSMALL)
        layoutRow3.width = "100%"
        layoutRow3.height = "min-content"
        layoutColumn4.setHeightFull()
        layoutRow3.setFlexGrow(1.0, layoutColumn4)
        layoutColumn4.addClassName(LumoUtility.Gap.XSMALL)
        layoutColumn4.addClassName(LumoUtility.Padding.XSMALL)
        layoutColumn4.width = "100%"
        layoutColumn4.height = "min-content"
        targetsChackbox.label = "Targets"
        targetsChackbox.width = "min-content"

        targetsChackbox.setItems("Clean", "Install", "Package", "Test", "Verify")
        targetsChackbox.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL)
        targetsChackbox.value = setOf("Clean", "Install")
        layoutColumn5.setHeightFull()
        layoutRow3.setFlexGrow(1.0, layoutColumn5)
        layoutColumn5.addClassName(LumoUtility.Gap.XSMALL)
        layoutColumn5.addClassName(LumoUtility.Padding.XSMALL)
        layoutColumn5.width = "100%"
        layoutColumn5.height = "min-content"
        optionsCheckbox.label = "Options"
        optionsCheckbox.width = "min-content"

        optionsCheckbox.setItems("git pull", "stop on error", "skip tests", "parallel")
        optionsCheckbox.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL)
        layoutRow4.setWidthFull()
        rightColumn.setFlexGrow(1.0, layoutRow4)
        layoutRow4.addClassName(LumoUtility.Gap.MEDIUM)
        layoutRow4.width = "100%"
        layoutRow4.style.set("flex-grow", "1")
        pomManipulationRadioGroup.label = "Pom manipulation"
        pomManipulationRadioGroup.style.set("flex-grow", "1")

        pomManipulationRadioGroup.setItems("manual", "auto-detect")
        pomManipulationRadioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
        pomManipulationRadioGroup.value = "auto-detect"

        textFieldJiraNr.label = "JiraNr"
        rightColumn.setAlignSelf(FlexComponent.Alignment.CENTER, textFieldJiraNr)
        textFieldJiraNr.width = "100%"
        textFieldJiraNr.isEnabled = false

        rightColumn.setAlignSelf(FlexComponent.Alignment.CENTER, buttonUpdatePomFiles)
        buttonUpdatePomFiles.width = "min-content"
        buttonUpdatePomFiles.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        layoutColumn6.setWidthFull()
        content?.setFlexGrow(1.0, layoutColumn6)
        layoutColumn6.addClassName(LumoUtility.Gap.XSMALL)
        layoutColumn6.addClassName(LumoUtility.Padding.XSMALL)
        layoutColumn6.width = "100%"
        layoutColumn6.height = "min-content"
        layoutRow5.setWidthFull()
        layoutColumn6.setFlexGrow(1.0, layoutRow5)
        layoutRow5.addClassName(LumoUtility.Gap.XSMALL)
        layoutRow5.addClassName(LumoUtility.Padding.XSMALL)
        layoutRow5.width = "100%"
        layoutRow5.height = "min-content"

        buttonBuild.setId("buttonBuild")
        buttonBuild.addClickListener {
            logEvent("buttonBuild clicked")
        }
        layoutRow5.setAlignSelf(FlexComponent.Alignment.CENTER, buttonBuild)
        buttonBuild.width = "min-content"
        buttonBuild.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        buttonCancel.setId("buttonCancel")
        buttonCancel.addClickListener {
            logEvent("buttonCancel clicked")
        }

        layoutRow5.setAlignSelf(FlexComponent.Alignment.CENTER, buttonCancel)
        buttonCancel.width = "min-content"
        layoutRow6.setHeightFull()
        layoutRow5.setFlexGrow(1.0, layoutRow6)
        layoutRow6.addClassName(LumoUtility.Gap.XSMALL)
        layoutRow6.addClassName(LumoUtility.Padding.XSMALL)
        layoutRow6.style.set("flex-grow", "1")
        layoutRow6.height = "min-content"
        layoutRow6.alignItems = FlexComponent.Alignment.CENTER
        layoutRow6.justifyContentMode = FlexComponent.JustifyContentMode.END
        buttonRefresh.width = "min-content"
        buttonRefresh.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        layoutRow6.setAlignSelf(FlexComponent.Alignment.CENTER, buttonCleanLog)
        buttonCleanLog.width = "min-content"
        buttonCleanLog.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        layoutRow7.addClassName(LumoUtility.Gap.MEDIUM)
        layoutRow7.width = "100%"
        layoutRow7.height = "min-content"

        layoutRow7.setAlignSelf(FlexComponent.Alignment.CENTER, textArea)
        textArea.style["flex-grow"] = "1"
        textArea.height = "100%"

        val treeGrid = pomFileDataProvider.createTreeGrid()

        content?.add(layoutRow)
        layoutRow.add(textField)
        layoutRow.add(buttonClearFilter)
        content?.add(layoutRow2)
        layoutRow2.add(leftColumn)
        leftColumn.add(treeGrid)
        layoutRow2.add(rightColumn)
        rightColumn.add(layoutRow3)
        layoutRow3.add(layoutColumn4)
        layoutColumn4.add(targetsChackbox)
        layoutRow3.add(layoutColumn5)
        layoutColumn5.add(optionsCheckbox)
        rightColumn.add(hr)
        rightColumn.add(layoutRow4)
        layoutRow4.add(pomManipulationRadioGroup)
        rightColumn.add(textFieldJiraNr)
        rightColumn.add(buttonUpdatePomFiles)
        content?.add(layoutColumn6)
        layoutColumn6.add(layoutRow5)
        layoutRow5.add(buttonBuild)
        layoutRow5.add(buttonCancel)
        layoutRow5.add(layoutRow6)
        layoutRow6.add(buttonRefresh)
        layoutRow6.add(buttonCleanLog)
        content?.add(layoutRow7)
        layoutRow7.add(textArea)

        // postInit
        postInit()
    }

    private fun setupTextArea() {
        this.textArea = TextArea()
        textArea.setId("logTextArea")
        textArea.setWidthFull()
        textArea.minHeight = "100px"
        textArea.maxHeight = "150px"
        textArea.label = "Log"
        textArea.placeholder = "<empty>"
        textArea.isClearButtonVisible = true
        textArea.isReadOnly = true
    }

    private fun postInit() {
        loggingService.initialiseer(this.textArea)
    }

//    private fun checkBoxDataProvicer(): CallbackDataProvider<*, *> {
//        CallbackDataProvider()
//    }
}
