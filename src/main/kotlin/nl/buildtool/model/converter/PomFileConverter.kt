package nl.buildtool.model.converter

import nl.buildtool.model.PomDependency
import nl.buildtool.model.PomFile
import nl.buildtool.utils.*
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

object PomFileConverter {
    private val logger = LoggerFactory.getLogger(PomFileConverter::class.java)
    private val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    fun mapToPomFile(file: File): PomFile? {
        return try {
            val pomXmlDoc = readXml(file)

            return PomFile(
                artifactId = extractValue(pomXmlDoc, XPATH_ARTIFACT_ID),
                groupId = extractValue(pomXmlDoc, XPATH_GROUP_ID),
                version = extractVersion(pomXmlDoc),
                file = file,
                modules = extractModules(pomXmlDoc),
                pomDependencies = extractDependencies(pomXmlDoc)
            )
        } catch (e: Exception) {
            logger.error("XML Error. file=${file}, ${e.message}")
            null
        }
    }

    private fun extractModules(pomXmlDoc: Document) = extractListValue(pomXmlDoc, XPATH_MODULES)

    private fun extractDependencies(pomXmlDoc: Document) = extractDependencies(pomXmlDoc, XPATH_DEPENDENCIES)

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
        try {
            val xpath = XPathFactory.newInstance().newXPath()
            val expr = xpath.compile(xpathString)
            val nodes = expr.evaluate(pomXmlDoc, XPathConstants.NODESET) as NodeList?
            return if (nodes != null && nodes.length > 0) {
                val nodesList = mutableListOf<String>()
                for (i in 0..<nodes.length) {
                    nodesList.add(nodes.item(i).textContent)
                }
                nodesList
            } else {
                emptyList()
            }
        }catch (e:Exception){
            logger.error(e.message, e)
        }
        return emptyList()
    }

    private fun extractDependencies(pomXmlDoc: Document, xpathString: String): List<PomDependency> {
        val xpath = XPathFactory.newInstance().newXPath()
        val expr = xpath.compile(xpathString)
        val nodes = expr.evaluate(pomXmlDoc, XPathConstants.NODESET) as NodeList
        return if (nodes.length > 0) {
            // Dependencies
            val nodesList = mutableListOf<PomDependency>()
            for (i in 0..<nodes.length) {
                if (nodes.item(i) != null) {
                    // Dependency gevonden
                    val pomDependency = extractDependency(pomXmlDoc, nodes.item(i))
                    pomDependency?.let {
                        nodesList.add(it)
                    }
                }
            }
            nodesList
        } else {
            emptyList()
        }
    }

    private fun extractDependency(pomXmlDoc: Document, dependencyNode: Node): PomDependency? {
        logger.info("Dependency=${dependencyNode.nodeName}")
        if (dependencyNode.hasChildNodes()) {
            // Loop elements groupId, artifactId, version
            var groupId: String? = null
            var artifactId: String? = null

            for (i in 0..<dependencyNode.childNodes.length) {
                val currentElement = dependencyNode.childNodes.item(i)
                if (currentElement is Element) {
                    logger.info("${currentElement.nodeName} = ${currentElement.textContent}")

                    when (currentElement.nodeName) {
                        GROUP_ID -> groupId = currentElement.textContent
                        ARTIFACT_ID -> artifactId = currentElement.textContent
                    }
                }
            }

            return artifactId?.let {
                val version = bepaalVersion(pomXmlDoc, artifactId)

                val pomDependency = PomDependency(
                    groupId = groupId ?: throw Exception("geen groupId"),
                    artifactId = artifactId ?: throw Exception("geen artifactId"),
                    version = version.first,
                    versionXpath = version.second
                )
                return pomDependency
            }
        }
        return null
    }

    private fun bepaalVersion(pomXmlDoc: Document, artifactId: String): Pair<String, String> {
        var versionXpath =
            "(.//dependencies/dependency/artifactId[text()=\"${artifactId}\"]/following-sibling::version | " +
                    ".//dependencies/dependency/artifactId[text()=\"${artifactId}\"]/preceding-sibling::version)[1]"

        var versionText = extractValue(pomXmlDoc, versionXpath)
        logger.info("Pom Version for $artifactId is $versionText")

        val versionPropertyRegex = Regex("\\$\\{.*}")
        if (versionPropertyRegex.matches(versionText)) {
            // versionText is een property
            versionText = versionText.replace("\${", "")
                .replace("}", "")
            versionXpath = "//*[contains(local-name(), '$versionText')]"
            versionText = extractValue(pomXmlDoc, versionXpath)
        }
        return Pair(versionText, versionXpath)
    }


    fun readXml(xmlFile: File): Document {
        val xmlDoc: Document = documentBuilder.parse(xmlFile)
        xmlDoc.documentElement.normalize()
        return xmlDoc
    }
}