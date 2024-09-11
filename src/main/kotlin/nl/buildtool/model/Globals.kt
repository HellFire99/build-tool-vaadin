package nl.buildtool.model

object Globals {

    // A list of all the pom files
    var pomFileList = listOf<PomFile>()

    // stopOnError
    var stopOnError = false

    fun isStopOnError() = stopOnError

    fun List<PomFile>.deselectAll() = this.forEach { it.checked = false }
    fun List<PomFile>.selectAll() = this.forEach { it.checked = true }
    fun PomFile.selectChildren() {
        if (modulePoms.isNotEmpty()) {
            modulePoms.values.forEach { it.checked = true }
        }
    }

    fun PomFile.deselectChildren() {
        if (modulePoms.isNotEmpty()) {
            modulePoms.values.forEach { it.checked = false }
        }
    }
}