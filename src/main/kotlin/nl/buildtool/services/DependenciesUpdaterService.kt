package nl.buildtool.services

import com.google.common.eventbus.Subscribe
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider
import nl.buildtool.model.PomDependency
import nl.buildtool.model.PomFile
import nl.buildtool.model.converter.PomFileConverter.writeXml
import nl.buildtool.model.events.PomFileDeselectedEvent
import nl.buildtool.model.events.PomFileSelectedEvent
import nl.buildtool.utils.ExtensionFunctions.logEvent
import nl.buildtool.utils.GlobalEventBus
import nl.buildtool.views.model.ViewModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.w3c.dom.Element
import javax.xml.xpath.XPathConstants.NODE
import javax.xml.xpath.XPathFactory

@Service
class DependenciesUpdatesService(private val viewModel: ViewModel) {
    private val logger = LoggerFactory.getLogger(DependenciesUpdatesService::class.java)
    private lateinit var ui: UI

    fun setupDependenciesUpdater(ui: UI) {
        this.ui = ui
    }

    @Subscribe
    private fun subscribe(event: PomFileDeselectedEvent) {
        logger.info("PomFileDeselectedEvent ontvangen. ${event.pomFile.artifactId}")
        ui.access {
            reselectTargetPomFiles()
        }
    }

    @Subscribe
    private fun subscribe(event: PomFileSelectedEvent) {
        logger.info("PomFileSelectedEvent ontvangen. ${event.pomFile.artifactId}")
        ui.access {
            reselectTargetPomFiles()
        }
    }

    fun subscribeEvents() {
        GlobalEventBus.eventBus.register(this)
    }

    fun unsubscribeEvents() {
        try {
            GlobalEventBus.eventBus.unregister(this)
        } catch (iae: IllegalArgumentException) {
            if (iae.message?.contains("missing event subscriber") == true) {
                logger.info("Nothing to unregister")
            } else throw iae
        }

    }

    fun reselectTargetPomFiles() {
        viewModel.targetGrid.deselectAll()

        viewModel.sourceGrid.selectedItems.forEach { selectedPomFile ->
            selectInTargetGrid(selectedPomFile)
        }

        val dataProvider = viewModel.targetGrid.dataProvider
        if (dataProvider is TreeDataProvider) {
            val selectedItems = viewModel.targetGrid.selectedItems
            if (selectedItems.size > 0) {
                dataProvider.setFilter { selectedItems.contains(it) }
            } else {
                dataProvider.setFilter(null)
            }
        }
    }

    fun selectInTargetGrid(selectedPomFile: PomFile) {
        val dependentPomFiles = findPomFiles(
            treeGrid = viewModel.targetGrid,
            artifactId = selectedPomFile.artifactId,
            groupId = selectedPomFile.groupId
        )
        dependentPomFiles.forEach { dependentPomFile ->
            viewModel.targetGrid.select(dependentPomFile)

            val parent = viewModel.targetGrid.treeData.getParent(dependentPomFile)
            // Selecteer de parent als die niet al geselecteerd is.
            if (parent != null && !viewModel.targetGrid.selectedItems.contains(parent)) {
                viewModel.targetGrid.select(parent)
            }
        }
    }

    fun updateDependencies() {
        val selectedSourceItems = viewModel.sourceGrid.selectedItems
        val selectedTargetItems = viewModel.targetGrid.selectedItems.toMutableSet()
        val changePomFilesMap = mutableSetOf<PomFile>()

        if (selectedTargetItems.isEmpty()) {
            logEvent("No dependencies selected.")
            return
        }

        // Reset reload boolean
        selectedTargetItems.parallelStream().map { it.triggerReload = false }

        selectedTargetItems.forEach { selectedTargetItem ->
            selectedSourceItems.forEach { selectedSourceItem ->
                if (selectedTargetItem.pomDependencies?.contains(
                        artifactId = selectedSourceItem.artifactId,
                        groupId = selectedSourceItem.groupId
                    ) == true
                ) {
                    val teUpdatenDependency = selectedTargetItem.pomDependencies?.firstOrNull {
                        it.artifactId == selectedSourceItem.artifactId &&
                                it.groupId == selectedSourceItem.groupId
                    } ?: return
                    updateDependency(
                        pomFileToUpdate = selectedTargetItem,
                        teUpdatenDependency = teUpdatenDependency,
                        targetArtifactId = selectedSourceItem.artifactId,
                        targetVersion = selectedSourceItem.version
                    )
                    changePomFilesMap.add(selectedTargetItem)
                }
            }
        }
        writeChangedDependencies(changePomFilesMap)
    }

    private fun updateDependency(
        pomFileToUpdate: PomFile,
        teUpdatenDependency: PomDependency,
        targetArtifactId: String,
        targetVersion: String
    ) {
        logger.info("Update dependency $targetArtifactId from ${teUpdatenDependency.version} to $targetVersion in pomFile ${pomFileToUpdate.file.canonicalFile}")

        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()
        val dependencyVersionXpath = teUpdatenDependency.versionXpath
        val versionNode = xPath.evaluate(dependencyVersionXpath, pomFileToUpdate.pomDocument, NODE) as Element?

        // Update version
        versionNode?.firstChild?.nodeValue = targetVersion

        pomFileToUpdate.triggerReload = true
    }

    private fun writeChangedDependencies(pomsWithChangedDependencies: Set<PomFile>) {
        pomsWithChangedDependencies.filter { pomFile -> pomFile.triggerReload == true }
            .parallelStream().forEach { pomFile ->
                // Wegschrijven
                logEvent("Write to file ${pomFile.file.canonicalFile}")
                writeXml(
                    pomFile = pomFile.file,
                    pomDocument = pomFile.pomDocument
                )
            }
    }

    private fun findPomFiles(treeGrid: TreeGrid<PomFile>, artifactId: String, groupId: String): List<PomFile> {
        val dependentPomFiles = mutableListOf<PomFile>()
        treeGrid.treeData.rootItems.forEach { rootPom ->
            // Check of deze pom een dependency heeft met de opgegeven artifactId en groupId
            val dependencyAanwezig = pomDependency(rootPom, artifactId, groupId)
            dependencyAanwezig?.let {
                dependentPomFiles.add(rootPom)
            }

            // Check of modules van deze pom een dependency hebben met de opgegeven artifactId en groupId
            val modulePoms = treeGrid.treeData.getChildren(rootPom).filter { modulePom ->
                pomDependency(modulePom, artifactId, groupId) != null
            }
            dependentPomFiles.addAll(modulePoms)
        }
        return dependentPomFiles
    }

    private fun pomDependency(
        pomFile: PomFile,
        artifactId: String,
        groupId: String
    ): PomDependency? {
        val pomDependency = pomFile.pomDependencies?.firstOrNull { pomDependency ->
            pomDependency.artifactId == artifactId && pomDependency.groupId == groupId
        }
        return pomDependency
    }


    private fun List<PomDependency>.contains(artifactId: String, groupId: String) =
        firstOrNull { it.artifactId == artifactId && it.groupId == groupId } != null
}