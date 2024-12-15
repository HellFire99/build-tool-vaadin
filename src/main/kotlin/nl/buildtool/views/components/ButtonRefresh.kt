package nl.buildtool.views.components

import com.github.mvysny.kaributools.refresh
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.treegrid.TreeGrid
import nl.buildtool.model.PomFile
import nl.buildtool.services.PomFileDataProviderService
import nl.buildtool.utils.ExtensionFunctions.logEvent

class ButtonRefresh(
    val pomFileDataProviderService: PomFileDataProviderService,
    val treeGrid: TreeGrid<PomFile>
) : Button() {
    init {
        text = "Refresh"
        setId("buttonRefresh")
        addClickListener {
            execute()
            logEvent("Pom files refreshed")
        }
    }

    private fun execute() {
        val newTreeData = pomFileDataProviderService.dataProvider(update = true)
        treeGrid.treeData = newTreeData.treeData
        treeGrid.refresh()
    }
}