package nl.buildtool.services

import com.vaadin.flow.component.treegrid.TreeGrid
import nl.buildtool.model.PomFile
import org.springframework.stereotype.Service

@Service
class DependenciesUpdatesService {
    fun setupDependenciesUpdater(
        sourceGrid: TreeGrid<PomFile>,
        targetGrid: TreeGrid<PomFile>
    ) {
        start hier
    }
}