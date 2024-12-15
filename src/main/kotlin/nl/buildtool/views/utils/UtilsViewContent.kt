package nl.buildtool.views.utils

import com.github.mvysny.kaributools.refresh
import com.google.common.eventbus.Subscribe
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.theme.lumo.LumoUtility
import nl.buildtool.model.*
import nl.buildtool.model.events.RefreshTableEvent
import nl.buildtool.services.DependenciesUpdatesService
import nl.buildtool.services.PomFileDataProviderService
import nl.buildtool.utils.ExtensionFunctions.logEvent
import nl.buildtool.utils.GlobalEventBus
import nl.buildtool.views.components.AutoDetectCustomOrResetRadio
import nl.buildtool.views.model.ViewModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class UtilsViewContent(
    private val pomFileDataProviderService: PomFileDataProviderService,
    private val dependenciesUpdatesService: DependenciesUpdatesService,
    private val viewModel: ViewModel
) {
    private val logger = LoggerFactory.getLogger(UtilsViewContent::class.java)

    private lateinit var secondColumnVerticalLayout: VerticalLayout
    private lateinit var contentRowPrefixPomFiles: HorizontalLayout
    private var contentRowUpdateDependencies: HorizontalLayout? = null
    private lateinit var utilsView: UtilsView
    lateinit var autoDetectCustomOrResetRadio: AutoDetectCustomOrResetRadio
    lateinit var pomFileSelectRadio: RadioButtonGroup<String>
    lateinit var customPrefixTextfield: TextField
    lateinit var pomFileSelectionGrid: TreeGrid<PomFile>
    lateinit var ui: UI
    var utilsMode = UtilsMode.UPDATE_POM_VERSIONS

    lateinit var prefixPomFilesTab: Tab
    lateinit var updateDependenciesTab: Tab

    fun initContent(utilsView: UtilsView) {
        this.utilsView = utilsView

        prefixPomFilesTab = Tab("Prefix pom files")
        updateDependenciesTab = Tab("Update  dependencies")
        val tabs = Tabs(prefixPomFilesTab, updateDependenciesTab)
        tabs.addSelectedChangeListener { event: Tabs.SelectedChangeEvent ->
            setContent(event.selectedTab)
        }

        utilsView.content?.add(tabs)

        secondColumnVerticalLayout = VerticalLayout()
        secondColumnVerticalLayout.setId("secondColumnVerticalLayout")

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
            middleColumn = middleColumn
        ) { this.evaluateExecuteButtonEnabling() }

        val rightColumn = VerticalLayout()
        rightColumn.setId("rightColumn")

        pomFileSelectionGrid = pomFileDataProviderService.createTreeGrid()
        pomFileSelectionGrid.addSelectionListener {
            this.evaluateExecuteButtonEnabling()
        }

        utilsView.content!!.addClassName(LumoUtility.Gap.XSMALL)
        utilsView.content!!.addClassName(LumoUtility.Padding.XSMALL)
        utilsView.content!!.width = "100%"
        utilsView.content!!.style["flex-grow"] = "1"

        secondColumnVerticalLayout.addClassName(LumoUtility.Gap.XSMALL)
        secondColumnVerticalLayout.addClassName(LumoUtility.Padding.XSMALL)
        secondColumnVerticalLayout.width = "100%"
        secondColumnVerticalLayout.style["flex-grow"] = "1"

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

        secondColumnVerticalLayout.add(contentRowPrefixPomFiles)

        middleColumn.add(pomFileSelectRadio)
        middleColumn.add(autoDetectCustomOrResetRadio)

        contentRowPrefixPomFiles.add(middleColumn)
        contentRowPrefixPomFiles.add(rightColumn)

        utilsView.content!!.add(secondColumnVerticalLayout)

        GlobalEventBus.eventBus.register(this)

        ui = UI.getCurrent()

        setupUpdateDependenciesContent()

        resetToDefaults()
    }

    private fun setContent(tab: Tab) {
        if (tab == prefixPomFilesTab) {
            prefixPomsTabClicked()
        } else if (tab == updateDependenciesTab) {
            updateDependenciesTabClicked()
        }
        evaluateExecuteButtonEnabling()
    }

    private fun prefixPomsTabClicked() {
        logEvent("Prefix Poms tab clicked")
        utilsMode = UtilsMode.UPDATE_POM_VERSIONS
        dependenciesUpdatesService.unsubscribeEvents()
        secondColumnVerticalLayout.removeAll()
        secondColumnVerticalLayout.add(contentRowPrefixPomFiles)
        resetToDefaults()
    }

    private fun resetToDefaults() {
        utilsMode = UtilsMode.UPDATE_POM_VERSIONS
        dependenciesUpdatesService.unsubscribeEvents()
        this.pomFileSelectRadio.value = RADIO_VALUE_ALL_IN_WORSPACE
        this.autoDetectCustomOrResetRadio.value = RADIO_VALUE_AUTO_DETECT
        this.pomFileSelectionGrid.deselectAll()
    }

    private fun updateDependenciesTabClicked() {
        logEvent("Update dependencies tab clicked")
        dependenciesUpdatesService.subscribeEvents()
        utilsMode = UtilsMode.UPDATE_DEPENDENCIES
        secondColumnVerticalLayout.removeAll()
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
        val sourceGrid = pomFileDataProviderService.createTreeGrid(fireEvents = true)
        val sourceColumn = createVerticalLayout(
            id = "sourceColumn",
            label = "Source",
            treeGrid = sourceGrid
        )

        // Target/right
        val targetGrid = pomFileDataProviderService.createTreeGrid(selectable = false)
        val targetColumn = createVerticalLayout(
            id = "targetColumn",
            label = "Target",
            treeGrid = targetGrid
        )

        contentRowUpdateDependencies.add(sourceColumn)
        contentRowUpdateDependencies.add(targetColumn)

        this.viewModel.init(
            sourceGrid = sourceGrid,
            targetGrid = targetGrid
        )

        this.dependenciesUpdatesService.setupDependenciesUpdater(ui = ui)
        this.contentRowUpdateDependencies = contentRowUpdateDependencies
    }

    fun evaluateExecuteButtonEnabling() {
        this.utilsView.executeButton.isEnabled =
            utilsMode == UtilsMode.UPDATE_DEPENDENCIES || pomFileSelectRadioIsValid() && customOrAutoDetectPrefixRadioIsValid()
    }

    @Subscribe
    private fun subscribe(event: RefreshTableEvent) {
        logEvent("RefreshTableEvent ontvangen")
        ui.access {
            val newTreeData = pomFileDataProviderService.dataProvider(true).treeData
            pomFileSelectionGrid.treeData = newTreeData
            viewModel.targetGrid.treeData = newTreeData
            viewModel.sourceGrid.treeData = newTreeData

            pomFileSelectionGrid.refresh()
            viewModel.targetGrid.refresh()
            viewModel.sourceGrid.refresh()
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