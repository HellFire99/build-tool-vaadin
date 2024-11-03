package nl.buildtool.utils

import nl.buildtool.model.PomFileConverter.mapToPomFile
import nl.buildtool.model.PomFileConverter.readXml
import nl.buildtool.utils.UpdatePomsUtil.Companion.getPomVersionNode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.deleteIfExists

class UpdatePomsUtilTest {
    private lateinit var updatePomsUtil: UpdatePomsUtil
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
        updatePomsUtil = UpdatePomsUtil()
        // Copy pom files for test
        val resourcePomMetParent = this::class.java.getResource("/$pomMetParentFileName")!!
        val resourcePomZonderParent = this::class.java.getResource("/$pomZonderParentFileName")!!

        pomMetParentXmlFile = File(resourcePomMetParent.path)
        pomZonderParentXmlFile = File(resourcePomZonderParent.path)
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
        updatePomsUtil.zetPrefixInPomDocumentVersion(
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