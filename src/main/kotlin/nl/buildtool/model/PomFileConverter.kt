package nl.buildtool.model

import nl.buildtool.utils.*
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
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
                modules = extractModules(pomXmlDoc)
            )
        } catch (e: Exception) {
            logger.error("XML Error. file=${file}, ${e.message}")
            null
        }
    }

    fun extractModules(pomXmlDoc: Document): List<String> {
        val modules = extractListValue(pomXmlDoc, XPATH_MODULES)
        return modules
    }

    fun extractVersion(pomXmlDoc: Document): String {
        var version: String? = extractValue(pomXmlDoc, XPATH_VERSION)
        if (version.isNullOrBlank()) {
            version = extractValue(pomXmlDoc, XPATH_VERSION_PARENT)
        }
        return version
    }


    fun extractValue(pomXmlDoc: Document, xpathString: String): String {
        val xpath = XPathFactory.newInstance().newXPath()
        val expr = xpath.compile(xpathString)
        val nodes = expr.evaluate(pomXmlDoc, XPathConstants.NODESET) as NodeList
        return if (nodes.item(0) != null) {
            nodes.item(0).textContent
        } else {
            ""
        }
    }

    fun extractListValue(pomXmlDoc: Document, xpathString: String): List<String> {
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
//
//    private fun readXml(pomFile: File): Document {
//        val dbFactory = DocumentBuilderFactory.newInstance()
//        val dBuilder = dbFactory.newDocumentBuilder()
//        val xmlInput = InputSource(StringReader(pomFile.readText()))
//        return dBuilder.parse(xmlInput)
//    }
}