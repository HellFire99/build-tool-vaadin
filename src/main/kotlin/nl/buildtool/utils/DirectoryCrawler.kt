package nl.buildtool.utils


import nl.buildtool.maven.BuildToolMavenInvoker
import nl.buildtool.model.PomFile
import nl.buildtool.model.converter.PomFileConverter.mapToPomFile
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File

const val XPATH_ARTIFACT_ID = "/project/artifactId"
const val XPATH_VERSION = "/project/version"
const val XPATH_VERSION_PARENT = "/project/parent/version"
const val XPATH_GROUP_ID = "/project/groupId"
const val XPATH_MODULES = "/project/modules/module"
const val XPATH_DEPENDENCIES = "/project/dependencies/dependency"
const val GROUP_ID = "groupId"
const val ARTIFACT_ID = "artifactId"
const val VERSION = "version"

@Component
class DirectoryCrawler {
    private val logger = LoggerFactory.getLogger(BuildToolMavenInvoker::class.java)

    @Value("\${root:.}")
    lateinit var root: String

    fun getPomFileList(): List<PomFile> {
        logger.info("Searching pom files in $root")
        val pomFilesFound = File(root).walkTopDown()
            .maxDepth(3)
            .filter { it.name == "pom.xml" }
            .map { mapToPomFile(it) }
            .filterNotNull()
            .toList()
            .sortedBy { it.artifactId }
        pomFilesFound.forEach { logger.info(it.toString()) }

        val pomFilesMetModules = createModulePoms(pomFilesFound)
        logger.info("${pomFilesFound.size} Pom files found. ")
        logger.info("${pomFilesMetModules.size} Pom files met modules found. ")
        return pomFilesMetModules
    }

    private fun createModulePoms(pomFiles: List<PomFile>): List<PomFile> {
        val pomFilesMetModules = pomFiles.filter { it.modules?.isNotEmpty() == true }
        pomFilesMetModules.forEach {
            val moduleParentPomDir = it.file.parent
            val moduleMap = mutableMapOf<String, PomFile>()
            it.modules?.forEach { moduleNaam ->
                val gezochteModulePomDirNaam = "$moduleParentPomDir${File.separator}$moduleNaam"
                val gezochteModulePomFile =
                    pomFiles.firstOrNull { pomFile -> pomFile.file.parent == gezochteModulePomDirNaam }
                gezochteModulePomFile?.let {
                    moduleMap[moduleNaam] = it
                }
            }

            it.modulePoms = moduleMap
        }

        // Filter de pomFiles die voorkomen in een module
        val moduleDirectories = pomFilesMetModules.flatMap { it.modulePoms.values }.map { it.file.parent }
        val pomFilesDieGeenModuleZijn =
            pomFiles.filterNot { pomFile -> moduleDirectories.contains(pomFile.file.parent) }
        return pomFilesDieGeenModuleZijn
    }

}