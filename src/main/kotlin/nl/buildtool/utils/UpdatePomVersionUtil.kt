package nl.buildtool.utils

import com.google.common.base.Stopwatch
import nl.buildtool.git.getGitBranchName
import nl.buildtool.model.*
import nl.buildtool.model.converter.PomFileConverter.readXml
import nl.buildtool.model.events.RefreshTableEvent
import nl.buildtool.utils.ExtensionFunctions.logEvent
import nl.buildtool.utils.ExtensionFunctions.post
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants.NODE
import javax.xml.xpath.XPathFactory

@Component
class UpdatePomVersionUtil {
    @Value("\${root:.}")
    lateinit var root: String

    companion object {
        private val jireNrRegexp = Regex("(?i)PSHV-[0-9]*")

        fun getPomVersion(doc: Document): String {
            val xpFactory = XPathFactory.newInstance()
            val xPath = xpFactory.newXPath()
            return xPath.evaluate(XPATH_VERSION, doc)
        }

        fun getPomVersionNode(doc: Document): Element? {
            val xpFactory = XPathFactory.newInstance()
            val xPath = xpFactory.newXPath()
            return xPath.evaluate(XPATH_VERSION, doc, NODE) as Element?
        }
    }

    fun updatePoms(updatePomsParameters: UpdatePomsParameters) {
        val teUpdatenPomFiles = bepaalTeUpdatenPomFiles(updatePomsParameters)

        val prefix = if (updatePomsParameters.autoDetectCustomOrReset == RADIO_VALUE_CUSTOM_PREFIX) {
            updatePomsParameters.customPrefixTextfield
        } else {
            null
        }

        this.updatePoms(
            jiraNr = prefix, // Null prefix betekent auto-detect
            autoDetectBranchNames = updatePomsParameters.autoDetectCustomOrReset == RADIO_VALUE_AUTO_DETECT,
            teUpdatenPomFiles = teUpdatenPomFiles
        )
    }

    fun resetPoms(updatePomsParameters: UpdatePomsParameters) {
        logEvent("Resetting pomfile prefixes...")

        val teUpdatenPomFiles = bepaalTeUpdatenPomFiles(updatePomsParameters)

        teUpdatenPomFiles.forEach { pomFile ->
            val gitBranchJiraNr = determineJiraNrByBranchName(pomFile)
            gitBranchJiraNr?.let { myGitBranchJiraNr ->
                // Als gitBranchJiraNr voorkomt in de pom.version, wijzig dan de pom.version
                // verwijder gitBranchJiraNr uit de pom.version
                val pomDocument = readXml(pomFile.file)
                verwijderPrefixInPomDocumentVersion(
                    pomDocument = pomDocument,
                    teVerwijderenPrefix = myGitBranchJiraNr
                )
                writeXml(pomFile.file, pomDocument)
                pomFile.triggerReload = true
            }
        }

        logEvent("Done resetting pomfile prefixes.")

        // trigger reload when poms have been updated and reset reloadTrigger
        doReloadPomList(updatePomsParameters.selectedPomFiles?.toSet().orEmpty())
    }


    fun zetPrefixInPomDocumentVersion(pomDocument: Document, gewenstePomVersionPrefix: String) {
        val versionNode = getPomVersionNode(pomDocument)

        versionNode?.let {
            val version = versionNode.firstChild.nodeValue
            version.let {
                if (!version.contains(gewenstePomVersionPrefix)) {
                    // Deze moeten we updaten
                    val newVersion = "$gewenstePomVersionPrefix-$version"
                    versionNode.firstChild.nodeValue = newVersion
                    logEvent(" --> Nieuwe pom versie: $newVersion")
                    pomDocument.normalizeDocument()
                }
            }
        }
    }

    fun verwijderPrefixInPomDocumentVersion(pomDocument: Document, teVerwijderenPrefix: String) {
        val versionNode = getPomVersionNode(pomDocument)

        versionNode?.let {
            val version = versionNode.firstChild.nodeValue
            version.let {
                if (version.contains(teVerwijderenPrefix)) {
                    // Deze moeten we updaten
                    val newVersion = version.replace("$teVerwijderenPrefix-", "")
                    versionNode.firstChild.nodeValue = newVersion
                    logEvent(" --> Nieuwe pom versie: $newVersion")
                    pomDocument.normalizeDocument()
                }
            }
        }
    }

    private fun updatePoms(
        jiraNr: String? = "",
        autoDetectBranchNames: Boolean? = false,
        teUpdatenPomFiles: Set<PomFile>
    ) {
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
            if (it) {
                logEvent("Auto-detecting branch names")
            }
        }

        teUpdatenPomFiles.forEach { pomFile ->
            val myJiraNr = jiraNr ?: determineJiraNrByBranchName(pomFile)
            myJiraNr?.let {
                updatePomVersie(pomFile, it)
            }
        }
        logEvent("done: ${stopwatch.stop().elapsed()}ms ")

        // trigger reload when poms have been updated and reset reloadTrigger
        doReloadPomList(teUpdatenPomFiles)
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

    private fun doReloadPomList(pomFiles: Set<PomFile>) {
        if (pomFiles.any { it.triggerReload == true }) {
            logEvent("Afvuren RefreshTableEvent")
            post(RefreshTableEvent(true))
            pomFiles.filter { it.triggerReload == true }.map { it.triggerReload = false }
        }
    }

    private fun updatePomVersie(pomFile: PomFile, gewenstePomVersionPrefix: String) {
        // Begint de pom.project.version met het gewensteJiraNr ?
        val pomDocument = readXml(pomFile.file)
        val pomVersion = getPomVersion(pomDocument)

        // nee, update pom versie
        if (!pomVersion.contains(gewenstePomVersionPrefix, true)) {
            logEvent(" --> Pom version: ${pomVersion}. Update pomVersion...")
            zetPrefixInPomDocumentVersion(
                pomDocument = pomDocument,
                gewenstePomVersionPrefix = gewenstePomVersionPrefix
            )
            writeXml(pomFile.file, pomDocument)
            pomFile.triggerReload = true
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

    private fun bepaalTeUpdatenPomFiles(updatePomsParameters: UpdatePomsParameters): Set<PomFile> {
        val teUpdatenPomFiles = if (updatePomsParameters.pomFileSelectRadioValue == RADIO_VALUE_SELECTION &&
            updatePomsParameters.selectedPomFiles?.isNotEmpty() == true
        ) {
            updatePomsParameters.selectedPomFiles.toSet()
        } else {
            val allePomfiles = Globals.pomFileList.toMutableSet()
            allePomfiles.addAll(allePomfiles.flatMap { it.modulePoms.values })
            allePomfiles
        }
        return teUpdatenPomFiles
    }


}