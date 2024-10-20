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
    private lateinit var treeGrid: TreeGrid<PomFile>
    private lateinit var dataProvider: TreeDataProvider<PomFile?>

    fun createTreeGrid(): TreeGrid<PomFile> {
        this.treeGrid = TreeGrid<PomFile>()
        this.treeGrid.height = "100%"
        this.treeGrid.width = "100%"
        this.dataProvider = dataProvider()
        treeGrid.setDataProvider(dataProvider)
        treeGrid.addHierarchyColumn(PomFile::artifactId).setHeader("ArtifactId")
        treeGrid.addColumn(PomFile::version).setHeader("Version")
        treeGrid.addColumn(PomFile::groupId).setHeader("GroupId")
        treeGrid.setSelectionMode(Grid.SelectionMode.MULTI)

        treeGrid.asMultiSelect().addValueChangeListener { event ->
            updateSelected(event)
        }

        //Refresh
        dataProvider.refreshAll()
        return treeGrid
    }

    fun dataProvider(): TreeDataProvider<PomFile?> {
        if (Globals.pomFileList.isEmpty()) {
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

    private fun updateSelected(event: AbstractField.ComponentValueChangeEvent<Grid<PomFile>, MutableSet<PomFile>>?) {
        event?.let {
            if (event.value.size > event.oldValue.size) {
                // Items aangevinkt
                updateSelected(event.value)
            } else if (event.value.size < event.oldValue.size) {
                updateDeselected(event)
            }
        }
        dataProvider.refreshAll()

        val alleGlobalPoms = Globals.pomFileList + Globals.pomFileList.flatMap { it.modulePoms.values }
        logger.info("Globals selection: " + alleGlobalPoms.filter { it.checked }
            .joinToString { it.artifactId })
    }

    fun updateSelected(pomfileSelection: MutableSet<PomFile>) {
        if (pomfileSelection.isNotEmpty()) {
            // select selection
            pomfileSelection.forEach {
                val alleGlobalPoms = Globals.pomFileList + Globals.pomFileList.flatMap { it.modulePoms.values }
                val globalPomFile = alleGlobalPoms.firstOrNull { pomFile -> pomFile.artifactId == it.artifactId }
                globalPomFile?.checked = true
                if (globalPomFile?.modulePoms?.isNotEmpty() == true) {
                    // Select children in treeGrid
                    globalPomFile.modulePoms.values.forEach { pomFile ->
                        this.treeGrid.selectionModel.select(pomFile)
                    }
                }
            }
        }
    }

    fun updateDeselected(event: AbstractField.ComponentValueChangeEvent<Grid<PomFile>, MutableSet<PomFile>>?) {
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
                        this.treeGrid.selectionModel.deselect(pomFile)
                    }
                }
            }

        }
    }

    fun refresh(withReload: Boolean) {
        Globals.pomFileList = directoryCrawler.getPomFileList()
        dataProvider.refreshAll()
    }
}