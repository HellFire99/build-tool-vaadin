package nl.buildtool.utils


import nl.buildtool.maven.BuildToolMavenInvoker
import nl.buildtool.model.PomFile
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

const val XPATH_ARTIFACT_ID = "/project/artifactId"
const val XPATH_VERSION = "/project/version"
const val XPATH_VERSION_PARENT = "/project/parent/version"
const val XPATH_GROUP_ID = "/project/groupId"
const val XPATH_MODULES = "/project/modules/module"

@Component
class DirectoryCrawler {
    private val logger = LoggerFactory.getLogger(BuildToolMavenInvoker::class.java)

    @Value("\${root:.}")
    lateinit var root: String

    private val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

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
                    pomFiles.first { pomFile -> pomFile.file.parent == gezochteModulePomDirNaam }
                moduleMap[moduleNaam] = gezochteModulePomFile
            }

            it.modulePoms = moduleMap
        }

        // Filter de pomFiles die voorkomen in een module
        val moduleDirectories = pomFilesMetModules.flatMap { it.modulePoms.values }.map { it.file.parent }
        val pomFilesDieGeenModuleZijn =
            pomFiles.filterNot { pomFile -> moduleDirectories.contains(pomFile.file.parent) }
        return pomFilesDieGeenModuleZijn
    }

    private fun mapToPomFile(file: File): PomFile? {
        return try {
            val pomXmlDoc = readXml(file)

            return PomFile(
                artifactId = extractValue(pomXmlDoc, XPATH_ARTIFACT_ID),
                groupId = extractValue(pomXmlDoc, XPATH_GROUP_ID),
                version = extractVersion(pomXmlDoc),
                file = file,
                modules = extractModules(pomXmlDoc)
            )
        } catch (e: Exception) {
            logger.error("XML Error. file=${file}, ${e.message}")
            null
        }
    }

    private fun extractModules(pomXmlDoc: Document): List<String> {
        val modules = extractListValue(pomXmlDoc, XPATH_MODULES)
        return modules
    }

    private fun extractVersion(pomXmlDoc: Document): String {
        var version: String? = extractValue(pomXmlDoc, XPATH_VERSION)
        if (version.isNullOrBlank()) {
            version = extractValue(pomXmlDoc, XPATH_VERSION_PARENT)
        }
        return version
    }

    private fun extractValue(pomXmlDoc: Document, xpathString: String): String {
        val xpath = XPathFactory.newInstance().newXPath()
        val expr = xpath.compile(xpathString)
        val nodes = expr.evaluate(pomXmlDoc, XPathConstants.NODESET) as NodeList
        return if (nodes.item(0) != null) {
            nodes.item(0).textContent
        } else {
            ""
        }
    }

    private fun extractListValue(pomXmlDoc: Document, xpathString: String): List<String> {
        val xpath = XPathFactory.newInstance().newXPath()
        val expr = xpath.compile(xpathString)
        val nodes = expr.evaluate(pomXmlDoc, XPathConstants.NODESET) as NodeList
        return if (nodes.length == 1 && nodes.item(0) != null) {
            listOf(nodes.item(0).textContent)
        } else if (nodes.length > 1) {
            val nodesList = mutableListOf<String>()
            for (i in 0..nodes.length - 1) {
                nodesList.add(nodes.item(i).textContent)
            }
            nodesList
        } else {
            emptyList()
        }
    }

    fun readXml(xmlFile: File): Document {
        val xmlDoc: Document = documentBuilder.parse(xmlFile)
        xmlDoc.documentElement.normalize()
        return xmlDoc
    }
}