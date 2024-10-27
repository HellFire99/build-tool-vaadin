package nl.buildtool.model

import org.apache.maven.shared.utils.cli.CommandLineException
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PomFile(
    val artifactId: String,
    val groupId: String,
    val version: String,
    val file: File,
    var checked: Boolean = false,
    var start: LocalDateTime? = null,
    var finished: LocalDateTime? = null,
    var durationOfLastBuild: Duration? = null,
    var status: BuildStatus = BuildStatus.NONE,
    var triggerReload: Boolean? = false,
    var modules: List<String>? = emptyList()
) {
    var executionException: CommandLineException? = null
    var modulePoms = mapOf<String, PomFile>()

    init {
        setBuildStatus(status)
    }

    fun isGitDir(): Boolean {
        return getGitDir().exists()
    }

    fun getGitDir(): File {
        val rootOfPom = file.parent
        return File("$rootOfPom/.git")
    }

    private fun determineIsGitDir(file: File): Boolean {
        val rootOfPom = file.parent
        val gitDir = File("$rootOfPom/.git")
        return gitDir.exists()
    }

    fun reset() {
        start = null
        finished = null
        durationOfLastBuild = null
        status = BuildStatus.NONE
    }

    fun setBuildStatus(status: BuildStatus) {
        this.status = status
    }

    fun getDurationOfLastBuildFormatted() = formatDuration(this.durationOfLastBuild)
    fun getStartFormatted() = this.start?.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    fun getFinishedFormatted() = this.finished?.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

    private

    fun formatDuration(durationOfLastBuild: Duration?): String {
        if (durationOfLastBuild == null) {
            return ""
        }
        val min = durationOfLastBuild.toMinutes().toString().padStart(2, '0')
        val sec = durationOfLastBuild.toSeconds().toString().padStart(2, '0')
        var ms = durationOfLastBuild.toMillis().toString().padStart(2, '0')
        if (ms.length > 2) {
            ms = ms.substring(0, 2)
        }

        return "${min}:${sec}:${ms}"

    }

    override fun toString(): String {
        return "PomFile(name='$artifactId', version='$version', file=$file, dir=${file.parent}, modules=${modules?.joinToString()})"
    }
}