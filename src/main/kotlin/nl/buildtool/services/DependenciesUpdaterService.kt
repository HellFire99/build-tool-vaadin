package nl.buildtool.services

import com.google.common.eventbus.Subscribe
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.treegrid.TreeGrid
import nl.buildtool.model.PomDependency
import nl.buildtool.model.PomFile
import nl.buildtool.model.events.PomFileDeselectedEvent
import nl.buildtool.model.events.PomFileSelectedEvent
import nl.buildtool.utils.GlobalEventBus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service

@Service
class DependenciesUpdatesService : InitializingBean {
    private val logger = LoggerFactory.getLogger(DependenciesUpdatesService::class.java)
    private lateinit var sourceGrid: TreeGrid<PomFile>
    private lateinit var targetGrid: TreeGrid<PomFile>
    private lateinit var ui: UI

    fun setupDependenciesUpdater(
        sourceGrid: TreeGrid<PomFile>,
        targetGrid: TreeGrid<PomFile>,
        ui: UI
    ) {
        this.sourceGrid = sourceGrid
        this.targetGrid = targetGrid
        this.ui = ui
    }

    @Subscribe
    private fun subscribe(event: PomFileDeselectedEvent) {
        logger.info("PomFileDeselectedEvent ontvangen. ${event.pomFile.artifactId}")
        ui.access {
            // TODO opzoeken dependencies obv artifactId/groupId

        }
    }

    @Subscribe
    private fun subscribe(event: PomFileSelectedEvent) {
        logger.info("PomFileSelectedEvent ontvangen. ${event.pomFile.artifactId}")
        // zoek in target obv artifactId en groupId
        ui.access {
            // TODO opzoeken dependencies obv artifactId/groupId
            val dependentPomFiles = findPomFiles(
                treeGrid = targetGrid,
                artifactId = event.pomFile.artifactId,
                groupId = event.pomFile.groupId
            )
            dependentPomFiles.let {
                dependentPomFiles.forEach { dependentPomFile ->
                    targetGrid.select(dependentPomFile)
                }
            }
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
        val dependencyAanwezig = pomFile.pomDependencies?.firstOrNull { pomDependency ->
            pomDependency.artifactId == artifactId && pomDependency.groupId == groupId
        }
        return dependencyAanwezig
    }

    override fun afterPropertiesSet() {
        GlobalEventBus.eventBus.register(this)
    }
}