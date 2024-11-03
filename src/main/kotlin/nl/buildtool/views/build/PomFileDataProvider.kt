package nl.buildtool.views.build

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.provider.hierarchy.TreeData
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider
import nl.buildtool.model.Globals
import nl.buildtool.model.PomFile
import nl.buildtool.utils.DirectoryCrawler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PomFileDataProvider(private val directoryCrawler: DirectoryCrawler) {
    private val logger = LoggerFactory.getLogger(PomFileDataProvider::class.java)

    fun createTreeGrid(
        update: Boolean = false,
        selectable: Boolean = true
    ): TreeGrid<PomFile> {
        val treeGrid = TreeGrid<PomFile>()
        treeGrid.height = "100%"
        treeGrid.width = "100%"
        val dataProvider = dataProvider(update)
        treeGrid.setDataProvider(dataProvider)
        treeGrid.addHierarchyColumn(PomFile::artifactId).setHeader("ArtifactId")
        treeGrid.addColumn(PomFile::version).setHeader("Version")
        treeGrid.addColumn(PomFile::groupId).setHeader("GroupId")
        treeGrid.setSelectionMode(Grid.SelectionMode.MULTI)

        treeGrid.asMultiSelect().addValueChangeListener { event ->
            updateSelected(event, treeGrid)
        }

        if (!selectable) {
            treeGrid.className = "nonSelectable"
        }

        //Refresh
        dataProvider.refreshAll()
        return treeGrid
    }

    fun dataProvider(update: Boolean = false): TreeDataProvider<PomFile?> {
        if (Globals.pomFileList.isEmpty() || update) {
            logger.info("Inlezen pomfiles...")
            Globals.pomFileList = directoryCrawler.getPomFileList()
        }

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

    private fun updateSelected(
        event: AbstractField.ComponentValueChangeEvent<Grid<PomFile>, MutableSet<PomFile>>?,
        treeGrid: TreeGrid<PomFile>
    ) {
        event?.let {
            if (event.value.size > event.oldValue.size) {
                // Items aangevinkt
                updateSelected(event.value, treeGrid)
            } else if (event.value.size < event.oldValue.size) {
                updateDeselected(event, treeGrid)
            }
        }
        treeGrid.dataProvider.refreshAll()

        val alleGlobalPoms = Globals.pomFileList + Globals.pomFileList.flatMap { it.modulePoms.values }
        logger.info("Globals selection: " + alleGlobalPoms.filter { it.checked }
            .joinToString { it.artifactId })
    }

    fun updateSelected(
        pomfileSelection: MutableSet<PomFile>,
        treeGrid: TreeGrid<PomFile>
    ) {
        if (pomfileSelection.isNotEmpty()) {
            // select selection
            pomfileSelection.forEach {
                val alleGlobalPoms = Globals.pomFileList + Globals.pomFileList.flatMap { it.modulePoms.values }
                val globalPomFile = alleGlobalPoms.firstOrNull { pomFile -> pomFile.artifactId == it.artifactId }
                globalPomFile?.checked = true
                if (globalPomFile?.modulePoms?.isNotEmpty() == true) {
                    // Select children in treeGrid
                    globalPomFile.modulePoms.values.forEach { pomFile ->
                        treeGrid.selectionModel.select(pomFile)
                    }
                }
            }
        }
    }

    fun updateDeselected(
        event: AbstractField.ComponentValueChangeEvent<Grid<PomFile>, MutableSet<PomFile>>?,
        treeGrid: TreeGrid<PomFile>
    ) {
        // Items uitgevinkt
        event?.let {
            val values = event.value.toSet()
            val oldValues = event.oldValue.toSet().toMutableSet()
            oldValues.removeAll(values)
            logger.info("Deselected items: ${oldValues.joinToString { it.artifactId }}")

            oldValues.forEach { oldValuePomFile ->
                val alleGlobalPoms = Globals.pomFileList + Globals.pomFileList.flatMap { it.modulePoms.values }
                val globalPomFile =
                    alleGlobalPoms.firstOrNull { pomFile -> pomFile.artifactId == oldValuePomFile.artifactId }
                globalPomFile?.checked = false

                // Deselect children
                if (globalPomFile?.modulePoms?.isNotEmpty() == true) {
                    // Select children in treeGrid
                    globalPomFile.modulePoms.values.forEach { pomFile ->
                        treeGrid.selectionModel.deselect(pomFile)
                    }
                }
            }

        }
    }
}