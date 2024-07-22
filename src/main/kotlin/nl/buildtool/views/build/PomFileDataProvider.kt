package nl.buildtool.views.build

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.provider.hierarchy.TreeData
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider
import nl.buildtool.model.Globals
import nl.buildtool.model.PomFile
import nl.buildtool.utils.DirectoryCrawler
import org.springframework.stereotype.Component

@Component
class PomFileDataProvider(private val directoryCrawler: DirectoryCrawler) {

    fun createTreeGrid(textArea: TextArea): TreeGrid<PomFile> {
        val treeGrid = TreeGrid<PomFile>()
        val dataProvider = dataProvider()
        treeGrid.setDataProvider(dataProvider)
        treeGrid.addHierarchyColumn(PomFile::artifactId)
            .setHeader("ArtifactId")
        treeGrid.addColumn(PomFile::version).setHeader("Version")
        treeGrid.addColumn(PomFile::groupId).setHeader("GroupId")

        treeGrid.setSelectionMode(Grid.SelectionMode.MULTI)

        treeGrid.asMultiSelect().addValueChangeListener { event ->
            val selectedPomFile = event.value.first()
            if (selectedPomFile.modulePoms.isNotEmpty()) {
                selectedPomFile.checked = true
                // Select children
                selectedPomFile.modulePoms.values.forEach {
                    treeGrid.getSelectionModel().select(it)
                }
            }
            val message = String.format(
                "Selection changed from %s to %s",
                event.oldValue, event.value
            )
            textArea.value = message
        }
        //Refresh
        dataProvider.refreshAll()
        return treeGrid
    }

    fun dataProvider(): TreeDataProvider<PomFile?> {
        Globals.pomFileList = directoryCrawler.getPomFileList()
        val pomFiles = Globals.pomFileList
        val data = TreeData<PomFile?>()

        // add root level items
        data.addItems(null, pomFiles)

        // add children for the root level items
        pomFiles.forEach {
            data.addItems(it, it.modulePoms.values)
        }

        // construct the data provider for the hierarchical data we've built
        return TreeDataProvider(data)
    }
}