package nl.buildtool.model

data class PomDependency(
    val artifactId: String,
    val groupId: String,
    var version: String,
    var versionXpath: String
)