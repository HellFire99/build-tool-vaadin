package nl.buildtool.git

import nl.buildtool.model.GitCommand
import nl.buildtool.model.PomFile
import nl.buildtool.model.timeoutAmount
import nl.buildtool.model.timeoutUnit
import nl.buildtool.utils.ExtensionFunctions.logEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.util.concurrent.TimeUnit


@Component
class GitExecutor {

    private val logger = LoggerFactory.getLogger(GitExecutor::class.java)
    fun executeGitPull(selectedPomFiles: List<PomFile>, selected: Boolean) {
        if (selected) {
            executeGitPull(selectedPomFiles);
        }
    }

    private fun executeGitPull(selectedPomFiles: List<PomFile>) {
        selectedPomFiles.forEach {
            // Check if a .git dir is present in the pom dir.
            val rootOfPom = File(it.file.parent).absolutePath
            if (it.isGitDir()) {
                logger.info("GIT dir is ${it.getGitDir()}")

                logEvent(" ")
                logEvent("==> Starting GIT Pull on $rootOfPom ")
                runGitCommand(GitCommand.PULL, it.getGitDir())
                logEvent("==> GIT Pull on $rootOfPom Complete")
            } else {
                logEvent("$rootOfPom is not a GIT directory. Skipping GIT Pull. ")
            }
        }
    }

    fun runGitCommand(gitCommand: GitCommand, gitDir: File) {
        try {
            val parts = gitCommand.command.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(gitDir.parentFile)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
                .also { it.waitFor(timeoutAmount, timeoutUnit) }

            logEvent(proc.inputStream.bufferedReader().readText())
            logEvent(proc.errorStream.bufferedReader().readText())
        } catch (e: Exception) {
            logger.error(e.message, e)
            logEvent("Exception while performing git-pull. ${e.message}")
        }
    }
}

fun PomFile.isGitBranchOf(gewensteBranchNaam: String): Boolean {
    val currentDir = File(this.file.parent)
    return if (currentDir.isDirectory) {
        val branchNaam = GitCommand.SHOW_CURRENT_BRANCH.command.runCommand(workingDir = currentDir)
        return branchNaam?.contains(gewensteBranchNaam) ?: false
    } else {
        false
    }
}

fun PomFile.getGitBranchName(): String? {
    val currentDir = File(this.file.parent)
    return if (currentDir.isDirectory) {
        GitCommand.SHOW_CURRENT_BRANCH.command.runCommand(workingDir = currentDir)
    } else {
        null
    }
}

private fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String? = runCatching {
    ProcessBuilder("\\s".toRegex().split(this))
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start().also { it.waitFor(timeoutAmount, timeoutUnit) }
        .inputStream.bufferedReader().readText()
}.onFailure { logEvent(it.toString()) }.getOrNull()