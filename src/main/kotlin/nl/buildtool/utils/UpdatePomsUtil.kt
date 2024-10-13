package nl.buildtool.utils

import com.google.common.base.Stopwatch
import nl.buildtool.git.getGitBranchName
import nl.buildtool.git.isGitBranchOf
import nl.buildtool.model.Globals
import nl.buildtool.model.PomFile
import nl.buildtool.model.events.RefreshTableEvent
import nl.buildtool.utils.ExtensionFunctions.logEvent
import nl.buildtool.utils.ExtensionFunctions.post
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathFactory

@Component
class UpdatePomsUtil {
    private val logger = LoggerFactory.getLogger(UpdatePomsUtil::class.java)

    @Value("\${root:.}")
    lateinit var root: String

    private val jireNrRegexp = Regex("PSHV-[0-9]*")

    fun updatePoms(jiraNr: String? = "", autoDetectBranchNames: Boolean? = false) {
        val stopwatch = Stopwatch.createStarted()

        if (jiraNr == null && autoDetectBranchNames == null) {
            logEvent("No JiraNr and auto-detect is empty")
            return
        }

        logEvent("Setting pom versions to $jiraNr for selected poms... ")
        logEvent("Folder: $root")
        jiraNr?.let {
            logEvent("Setting to static JiraNr $jiraNr")
        }
        autoDetectBranchNames?.let {
            logEvent("Auto-detecting branch names")
        }

        val pomFiles = Globals.pomFileList
        pomFiles.forEach { pomFile ->
            val myJiraNr = jiraNr ?: determineJiraNrByBranchName(pomFile)
            myJiraNr?.let {
                if (pomFile.isGitBranchOf(it)) {
                    logEvent("$it is een $it branch")
                    updatePomVersie(pomFile, it)
                }
            }

        }
        logEvent("done: ${stopwatch.stop().elapsed()}ms ")

        // trigger reload when poms have been updated and reset reloadTrigger
        doReloadPomList(pomFiles)
    }

    private fun determineJiraNrByBranchName(pomFile: PomFile): String? {
        val gitBranchName = pomFile.getGitBranchName()
        return gitBranchName?.let {
            if (jireNrRegexp.containsMatchIn(it)) {
                jireNrRegexp.find(it)?.value
            } else {
                null
            }
        }
    }

    private fun doReloadPomList(pomFiles: List<PomFile>) {
        if (pomFiles.any { it.triggerReload == true }) {
            post(RefreshTableEvent(true))
            pomFiles.filter { it.triggerReload == true }.map { it.triggerReload = false }
        }
    }

    private fun updatePomVersie(pomFile: PomFile, gewensteBranchNaam: String) {
        // Begint de pom.project.version met het gewensteJiraNr ?
        val pomDocument = readXml(pomFile.file)
        val pomVersion = getPomVersion(pomDocument)

        // nee, update pom versie
        if (!pomVersion.contains(gewensteBranchNaam, true)) {
            logEvent(" --> Pom version: ${pomVersion}. Update pomVersion...")
            updatePomVersie(pomFile, pomDocument, pomVersion, gewensteBranchNaam)
            pomFile.triggerReload = true
        }
    }

    private fun readXml(pomFile: File): Document {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val xmlInput = InputSource(StringReader(pomFile.readText()))
        return dBuilder.parse(xmlInput)
    }

    private fun updatePomVersie(pomFile: PomFile, pomDocument: Document, pomVersion: String, jiraNr: String) {
        val versions = pomDocument.getElementsByTagName("version")

        // de 2e voorkomende version is de pom.versie die we zoeken
        val versie = versions.item(1) as Element?
        val waarde = versie?.firstChild?.textContent
        waarde?.let {
            if (waarde == pomVersion && !waarde.contains(jiraNr)) {
                // Deze moeten we updaten
                versie.textContent = "$jiraNr-$waarde"
                logEvent(" --> NIEUWE POM VERSIE: ${versie.textContent}")
                pomDocument.normalizeDocument()
                writeXml(pomFile.file, pomDocument)
            }
        }
    }

    private fun writeXml(pomFile: File, pomDocument: Document) {
        pomDocument.xmlStandalone = true
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        val source = DOMSource(pomDocument)
        val result = StreamResult(pomFile)
        transformer.transform(source, result)
        logEvent(" --> POM file Created: $pomFile")
    }

    private fun getPomVersion(doc: Document): String {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()
        return xPath.evaluate(XPATH_VERSION, doc)
    }

}