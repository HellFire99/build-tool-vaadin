package nl.buildtool.views.model

import com.vaadin.flow.component.treegrid.TreeGrid
import nl.buildtool.model.PomFile
import org.springframework.stereotype.Component

@Component
class ViewModel {
    final lateinit var buildViewGrid: TreeGrid<PomFile>

    final lateinit var sourceGrid: TreeGrid<PomFile>
        private set

    final lateinit var targetGrid: TreeGrid<PomFile>
        private set

    fun init(sourceGrid: TreeGrid<PomFile>, targetGrid: TreeGrid<PomFile>) {
        this.sourceGrid = sourceGrid
        this.targetGrid = targetGrid
    }
}