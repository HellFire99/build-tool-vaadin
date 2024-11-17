package nl.buildtool.utils

import nl.buildtool.model.converter.PomFileConverter.mapToPomFile
import nl.buildtool.model.converter.PomFileConverter.readXml
import nl.buildtool.utils.FileUtil.getFile
import nl.buildtool.utils.UpdatePomVersionUtil.Companion.getPomVersionNode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.deleteIfExists

class UpdatePomVersionUtilTest {
    private lateinit var updatePomVersionUtil: UpdatePomVersionUtil
    private val pomMetParentFileName = "pom_met_parent.xml"
    private val pomZonderParentFileName = "pom_zonder_parent.xml"

    private val pomMetParentBackupFileName = "${pomMetParentFileName}_backup"
    private val pomZonderParentBackupFileName = "${pomZonderParentFileName}_backup"

    private lateinit var pomMetParentXmlFile: File
    private lateinit var pomZonderParentXmlFile: File

    @BeforeEach
    fun beforeEach1() {
        deleteExistingFiles()
    }

    @BeforeEach
    fun beforeEach2() {
        updatePomVersionUtil = UpdatePomVersionUtil()
        // Copy pom files for test
        pomMetParentXmlFile = getFile(pomMetParentFileName)
        pomZonderParentXmlFile = getFile(pomZonderParentFileName)
        val dir = pomMetParentXmlFile.parent

        pomMetParentXmlFile.copyTo(
            File("$dir/$pomMetParentBackupFileName")
        )
        pomZonderParentXmlFile.copyTo(
            File("$dir/$pomZonderParentBackupFileName")
        )
    }

    @AfterEach
    fun afterEach() {
        // Deletye copied pomfiles
        deleteExistingFiles()
    }

    @Test
    fun `test zetPrefixInPomDocumentVersion`() {
        // Prepare
        val pomFile = mapToPomFile(pomMetParentXmlFile)
            ?: throw Exception("Fout in junit test. Kon ${pomMetParentXmlFile.absolutePath} niet lezen.")
        val pomDocument = readXml(pomFile.file)
        val startVersionText = getPomVersionNode(pomDocument)?.firstChild?.textContent
        val prefix = "jUnit"

        // Test
        updatePomVersionUtil.zetPrefixInPomDocumentVersion(
            pomDocument = pomDocument,
            gewenstePomVersionPrefix = "jUnit"
        )

        // Verify
        val naReplaceVersionNode = getPomVersionNode(pomDocument)
        assertThat(naReplaceVersionNode?.firstChild?.nodeValue).isEqualTo("${prefix}-$startVersionText")
    }

    private fun deleteExistingFiles() {
        val resourcePomMetParentBackup = this::class.java.getResource("/$pomMetParentBackupFileName")?.toURI()
        val resourcePomZonderParentBackup = this::class.java.getResource("/$pomZonderParentBackupFileName")?.toURI()

        resourcePomMetParentBackup?.let {
            val mainPath = Paths.get(it).toString()
            Paths.get(mainPath).deleteIfExists()
        }

        resourcePomZonderParentBackup?.let {
            val mainPath = Paths.get(it).toString()
            Paths.get(mainPath).deleteIfExists()
        }
    }
}