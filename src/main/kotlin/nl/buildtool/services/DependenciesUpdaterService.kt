package nl.buildtool.services

import com.google.common.eventbus.Subscribe
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.treegrid.TreeGrid
import nl.buildtool.model.PomDependency
import nl.buildtool.model.PomFile
import nl.buildtool.model.converter.PomFileConverter.readXml
import nl.buildtool.model.converter.PomFileConverter.writeXml
import nl.buildtool.model.events.PomFileDeselectedEvent
import nl.buildtool.model.events.PomFileSelectedEvent
import nl.buildtool.utils.GlobalEventBus
import nl.buildtool.views.model.ViewModel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import org.w3c.dom.Element
import javax.xml.xpath.XPathConstants.NODE
import javax.xml.xpath.XPathFactory

@Service
class DependenciesUpdatesService(private val viewModel: ViewModel) : InitializingBean {
    private val logger = LoggerFactory.getLogger(DependenciesUpdatesService::class.java)
    private lateinit var ui: UI

    fun setupDependenciesUpdater(ui: UI) {
        this.ui = ui
    }

    @Subscribe
    private fun subscribe(event: PomFileDeselectedEvent) {
        logger.info("PomFileDeselectedEvent ontvangen. ${event.pomFile.artifactId}")
        ui.access {
            val dependentPomFiles = findPomFiles(
                treeGrid = viewModel.targetGrid,
                artifactId = event.pomFile.artifactId,
                groupId = event.pomFile.groupId
            )
            dependentPomFiles.let {
                dependentPomFiles.forEach { dependentPomFile ->
                    viewModel.targetGrid.deselect(dependentPomFile)

                    val parent = viewModel.targetGrid.treeData.getParent(dependentPomFile)
                    if (parent != null) {
                        viewModel.targetGrid.deselect(parent)
                    }
                }
            }
        }
    }

    @Subscribe
    private fun subscribe(event: PomFileSelectedEvent) {
        logger.info("PomFileSelectedEvent ontvangen. ${event.pomFile.artifactId}")
        // zoek in target obv artifactId en groupId
        ui.access {
            val dependentPomFiles = findPomFiles(
                treeGrid = viewModel.targetGrid,
                artifactId = event.pomFile.artifactId,
                groupId = event.pomFile.groupId
            )
            dependentPomFiles.let {
                dependentPomFiles.forEach { dependentPomFile ->
                    viewModel.targetGrid.select(dependentPomFile)

                    val parent = viewModel.targetGrid.treeData.getParent(dependentPomFile)
                    if (parent != null) {
                        viewModel.targetGrid.select(parent)
                    }
                }
            }
        }
    }

    fun updateDependencies() {
        val soureGrid = viewModel.sourceGrid
        val targetGrid = viewModel.targetGrid

        val selectedSourceItems = soureGrid.selectedItems
        targetGrid.selectedItems.forEach { selectedTargetItem ->
            selectedSourceItems.forEach { selectedSourceItem ->
                if (selectedTargetItem.pomDependencies?.contains(
                        artifactId = selectedSourceItem.artifactId,
                        groupId = selectedSourceItem.groupId
                    ) == true
                ) {
                    val teUpdatenDependency = selectedTargetItem.pomDependencies?.firstOrNull {
                        it.artifactId == selectedSourceItem.artifactId &&
                                it.groupId == selectedSourceItem.groupId
                    }
                    teUpdatenDependency?.let {
                        logger.info("Update dependency ${it.artifactId} from ${it.version} to ${selectedSourceItem.version} in pomFile ${selectedTargetItem.file.canonicalFile}")
                        updateDependency(
                            pomFileToUpdate = selectedTargetItem,
                            targetArtifactId = selectedSourceItem.artifactId,
                            targetGroupId = selectedSourceItem.groupId,
                            targetVersion = selectedSourceItem.version
                        )
                    }
                }
            }
        }
    }

    private fun updateDependency(
        pomFileToUpdate: PomFile,
        targetArtifactId: String,
        targetGroupId: String,
        targetVersion: String
    ) {
        val teUpdatenDependency = pomFileToUpdate.pomDependencies?.firstOrNull {
            it.artifactId == targetArtifactId && it.groupId == targetGroupId
        }
            ?: throw Exception(
                "Dependency niet gevonden. file=${pomFileToUpdate.file.canonicalFile}; " +
                        "gezocht artifactId=$targetArtifactId; gezocht groupId=$targetGroupId"
            )

        val pomDocument = readXml(pomFileToUpdate.file)
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()
        val dependencyVersionXpath = teUpdatenDependency.versionXpath
        val versionNode = xPath.evaluate(dependencyVersionXpath, pomDocument, NODE) as Element?

        // Update version
        versionNode?.firstChild?.nodeValue = targetVersion

        // Wegschrijven
        logger.info("Write to file ${pomFileToUpdate.file.canonicalFile}")
        writeXml(
            pomFile = pomFileToUpdate.file,
            pomDocument = pomDocument
        )
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

    override fun afterPropertiesSet() {
        GlobalEventBus.eventBus.register(this)
    }

    private fun List<PomDependency>.contains(artifactId: String, groupId: String) =
        firstOrNull { it.artifactId == artifactId && it.groupId == groupId } != null
}