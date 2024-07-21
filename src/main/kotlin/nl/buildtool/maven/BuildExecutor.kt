package nl.buildtool.maven

import nl.buildtool.model.BuildStatus
import nl.buildtool.model.Globals
import nl.buildtool.model.PomFile
import nl.buildtool.model.events.BuildingCompleteEvent
import nl.buildtool.model.events.RefreshTableEvent
import nl.buildtool.utils.ExtensionFunctions.post
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

@Component
class BuildExecutor(val buildToolMavenInvoker: BuildToolMavenInvoker) {
    private val logger = LoggerFactory.getLogger(BuildExecutor::class.java)
    var thread: Thread? = null

    fun executeBuild(
        pomFileList: List<PomFile>,
        mavenTargetList: List<String>
    ) {
        Globals.pomFileList.forEach { it.reset() }
        pomFileList.forEach { it.setBuildStatus(BuildStatus.QUEUED) }
        post(RefreshTableEvent())

        val pomFiles = pomFileList
            .filter { it.checked }
        if (pomFiles.isEmpty()) {
            logger.info("Nothing to build. ")
            post(BuildingCompleteEvent("Nothing to build.  "))
            return
        }
        if (mavenTargetList.isEmpty()) {
            logger.info("No targets selected. ")
            post(BuildingCompleteEvent("No targets selected. "))
            return
        }

        thread = thread(start = true) {
            buildToolMavenInvoker.invoke(pomFiles, mavenTargetList)
        }
    }

    fun cancelBuild() {
        buildToolMavenInvoker.cancelBuild()
        if (this.thread != null) {
            this.thread!!.interrupt()
        }
    }

}