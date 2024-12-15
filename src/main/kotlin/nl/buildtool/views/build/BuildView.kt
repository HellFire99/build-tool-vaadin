package nl.buildtool.views.build

import com.github.mvysny.kaributools.refresh
import com.google.common.eventbus.Subscribe
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Menu
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.lumo.LumoUtility
import nl.buildtool.maven.BuildExecutor
import nl.buildtool.model.events.BuildingCompleteEvent
import nl.buildtool.services.LoggingService
import nl.buildtool.services.PomFileDataProviderService
import nl.buildtool.utils.ExtensionFunctions.logEvent
import nl.buildtool.utils.GlobalEventBus
import nl.buildtool.views.MainLayout
import nl.buildtool.views.components.ButtonBuild
import nl.buildtool.views.components.ButtonCleanLog
import nl.buildtool.views.components.ButtonRefresh
import nl.buildtool.views.components.CheckboxTargets
import nl.buildtool.views.model.ViewModel
import org.springframework.beans.factory.InitializingBean

@PageTitle("Build")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0.0)
@Route(value = "", layout = MainLayout::class)
class BuildView(
    pomFileDataProviderService: PomFileDataProviderService,
    loggingService: LoggingService,
    buildExecutor: BuildExecutor,
    private val viewModel: ViewModel
) : Composite<VerticalLayout?>(), InitializingBean {
    private lateinit var ui: UI

    init {
        ui = UI.getCurrent()
        viewModel.buildViewGrid = pomFileDataProviderService.createTreeGrid()

        content?.style?.set("gap", "0")
        content?.width = "100%"
        content?.style?.set("flex-grow", "1")

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
        layoutRow2.width = "100%"
        layoutRow2.minHeight = "490px"
        layoutRow2.style.set("flex-grow", "1")
        layoutRow2.style?.set("gap", "0")

        val leftColumn = VerticalLayout()
        leftColumn.setId("leftColumn")
        leftColumn.addClassName(LumoUtility.Padding.XSMALL)
        leftColumn.width = "100%"
        leftColumn.style.set("flex-grow", "1")
        leftColumn.style.set("padding-left", "0")

        val layoutRow3 = HorizontalLayout()
        layoutRow3.setId("layoutRow3")
        layoutRow3.setWidthFull()
        layoutRow3.width = "100%"
        layoutRow3.height = "min-content"
        layoutRow3.style?.set("gap", "0")

        val rightColumn = VerticalLayout()
        rightColumn.setId("rightColumn")
        rightColumn.style?.set("gap", "0")
        rightColumn.minWidth = "360px"
        rightColumn.width = ""

        val layoutColumn4 = VerticalLayout()
        layoutColumn4.setId("layoutColumn4")

        val targetsCheckbox = CheckboxTargets()

        val layoutColumn5 = VerticalLayout()
        layoutColumn5.setId("layoutColumn5")

        val optionsCheckbox = CheckboxGroup<Any?>()
        optionsCheckbox.setId("checkboxGroup2")
        optionsCheckbox.label = "Options"
        optionsCheckbox.setItems("git pull", "stop on error", "skip tests", "parallel")
        optionsCheckbox.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL)

        val layoutColumn6 = VerticalLayout()
        layoutColumn6.setId("layoutColumn6")

        val layoutRow5 = HorizontalLayout()
        layoutRow5.setId("layoutRow5")

        val buttonBuild = ButtonBuild(buildExecutor, viewModel.buildViewGrid, targetsCheckbox)
        val buttonCancel = Button("Cancel")
        val layoutRow6 = HorizontalLayout()
        layoutRow6.setId("layoutRow6")

        val buttonRefresh = ButtonRefresh(pomFileDataProviderService, viewModel.buildViewGrid)
        val buttonCleanLog = ButtonCleanLog(loggingService)

        val footerRow = HorizontalLayout()
        footerRow.setId("footerRow")
        footerRow.addClassName(LumoUtility.Gap.MEDIUM)
        footerRow.width = "100%"
        footerRow.height = "min-content"

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

        layoutColumn4.setHeightFull()
        layoutRow3.setFlexGrow(1.0, layoutColumn4)
        layoutColumn4.addClassName(LumoUtility.Gap.XSMALL)
        layoutColumn4.addClassName(LumoUtility.Padding.XSMALL)
        layoutColumn4.width = "100%"
        layoutColumn4.height = "min-content"
        layoutColumn5.setHeightFull()
        layoutRow3.setFlexGrow(1.0, layoutColumn5)
        layoutColumn5.addClassName(LumoUtility.Gap.XSMALL)
        layoutColumn5.addClassName(LumoUtility.Padding.XSMALL)
        layoutColumn5.width = "100%"
        layoutColumn5.height = "min-content"

        layoutColumn6.setWidthFull()
        content?.setFlexGrow(1.0, layoutColumn6)
        layoutColumn6.addClassName(LumoUtility.Gap.XSMALL)
        layoutColumn6.addClassName(LumoUtility.Padding.XSMALL)
        layoutColumn6.width = "100%"
        layoutColumn6.height = "min-content"
        layoutColumn6.style.set("padding-top", "0")
        layoutColumn6.style.set("padding-bottom", "0")
        layoutRow5.setWidthFull()
        layoutColumn6.setFlexGrow(1.0, layoutRow5)
        layoutRow5.addClassName(LumoUtility.Gap.XSMALL)
        layoutRow5.addClassName(LumoUtility.Padding.XSMALL)
        layoutRow5.width = "100%"
        layoutRow5.height = "min-content"

        layoutRow5.setAlignSelf(FlexComponent.Alignment.CENTER, buttonBuild)

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

        val loggingTextArea = loggingService.setupTextArea(
            loggingTextArea = TextArea()
        )
        footerRow.setAlignSelf(FlexComponent.Alignment.CENTER, loggingTextArea)

        content?.add(layoutRow)
        layoutRow.add(textField)
        layoutRow.add(buttonClearFilter)
        content?.add(layoutRow2)
        layoutRow2.add(leftColumn)
        leftColumn.add(viewModel.buildViewGrid)
        layoutRow2.add(rightColumn)
        rightColumn.add(layoutRow3)
        layoutRow3.add(layoutColumn4)
        layoutColumn4.add(targetsCheckbox)
        layoutRow3.add(layoutColumn5)
        layoutColumn5.add(optionsCheckbox)
        content?.add(layoutColumn6)
        layoutColumn6.add(layoutRow5)
        layoutRow5.add(buttonBuild)
        layoutRow5.add(buttonCancel)
        layoutRow5.add(layoutRow6)
        layoutRow6.add(buttonRefresh)
        layoutRow6.add(buttonCleanLog)

        content?.add(footerRow)
        footerRow.add(loggingTextArea)
    }

    @Subscribe
    fun subscribe(event: BuildingCompleteEvent) {
        logEvent(event.statusText)
        ui.access {
            viewModel.buildViewGrid.deselectAll()
            viewModel.buildViewGrid.refresh()
        }
    }

    override fun afterPropertiesSet() {
        GlobalEventBus.eventBus.register(this)
    }
}
