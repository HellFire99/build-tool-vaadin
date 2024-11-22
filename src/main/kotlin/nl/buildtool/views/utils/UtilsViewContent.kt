package nl.buildtool.views.utils

import com.github.mvysny.kaributools.refresh
import com.google.common.eventbus.Subscribe
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.theme.lumo.LumoUtility
import nl.buildtool.model.LABEL_AUTO_DETECT_INFO
import nl.buildtool.model.PomFile
import nl.buildtool.model.RADIO_VALUE_ALL_IN_WORSPACE
import nl.buildtool.model.RADIO_VALUE_AUTO_DETECT
import nl.buildtool.model.RADIO_VALUE_CUSTOM_PREFIX
import nl.buildtool.model.RADIO_VALUE_SELECTION
import nl.buildtool.model.UtilsMode
import nl.buildtool.model.events.RefreshTableEvent
import nl.buildtool.services.DependenciesUpdatesService
import nl.buildtool.utils.ExtensionFunctions.logEvent
import nl.buildtool.utils.GlobalEventBus
import nl.buildtool.views.build.PomFileDataProvider
import nl.buildtool.views.components.AutoDetectCustomOrResetRadio
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UtilsViewContent(
    private val pomFileDataProvider: PomFileDataProvider,
    private val dependenciesUpdatesService: DependenciesUpdatesService) {
    private val logger = LoggerFactory.getLogger(UtilsViewContent::class.java)

    private lateinit var mainRow: HorizontalLayout
    private lateinit var secondColumnVerticalLayout: VerticalLayout
    private lateinit var subTitle: H4
    private lateinit var contentRowPrefixPomFiles: HorizontalLayout
    private var contentRowUpdateDependencies: HorizontalLayout? = null
    private lateinit var utilsView: UtilsView
    lateinit var autoDetectCustomOrResetRadio: AutoDetectCustomOrResetRadio
    lateinit var pomFileSelectRadio: RadioButtonGroup<String>
    lateinit var customPrefixTextfield: TextField
    lateinit var pomFileSelectionGrid: TreeGrid<PomFile>
    lateinit var ui: UI
    var utilsMode = UtilsMode.UPDATE_POM_VERSIONS

    fun initContent(utilsView: UtilsView) {
        this.utilsView = utilsView

        mainRow = HorizontalLayout()
        mainRow.setId("mainRow")

        val leftColumn = VerticalLayout()
        leftColumn.setId("leftColumn")

        val prefixPomsButton = Button("Prefix pom files")
        prefixPomsButton.addClickListener {
            prefixPomsButtonClicked()
        }

        val updateDependenciesButton = Button("Update  dependencies")
        updateDependenciesButton.addClickListener {
            updateDependenciesButtonClicked()
        }

        secondColumnVerticalLayout = VerticalLayout()
        secondColumnVerticalLayout.setId("secondColumnVerticalLayout")

        subTitle = H4()
        contentRowPrefixPomFiles = HorizontalLayout()
        contentRowPrefixPomFiles.setId("innerRow")
        contentRowPrefixPomFiles.minHeight = "490px"

        val middleColumn = VerticalLayout()
        middleColumn.setId("middleColumn")

        val autoDetectInfoMessage = TextArea()
        autoDetectInfoMessage.setId("autoDetectInfo")
        autoDetectInfoMessage.isReadOnly = true
        autoDetectInfoMessage.label = LABEL_AUTO_DETECT_INFO
        autoDetectInfoMessage.width = "100%"
        autoDetectInfoMessage.setWidthFull()

        customPrefixTextfield = TextField()
        customPrefixTextfield.setId("customPrefixTextfield")
        customPrefixTextfield.label = "Prefix"
        customPrefixTextfield.width = "100%"
        customPrefixTextfield.addValueChangeListener {
            this.evaluateExecuteButtonEnabling()
        }
        customPrefixTextfield.valueChangeTimeout = 300
        customPrefixTextfield.valueChangeMode = ValueChangeMode.LAZY

        pomFileSelectRadio = RadioButtonGroup<String>()
        pomFileSelectRadio.setId("pomFileSelectRadio")

        autoDetectCustomOrResetRadio = AutoDetectCustomOrResetRadio(
            autoDetectInfoMessage = autoDetectInfoMessage,
            customPrefixTextfield = customPrefixTextfield,
            middleColumn = middleColumn) { this.evaluateExecuteButtonEnabling() }

        val rightColumn = VerticalLayout()
        rightColumn.setId("rightColumn")

        pomFileSelectionGrid = pomFileDataProvider.createTreeGrid()
        pomFileSelectionGrid.addSelectionListener {
            this.evaluateExecuteButtonEnabling()
        }

        utilsView.content!!.addClassName(LumoUtility.Gap.XSMALL)
        utilsView.content!!.addClassName(LumoUtility.Padding.XSMALL)
        utilsView.content!!.width = "100%"
        utilsView.content!!.style["flex-grow"] = "1"
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
        secondColumnVerticalLayout.addClassName(LumoUtility.Gap.XSMALL)
        secondColumnVerticalLayout.addClassName(LumoUtility.Padding.XSMALL)
        secondColumnVerticalLayout.width = "100%"
        secondColumnVerticalLayout.style["flex-grow"] = "1"
        subTitle.text = "Prefix pom files"
        subTitle.width = "max-content"
        contentRowPrefixPomFiles.setWidthFull()
        secondColumnVerticalLayout.setFlexGrow(1.0, contentRowPrefixPomFiles)
        contentRowPrefixPomFiles.addClassName(LumoUtility.Gap.XSMALL)
        contentRowPrefixPomFiles.addClassName(LumoUtility.Padding.XSMALL)
        contentRowPrefixPomFiles.width = "100%"
        contentRowPrefixPomFiles.style["flex-grow"] = "1"
        middleColumn.setHeightFull()
        contentRowPrefixPomFiles.setFlexGrow(1.0, middleColumn)
        middleColumn.addClassName(LumoUtility.Gap.XSMALL)
        middleColumn.addClassName(LumoUtility.Padding.XSMALL)
        middleColumn.width = "min-content"
        middleColumn.style["flex-grow"] = "1"
        pomFileSelectRadio.label = "Choose which pom files to update"
        pomFileSelectRadio.width = "min-content"
        pomFileSelectRadio.setItems(RADIO_VALUE_ALL_IN_WORSPACE, RADIO_VALUE_SELECTION)
        pomFileSelectRadio.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
        pomFileSelectRadio.addValueChangeListener {
            if (it.value == RADIO_VALUE_SELECTION) {
                rightColumn.add(pomFileSelectionGrid)
            } else if (it.value == RADIO_VALUE_ALL_IN_WORSPACE) {
                rightColumn.remove(pomFileSelectionGrid)
                pomFileSelectionGrid.deselectAll()
            }
            evaluateExecuteButtonEnabling()
        }

        rightColumn.setHeightFull()

        contentRowPrefixPomFiles.setFlexGrow(1.0, rightColumn)
        rightColumn.addClassName(LumoUtility.Gap.XSMALL)
        rightColumn.addClassName(LumoUtility.Padding.XSMALL)
        rightColumn.width = "100%"
        rightColumn.style["flex-grow"] = "1"

        mainRow.add(leftColumn)
        leftColumn.add(prefixPomsButton)
        leftColumn.add(updateDependenciesButton)

        secondColumnVerticalLayout.add(subTitle)
        secondColumnVerticalLayout.add(contentRowPrefixPomFiles)
        mainRow.add(secondColumnVerticalLayout)

        middleColumn.add(pomFileSelectRadio)
        middleColumn.add(autoDetectCustomOrResetRadio)

        contentRowPrefixPomFiles.add(middleColumn)
        contentRowPrefixPomFiles.add(rightColumn)

        utilsView.content!!.add(mainRow)

        GlobalEventBus.eventBus.register(this)

        ui = UI.getCurrent()

        setupUpdateDependenciesContent()

        resetToDefaults()
    }

    private fun prefixPomsButtonClicked() {
        logger.info("prefixPomsButtonClicked")
        utilsMode = UtilsMode.UPDATE_POM_VERSIONS
        logEvent("Prefix Poms button clicked")
        subTitle.text = "Prefix pom files"

        if (contentRowUpdateDependencies != null) {
            secondColumnVerticalLayout.remove(contentRowUpdateDependencies)
        }

        secondColumnVerticalLayout.add(contentRowPrefixPomFiles)
        resetToDefaults()
    }

    private fun resetToDefaults() {
        this.pomFileSelectRadio.value = RADIO_VALUE_ALL_IN_WORSPACE
        this.autoDetectCustomOrResetRadio.value = RADIO_VALUE_AUTO_DETECT
        this.pomFileSelectionGrid.deselectAll()
    }

    private fun updateDependenciesButtonClicked() {
        logEvent("Update dependencies button clicked")
        utilsMode = UtilsMode.UPDATE_DEPENDENCIES
        subTitle.text = "Update dependencies"

        // Verwijder Prefix pom files content
        secondColumnVerticalLayout.remove(contentRowUpdateDependencies)
        secondColumnVerticalLayout.remove(contentRowPrefixPomFiles)
        secondColumnVerticalLayout.add(contentRowUpdateDependencies)
    }

    private fun setupUpdateDependenciesContent() {
        val contentRowUpdateDependencies = HorizontalLayout()
        contentRowUpdateDependencies.setId("contentRowUpdateDependencies")
        contentRowUpdateDependencies.minHeight = "490px"
        contentRowUpdateDependencies.setWidthFull()
        secondColumnVerticalLayout.setFlexGrow(1.0, contentRowUpdateDependencies)
        contentRowUpdateDependencies.addClassName(LumoUtility.Gap.XSMALL)
        contentRowUpdateDependencies.addClassName(LumoUtility.Padding.XSMALL)
        contentRowUpdateDependencies.width = "100%"
        contentRowUpdateDependencies.style["flex-grow"] = "1"
        contentRowUpdateDependencies.style["padding"] = "0"

        // Source/left
        val sourceGrid = pomFileDataProvider.createTreeGrid(fireEvents = true)
        val sourceColumn = createVerticalLayout(
            id = "sourceColumn",
            label = "Source",
            treeGrid = sourceGrid
                                               )

        // Target/right
        val targetGrid = pomFileDataProvider.createTreeGrid(selectable = false)
        val targetColumn = createVerticalLayout(
            id = "targetColumn",
            label = "Target",
            treeGrid = targetGrid
                                               )

        contentRowUpdateDependencies.add(sourceColumn)
        contentRowUpdateDependencies.add(targetColumn)

        this.dependenciesUpdatesService.setupDependenciesUpdater(
            sourceGrid = sourceGrid,
            targetGrid = targetGrid,
            ui = ui
                                                                )
        this.contentRowUpdateDependencies = contentRowUpdateDependencies
    }

    fun evaluateExecuteButtonEnabling() {
        this.utilsView.executeButton.isEnabled = pomFileSelectRadioIsValid() && customOrAutoDetectPrefixRadioIsValid()
    }

    @Subscribe
    private fun subscribe(event: RefreshTableEvent) {
        logEvent("RefreshTableEvent ontvangen")
        ui.access {
            pomFileSelectionGrid.treeData = pomFileDataProvider.dataProvider(true).treeData
            pomFileSelectionGrid.refresh()
        }
    }

    private fun createVerticalLayout(
        id: String,
        label: String,
        treeGrid: TreeGrid<PomFile>
                                    ): VerticalLayout {
        val column = VerticalLayout()
        column.setId(id)
        column.addClassName(LumoUtility.Gap.XSMALL)
        column.addClassName(LumoUtility.Padding.XSMALL)
        column.width = "250px"
        column.setWidthFull()
        column.style["flex-grow"] = "1"
        column.style["padding"] = "0"
        contentRowPrefixPomFiles.setFlexGrow(1.0, column)

        val h5 = H5(label)

        column.add(h5)
        column.add(treeGrid)
        return column
    }

    private fun pomFileSelectRadioIsValid() =
        this.pomFileSelectRadio.value == RADIO_VALUE_ALL_IN_WORSPACE ||
                (this.pomFileSelectRadio.value == RADIO_VALUE_SELECTION && this.pomFileSelectionGrid.selectedItems.isNotEmpty())

    private fun customOrAutoDetectPrefixRadioIsValid() =
        this.autoDetectCustomOrResetRadio.value?.isNotEmpty() == true && customPrefixIsValid()

    private fun customPrefixIsValid() =
        this.autoDetectCustomOrResetRadio.value != RADIO_VALUE_CUSTOM_PREFIX ||
                (this.autoDetectCustomOrResetRadio.value == RADIO_VALUE_CUSTOM_PREFIX &&
                        this.customPrefixTextfield.value?.isNotEmpty() == true)

}