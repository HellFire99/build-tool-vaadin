package nl.buildtool.model.converter

import nl.buildtool.model.converter.PomFileConverter.mapToPomFile
import nl.buildtool.model.converter.PomFileConverter.readXml
import nl.buildtool.utils.FileUtil.getFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.w3c.dom.Element
import javax.xml.xpath.XPathConstants.NODE
import javax.xml.xpath.XPathFactory

class PomFileConverterTest {

    private val dependencyTestPom1 = "dependencyTestPom1.xml"
    private val dependencyTestPom2 = "dependencyTestPom2.xml"
    private val dependencyTestPom3 = "dependencyTestPom3.xml"
    private val dependencyTestPom4 = "dependencyTestPom4.xml"

    val expectedXpath1 =
        "(.//dependencies/dependency/artifactId[text()=\"service-b\"]/following-sibling::version | " +
                ".//dependencies/dependency/artifactId[text()=\"service-b\"]/preceding-sibling::version)[1]"
    val expectedXPath2 = "//*[contains(local-name(), 'service-b.version')]"

    @Test
    fun `test inlezen pomfile met dependency en directe version`() {
        // Prepare
        val file = getFile(dependencyTestPom1)

        // Test
        val pomFile = mapToPomFile(file)

        // Verify
        assertThat(pomFile?.version).isEqualTo("2.0-SNAPSHOT")
        assertThat(pomFile?.groupId).isEqualTo("nl.service.a")
        assertThat(pomFile?.artifactId).isEqualTo("service-a")
        assertThat(pomFile?.pomDependencies?.size).isEqualTo(1)

        assertThat(pomFile?.pomDependencies?.get(0)?.groupId).isEqualTo("nl.service.b")
        assertThat(pomFile?.pomDependencies?.get(0)?.artifactId).isEqualTo("service-b")
        assertThat(pomFile?.pomDependencies?.get(0)?.version).isEqualTo("1.01-SNAPSHOT")
        assertThat(pomFile?.pomDependencies?.get(0)?.versionXpath).isEqualTo(expectedXpath1)
    }

    @Test
    fun `test inlezen pomfile met dependency en meerdere dependencies`() {
        // Prepare
        val file = getFile(dependencyTestPom2)

        // Test
        val pomFile = mapToPomFile(file)

        // Verify
        assertThat(pomFile?.version).isEqualTo("2.0-SNAPSHOT")
        assertThat(pomFile?.groupId).isEqualTo("nl.service.a")
        assertThat(pomFile?.artifactId).isEqualTo("service-a")
        assertThat(pomFile?.pomDependencies?.size).isEqualTo(1)

        assertThat(pomFile?.pomDependencies?.get(0)?.groupId).isEqualTo("nl.service.b")
        assertThat(pomFile?.pomDependencies?.get(0)?.artifactId).isEqualTo("service-b")
        assertThat(pomFile?.pomDependencies?.get(0)?.version).isEqualTo("1.01-SNAPSHOT")
        assertThat(pomFile?.pomDependencies?.get(0)?.versionXpath).isEqualTo(expectedXpath1)
    }

    @Test
    fun `test inlezen pomfile met dependency en meerdere dependencies en version als property`() {
        // Prepare
        val file = getFile(dependencyTestPom3)

        // Test
        val pomFile = mapToPomFile(file)

        // Verify
        assertThat(pomFile?.version).isEqualTo("2.0-SNAPSHOT")
        assertThat(pomFile?.groupId).isEqualTo("nl.service.a")
        assertThat(pomFile?.artifactId).isEqualTo("service-a")
        assertThat(pomFile?.pomDependencies?.size).isEqualTo(1)

        assertThat(pomFile?.pomDependencies?.get(0)?.groupId).isEqualTo("nl.service.b")
        assertThat(pomFile?.pomDependencies?.get(0)?.artifactId).isEqualTo("service-b")
        assertThat(pomFile?.pomDependencies?.get(0)?.version).isEqualTo("1.01-SNAPSHOT")
        assertThat(pomFile?.pomDependencies?.get(0)?.versionXpath).isEqualTo(expectedXPath2)
    }

    @Test
    fun `test inlezen pomfile met dependency en meerdere dependencies en version als property v2`() {
        // Prepare
        val file = getFile(dependencyTestPom4)

        // Test
        val pomFile = mapToPomFile(file)

        // Verify
        assertThat(pomFile?.version).isEqualTo("2.0-SNAPSHOT")
        assertThat(pomFile?.groupId).isEqualTo("nl.service.a")
        assertThat(pomFile?.artifactId).isEqualTo("service-a")
        assertThat(pomFile?.pomDependencies?.size).isEqualTo(1)

        assertThat(pomFile?.pomDependencies?.get(0)?.groupId).isEqualTo("nl.service.b")
        assertThat(pomFile?.pomDependencies?.get(0)?.artifactId).isEqualTo("service-b")
        assertThat(pomFile?.pomDependencies?.get(0)?.version).isEqualTo("1.01-SNAPSHOT")
        assertThat(pomFile?.pomDependencies?.get(0)?.versionXpath).isEqualTo(expectedXPath2)
    }

    @Test
    fun `test update dependency version`() {
        // Prepare
        val file = getFile(dependencyTestPom1)

        // Test
        val pomFile = mapToPomFile(file)

        // Verify
        val pomDocument = readXml(pomFile!!.file)
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()
        val dependencyVersionXpath = pomFile.pomDependencies?.get(0)?.versionXpath
        var versionNode = xPath.evaluate(dependencyVersionXpath, pomDocument, NODE) as Element?

        assertThat(versionNode?.textContent).isEqualTo("1.01-SNAPSHOT")

        // Update version
        versionNode?.firstChild?.nodeValue = "AANGEPASTE_VERSION"

        // Check of ie geupdate is
        versionNode = xPath.evaluate(dependencyVersionXpath, pomDocument, NODE) as Element?

        assertThat(versionNode?.textContent).isEqualTo("AANGEPASTE_VERSION")

    }
}