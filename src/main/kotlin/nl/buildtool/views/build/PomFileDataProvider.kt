package nl.buildtool.views.build

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.provider.hierarchy.TreeData
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider
import nl.buildtool.model.Globals
import nl.buildtool.model.PomFile
import nl.buildtool.model.events.PomFileDeselectedEvent
import nl.buildtool.model.events.PomFileSelectedEvent
import nl.buildtool.utils.DirectoryCrawler
import nl.buildtool.utils.ExtensionFunctions.post
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PomFileDataProvider(private val directoryCrawler: DirectoryCrawler) {
    private val logger = LoggerFactory.getLogger(PomFileDataProvider::class.java)

    fun createTreeGrid(
        update: Boolean = false,
        selectable: Boolean = true,
        fireEvents: Boolean = false
    ): TreeGrid<PomFile> {
        val treeGrid = TreeGrid<PomFile>()
        treeGrid.height = "100%"
        treeGrid.width = "100%"
        treeGrid.className = "smaller-font"
        val dataProvider = dataProvider(update)
        treeGrid.setDataProvider(dataProvider)
        treeGrid.addHierarchyColumn(PomFile::artifactId).setHeader("ArtifactId")
        treeGrid.addColumn(PomFile::version).setHeader("Version")
        treeGrid.addColumn(PomFile::groupId).setHeader("GroupId")
        treeGrid.setSelectionMode(Grid.SelectionMode.MULTI)

        treeGrid.asMultiSelect().addValueChangeListener { event ->
            if (fireEvents) {
                updateSelected(
                    event = event,
                    treeGrid = treeGrid,
                    fireEvents = true
                )
            }
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
        treeGrid: TreeGrid<PomFile>,
        fireEvents: Boolean = false
    ) {
        event?.let {
            if (event.value.size > event.oldValue.size) {
                // Items aangevinkt
                updateSelected(event.value, treeGrid, fireEvents)
            } else if (event.value.size < event.oldValue.size) {
                updateDeselected(event, treeGrid, fireEvents)
            }
        }
        treeGrid.dataProvider.refreshAll()

        val alleGlobalPoms = Globals.pomFileList + Globals.pomFileList.flatMap { it.modulePoms.values }
        logger.info("Globals selection: " + alleGlobalPoms.filter { it.checked }
            .joinToString { it.artifactId })
    }

    fun updateSelected(
        pomfileSelection: MutableSet<PomFile>,
        treeGrid: TreeGrid<PomFile>,
        fireEvents: Boolean = false
    ) {
        if (pomfileSelection.isNotEmpty()) {
            // select selection
            pomfileSelection.filter { !it.checked }.forEach {
                val alleGlobalPoms = Globals.pomFileList + Globals.pomFileList.flatMap { it.modulePoms.values }
                val globalPomFile = alleGlobalPoms.firstOrNull { pomFile -> pomFile.artifactId == it.artifactId }
                globalPomFile?.checked = true

                if (globalPomFile?.modulePoms?.isNotEmpty() == true) {
                    // Select children in treeGrid
                    globalPomFile.modulePoms.values.forEach { pomFile ->
                        treeGrid.selectionModel.select(pomFile)
                    }
                }

                globalPomFile?.let {
                    logger.info("Afvuren PomFileSelectedEvent. ${it.artifactId}")
                    post(PomFileSelectedEvent(it))
                }
            }
        }
    }

    fun updateDeselected(
        event: AbstractField.ComponentValueChangeEvent<Grid<PomFile>, MutableSet<PomFile>>?,
        treeGrid: TreeGrid<PomFile>,
        fireEvents: Boolean = false
    ) {
        // Items uitgevinkt
        event?.let {
            val values = event.value.toSet()
            val oldValues = event.oldValue.toSet().toMutableSet()
            oldValues.removeAll(values)
            logger.info("Deselected items: ${oldValues.joinToString { it.artifactId }}")

            oldValues.filter { it.checked }.forEach { oldValuePomFile ->
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
                globalPomFile?.let {
                    logger.info("Afvuren PomFileDeselectedEvent. ${it.artifactId}")
                    post(PomFileDeselectedEvent(it))
                }
            }

        }
    }
}