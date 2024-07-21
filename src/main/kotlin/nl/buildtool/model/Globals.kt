package nl.buildtool.model

object Globals {

    // A list of all the pom files
    var pomFileList = listOf<PomFile>()

    // stopOnError
    var stopOnError = false

    fun isStopOnError() = stopOnError

}