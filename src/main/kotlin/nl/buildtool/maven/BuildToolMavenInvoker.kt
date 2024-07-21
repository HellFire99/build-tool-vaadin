package nl.buildtool.maven

import nl.buildtool.model.BuildStatus
import nl.buildtool.model.Globals
import nl.buildtool.model.PomFile
import nl.buildtool.model.events.BuildingCompleteEvent
import nl.buildtool.model.events.ChangePomfileStatusEvent
import nl.buildtool.model.events.MavenLogEvent
import nl.buildtool.model.events.RefreshTableEvent
import nl.buildtool.utils.GlobalEventBus
import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future

@Component
class BuildToolMavenInvoker() {
    private val logger = LoggerFactory.getLogger(BuildToolMavenInvoker::class.java)
    private var mavenHome = initMavenHome()
    private var cancelled = false
    private var currentLatch: CountDownLatch = CountDownLatch(1)
    private var executor = Executors.newSingleThreadExecutor()
    private var future: Future<*>? = null

    private fun initMavenHome(): String {
        var mHome = System.getenv("M2_HOME")

        if (mHome == null) {
            mHome = System.getenv("MAVEN_HOME")
        }
        logger.info("Maven home found: $mHome")
        if (mHome == null) {
            val error = "No maven home found. Define a M2_HOME or MAVEN_HOME environment variable first. "
            logger.error(error)
            throw NullPointerException(error)
        }
        return mHome
    }

    fun invoke(pomFiles: List<PomFile>, targets: List<String>) {
        cancelled = false
        val invoker = DefaultInvoker()
        invoker.mavenHome = File(mavenHome)
        invoker.setOutputHandler {
            // TODO make async
            run {
                GlobalEventBus.eventBus.post(MavenLogEvent(it))
            }
        }

        // set all to queued
        pomFiles.forEach {
            it.status = BuildStatus.QUEUED
        }
        run {
            GlobalEventBus.eventBus.post(RefreshTableEvent())
        }

        // Start one by one
        pomFiles.forEach {
            val endStatus = invokePom(it, targets, invoker)

            if (endStatus != null && endStatus == BuildStatus.FAIL && Globals.isStopOnError()) {
                run {
                    GlobalEventBus.eventBus.post(BuildingCompleteEvent("Building stopped because previous build failed. "))
                }
                return
            }
        }

        run {
            GlobalEventBus.eventBus.post(BuildingCompleteEvent("Building complete. "))
        }
    }

    private fun invokePom(it: PomFile, targets: List<String>, invoker: DefaultInvoker): BuildStatus? {
        var endStatus: BuildStatus? = null
        if (!cancelled) {
            postMessage(null, "Execute ${it.artifactId}, target=$targets")
            currentLatch = CountDownLatch(1)
            future = executor.submit {
                endStatus = invoke(it, targets, invoker)
                currentLatch.countDown()
            }

            currentLatch.await()
        }
        return endStatus
    }

    private fun invoke(pomFile: PomFile, targets: List<String>, invoker: DefaultInvoker): BuildStatus {
        // set to building
        pomFile.start = LocalDateTime.now()
        pomFile.setBuildStatus(BuildStatus.BUILDING)
        postMessage(pomFile, "${pomFile.artifactId} status ${pomFile.status}")

        // Invoke maven
        val request = DefaultInvocationRequest()
        request.pomFile = pomFile.file
        request.goals = targets

        val result = invoker.execute(request)

        // Set result status
        pomFile.status = if (result.exitCode == 0)
            (BuildStatus.SUCCESS) else {
            BuildStatus.FAIL
        }
        pomFile.executionException = result.executionException
        pomFile.finished = LocalDateTime.now()
        pomFile.durationOfLastBuild = Duration.between(pomFile.start, pomFile.finished)
        postMessage(
            pomFile,
            "${pomFile.artifactId} status ${pomFile.status}. Duration was ${pomFile.durationOfLastBuild}"
        )

        return pomFile.status
    }

    fun cancelBuild() {
        cancelled = true
        if (future != null && !future!!.isDone) {
            future!!.cancel(true)
        }
        if (currentLatch.count == 1L) {
            currentLatch.countDown()
        }
    }

    private fun postMessage(pomfile: PomFile?, message: String) {
        logger.info(message)
        run {
            GlobalEventBus.eventBus.post(message)
        }
        if (pomfile != null) {
            run {
                GlobalEventBus.eventBus.post(
                    ChangePomfileStatusEvent(
                        pomfile.artifactId,
                        pomfile.version,
                        pomfile.status!!
                    )
                )
            }
        }

    }
}