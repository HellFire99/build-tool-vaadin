package nl.buildtool.views.build

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.provider.hierarchy.TreeData
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider
import nl.buildtool.model.Globals
import nl.buildtool.model.PomFile
import nl.buildtool.utils.DirectoryCrawler
import nl.buildtool.utils.ExtensionFunctions.logEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PomFileDataProvider(private val directoryCrawler: DirectoryCrawler) {
    private val logger = LoggerFactory.getLogger(PomFileDataProvider::class.java)
    private lateinit var treeGrid: TreeGrid<PomFile>
    private lateinit var dataProvider: TreeDataProvider<PomFile?>

    fun createTreeGrid(): TreeGrid<PomFile> {
        this.treeGrid = TreeGrid<PomFile>()
        this.dataProvider = dataProvider()
        treeGrid.setDataProvider(dataProvider)
        treeGrid.addHierarchyColumn(PomFile::artifactId)
            .setHeader("ArtifactId")
        treeGrid.addColumn(PomFile::version).setHeader("Version")
        treeGrid.addColumn(PomFile::groupId).setHeader("GroupId")

        treeGrid.setSelectionMode(Grid.SelectionMode.MULTI)

        treeGrid.asMultiSelect().addValueChangeListener { event ->
            updateSelection(event)
            logEvent(
                String.format(
                    "Selection changed from %s to %s",
                    event.oldValue, event.value
                )
            )
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

    private fun updateSelection(event: AbstractField.ComponentValueChangeEvent<Grid<PomFile>, MutableSet<PomFile>>?) {
        event?.let {
            updateSelection(event.value)
        }
    }

    fun updateSelection(pomfileSelection: MutableSet<PomFile>) {
        if (pomfileSelection.isEmpty()) {
            // Deselect all
            Globals.pomFileList.forEach { it.checked = false }
        } else {
            // select selection
//            this.treeGrid.getSelectionModel().deselectAll()
            Globals.pomFileList.forEach { it.checked = false }
            Globals.pomFileList.filter { it.modulePoms.isNotEmpty() }.forEach {
                it.modulePoms.values.forEach { modulePom ->
                    modulePom.checked = false
                }
            }

            pomfileSelection.forEach {
                it.checked = true
            }

            Globals.pomFileList.filter { it.checked }.forEach {
                if (it.modulePoms.isNotEmpty()) {
                    it.modulePoms.values.forEach { modulePom ->
                        modulePom.checked = true
                        this.treeGrid.getSelectionModel().select(modulePom)
                    }
                }
            }
            this.dataProvider.refreshAll()

            logger.info("Globals.pomFileList selection: " + Globals.pomFileList.filter { it.checked }
                .joinToString { it.artifactId })
            logger.info("pomfileSelection selection: " + pomfileSelection.filter { it.checked }
                .joinToString { it.artifactId })
        }
    }
}