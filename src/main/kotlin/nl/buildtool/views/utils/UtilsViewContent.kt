package nl.buildtool.views.utils

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.theme.lumo.LumoUtility
import nl.buildtool.model.RADIO_VALUE_ALL_IN_WORSPACE
import nl.buildtool.model.RADIO_VALUE_AUTO_DETECT
import nl.buildtool.model.RADIO_VALUE_CUSTOM_PREFIX
import nl.buildtool.model.RADIO_VALUE_SELECTION
import nl.buildtool.views.build.PomFileDataProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UtilsViewContent(private val pomFileDataProvider: PomFileDataProvider) {
    private val logger = LoggerFactory.getLogger(UtilsViewContent::class.java)

    private lateinit var mainRow: HorizontalLayout
    private lateinit var secondColumnVerticalLayout: VerticalLayout
    private lateinit var subTitle: H4
    private lateinit var contentRowPrefixPomFiles: HorizontalLayout
    private var contentRowUpdateDependencies: HorizontalLayout? = null

    fun initContent(utilsView: UtilsView) {
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

        val pomFileSelectRadio = RadioButtonGroup<String>()
        pomFileSelectRadio.setId("pomFileSelectRadio")

        val customOrAutoDetectPrefixRadio = RadioButtonGroup<String>()
        customOrAutoDetectPrefixRadio.setId("customOrAutoDetectPrefixRadio")

        val customPrefixTextfield = TextField()
        customPrefixTextfield.setId("customPrefixTextfield")

        val rightColumn = VerticalLayout()
        rightColumn.setId("rightColumn")

        val treeGrid = pomFileDataProvider.createTreeGrid()

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
            if (it.value == RADIO_VALUE_ALL_IN_WORSPACE) {
//                treeGrid.setSelectionMode(Grid.SelectionMode.NONE)
                
            }
        }

        customOrAutoDetectPrefixRadio.label = "Auto-detect or custom prefix"
        customOrAutoDetectPrefixRadio.width = "min-content"
        customOrAutoDetectPrefixRadio.setItems(RADIO_VALUE_AUTO_DETECT, RADIO_VALUE_CUSTOM_PREFIX)
        customOrAutoDetectPrefixRadio.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
        customPrefixTextfield.label = "Prefix"
        customPrefixTextfield.width = "100%"
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
        middleColumn.add(customOrAutoDetectPrefixRadio)
        middleColumn.add(customPrefixTextfield)
        contentRowPrefixPomFiles.add(middleColumn)

        contentRowPrefixPomFiles.add(rightColumn)
        rightColumn.add(treeGrid)

        utilsView.content!!.add(mainRow)
    }

    private fun prefixPomsButtonClicked() {
        logger.info("prefixPomsButtonClicked")
        subTitle.text = "Prefix pom files"

        secondColumnVerticalLayout.remove(contentRowUpdateDependencies)
        secondColumnVerticalLayout.add(contentRowPrefixPomFiles)
    }

    private fun updateDependenciesButtonClicked() {
        logger.info("updateDependenciesButtonClicked")
        subTitle.text = "Update dependencies"

        // Verwijder Prefix pom files content
        secondColumnVerticalLayout.remove(contentRowPrefixPomFiles)

        if (contentRowUpdateDependencies == null) {
            setupUpdateDependenciesContent()
        }

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

        val sourceColumn = VerticalLayout()
        sourceColumn.setId("sourceColumn")
        sourceColumn.addClassName(LumoUtility.Gap.XSMALL)
        sourceColumn.addClassName(LumoUtility.Padding.XSMALL)
        sourceColumn.style["flex-grow"] = "1"
        sourceColumn.width = "250px"
        sourceColumn.setWidthFull()
        sourceColumn.style["flex-grow"] = "1"
        contentRowPrefixPomFiles.setFlexGrow(1.0, sourceColumn)

        val sourceText = H5("Source")
        val sourceGrid = pomFileDataProvider.createTreeGrid()

        sourceColumn.add(sourceText)
        sourceColumn.add(sourceGrid)

        val targetColumn = VerticalLayout()
        targetColumn.setId("targetColumn")
        targetColumn.addClassName(LumoUtility.Gap.XSMALL)
        targetColumn.addClassName(LumoUtility.Padding.XSMALL)
        targetColumn.style["flex-grow"] = "1"
        targetColumn.width = "250px"
        targetColumn.setWidthFull()
        targetColumn.style["flex-grow"] = "1"
        contentRowPrefixPomFiles.setFlexGrow(1.0, targetColumn)

        val targetGrid = pomFileDataProvider.createTreeGrid()
        val targetText = H5("Target")
        targetColumn.add(targetText)
        targetColumn.add(targetGrid)

        contentRowUpdateDependencies.add(sourceColumn)
        contentRowUpdateDependencies.add(targetColumn)
        this.contentRowUpdateDependencies = contentRowUpdateDependencies
    }
}