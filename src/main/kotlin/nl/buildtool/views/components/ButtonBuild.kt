package nl.buildtool.views.components

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.treegrid.TreeGrid
import nl.buildtool.maven.BuildExecutor
import nl.buildtool.model.PomFile

class ButtonBuild(
    private val buildExecutor: BuildExecutor,
    private val treeGrid: TreeGrid<PomFile>,
    private val checkboxTargets: CheckboxTargets
) : Button() {
    init {
        text = "Execute"
        width = "min-content"
        addThemeVariants(ButtonVariant.LUMO_PRIMARY)

        setId("buttonBuild")
        addClickListener {
            execute()
        }
    }

    private fun execute() {
        buildExecutor.executeBuild(
            pomFileList = treeGrid.selectedItems.toList(),
            mavenTargetList = checkboxTargets.value.map { it.lowercase() })
    }
}